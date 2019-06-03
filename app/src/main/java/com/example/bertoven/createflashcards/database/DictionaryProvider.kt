package com.example.bertoven.createflashcards.database

import android.app.SearchManager
import android.content.ContentProvider
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteQueryBuilder
import android.net.Uri
import timber.log.Timber

const val AUTHORITY = "com.example.bertoven.createflashcards.database.DictionaryProvider"

private const val SEARCH_WORDS = 0
private const val GET_WORD = 1

private const val SEARCH_SUGGEST = 2

//private const val QUERY_ORDER = "${SuggestionsContract.Columns.MATCHES_LENGTH}, ${SuggestionsContract.Columns.TEXT_LENGTH}"
private const val QUERY_ORDER = "length(${DictionaryContract.Columns.KEY_WORD})"
private const val QUERY_LIMIT = "20"

val CONTENT_AUTHORITY_URI: Uri = Uri.parse("content://$AUTHORITY")

class DictionaryProvider : ContentProvider() {

    private val uriMatcher by lazy { buildUriMatcher() }

    private fun buildUriMatcher(): UriMatcher {
        val matcher = UriMatcher(UriMatcher.NO_MATCH)

        matcher.addURI(AUTHORITY, DictionaryContract.TABLE_NAME, SEARCH_WORDS)
        matcher.addURI(AUTHORITY, "${DictionaryContract.TABLE_NAME}/#", GET_WORD)

        matcher.addURI(AUTHORITY, SearchManager.SUGGEST_URI_PATH_QUERY, SEARCH_SUGGEST)
        matcher.addURI(AUTHORITY, "${SearchManager.SUGGEST_URI_PATH_QUERY}/*", SEARCH_SUGGEST)

        return matcher
    }

    override fun onCreate(): Boolean {
        return true
    }

    override fun getType(uri: Uri): String {
        val matcher = uriMatcher.match(uri)

        return when (matcher) {
            SEARCH_WORDS -> DictionaryContract.WORDS_MIME_TYPE

            GET_WORD -> DictionaryContract.PATH_MIME_TYPE

            SEARCH_SUGGEST -> SearchManager.SUGGEST_MIME_TYPE

            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }
    }

    override fun query(uri: Uri, projection: Array<out String>?, selection: String?, selectionArgs: Array<out String>?, sortOrder: String?): Cursor? {
        when (uriMatcher.match(uri)) {
            SEARCH_SUGGEST -> {
                if (selectionArgs == null) {
                    throw IllegalArgumentException(
                        "selectionArgs must be provided for the URI: $uri"
                    )
                }
                return getSuggestions(selectionArgs[0].toLowerCase())
            }

            SEARCH_WORDS -> {
                if (selectionArgs == null) {
                    return searchWords(null)
                }
                return searchWords(selectionArgs[0].toLowerCase())
            }

            GET_WORD -> {
                return getWord(uri)
            }

            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }
    }

    private fun getSuggestions(query: String): Cursor? {
//        val projection = arrayOf(
//                "${DictionaryContract.TABLE_NAME}.${DictionaryContract.Columns.ID}",
//                DictionaryContract.Columns.KEY_WORD,
//                DictionaryContract.Columns.KEY_PATH,
//                "${DictionaryContract.TABLE_NAME}.${DictionaryContract.Columns.INTENT_DATA_ID}"
//        )

        val projection = arrayOf(
            DictionaryContract.Columns.ID,
            DictionaryContract.Columns.KEY_WORD,
            DictionaryContract.Columns.KEY_PATH,
            DictionaryContract.Columns.INTENT_DATA_ID,
            "length(${DictionaryContract.Columns.KEY_WORD})"
        )

        val selection = "${DictionaryContract.Columns.KEY_WORD} MATCH ?"
        val selectionArgs = arrayOf("\"$query*\"")

//        val db = DictionaryDatabase.getInstance(senseGroupContext).writableDatabase

//        createSuggestionsTable(db, selection, selectionArgs)

//        val queryBuilder = SQLiteQueryBuilder()

//        queryBuilder.tables = """${DictionaryContract.TABLE_NAME} JOIN ${SuggestionsContract.TABLE_NAME}
//            ON (${BaseColumns._ID} = ${SuggestionsContract.Columns.WORD_ID})""".replaceIndent(" ")

        return queryDatabase(projection, selection, selectionArgs, QUERY_ORDER, QUERY_LIMIT)

//        val cursor = queryBuilder.query(db, projection, selection, selectionArgs, null, null, QUERY_ORDER, QUERY_LIMIT)
//
//        if (cursor != null && !cursor.moveToNext()) {
//            return null
//        }
//        return cursor
    }

    private fun searchWords(query: String?): Cursor? {
        val projection = arrayOf(DictionaryContract.Columns.KEY_WORD,
            DictionaryContract.Columns.KEY_PATH)

        var selection: String? = null
        var selectionArgs: Array<String>? = null

        if (query != null) {
            selection = "${DictionaryContract.Columns.KEY_WORD} MATCH ?"
            selectionArgs = arrayOf("\"$query*\"")
        }

        return queryDatabase(projection, selection, selectionArgs, null, null)
    }

    private fun getWord(uri: Uri): Cursor? {
        val projection = arrayOf(DictionaryContract.Columns.KEY_WORD,
            DictionaryContract.Columns.KEY_PATH)

        val selection = "${DictionaryContract.Columns.ID} = ?"
        val selectionArgs = arrayOf(DictionaryContract.getId(uri).toString())

        return queryDatabase(projection, selection, selectionArgs, null, null)
    }

    private fun queryDatabase(projection: Array<String>, selection: String?, selectionArgs: Array<String>?, sortOrder: String?, limit: String?): Cursor? {
        val queryBuilder = SQLiteQueryBuilder()

        val db = DictionaryDatabase.getInstance(context).readableDatabase

        queryBuilder.tables = DictionaryContract.TABLE_NAME

        val cursor = queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder, limit)

        if (cursor != null && !cursor.moveToNext()) {
            return null
        }
        return cursor
    }

    private fun createSuggestionsTable(db: SQLiteDatabase, selection: String, selectionArgs: Array<String>) {
        db.execSQL("DROP TABLE IF EXISTS ${SuggestionsContract.TABLE_NAME}")

        val sSQL = """CREATE TABLE ${SuggestionsContract.TABLE_NAME} (
            ${SuggestionsContract.Columns.TEXT_LENGTH} INTEGER,
            ${SuggestionsContract.Columns.MATCHES_LENGTH} INTEGER,
            ${SuggestionsContract.Columns.WORD_ID} INTEGER
        )""".replaceIndent(" ")

        db.execSQL(sSQL)

        val suggestionsCursor = db.query(
            DictionaryContract.TABLE_NAME,
            arrayOf("snippet(${DictionaryContract.TABLE_NAME}, '#', '', '')", DictionaryContract.Columns.ID),
            selection,
            selectionArgs,
            null, null, null
        )

        suggestionsCursor.use {
            val statement = db.compileStatement(
                """INSERT INTO ${SuggestionsContract.TABLE_NAME} (
                        ${SuggestionsContract.Columns.TEXT_LENGTH},
                        ${SuggestionsContract.Columns.MATCHES_LENGTH},
                        ${SuggestionsContract.Columns.WORD_ID}
                    ) VALUES (?, ?, ?)""".replaceIndent(" ")
            )
            db.beginTransaction()

            while (it.moveToNext()) {
                val text = it.getString(0)
                val wordId = it.getLong(1)

                val regex = "#\\p{L}+".toRegex()
                val phraseMatches = regex.findAll(text)
                val phraseMatchesCount = phraseMatches.count()
                val phraseMatchesIterator = phraseMatches.iterator()

                var phraseMatchesCharacterCount = 0

                while (phraseMatchesIterator.hasNext()) {
                    // phrase match length - "#"
                    phraseMatchesCharacterCount += phraseMatchesIterator.next().value.length - 1
                }
                // text length - phrase matches length - all "#"
                val textWithoutMatchesLength = text.length - phraseMatchesCharacterCount - phraseMatchesCount

                statement.bindLong(1, textWithoutMatchesLength.toLong())
                statement.bindLong(2, phraseMatchesCharacterCount.toLong())
                statement.bindLong(3, wordId)

                val id = statement.executeInsert()
                statement.clearBindings()

                if (id < 0) {
                    Timber.e("Unable to add suggestion: $text")
                }
            }
            db.setTransactionSuccessful()
            db.endTransaction()
        }
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri {
        throw UnsupportedOperationException()
    }

    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<out String>?): Int {
        throw UnsupportedOperationException()
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        throw UnsupportedOperationException()
    }
}