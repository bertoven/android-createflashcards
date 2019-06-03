package com.example.bertoven.createflashcards.database

import android.app.SearchManager
import android.content.ContentResolver
import android.content.ContentUris
import android.net.Uri
import android.provider.BaseColumns

object DictionaryContract {
    internal const val TABLE_NAME = "DictionaryEntries"

    val CONTENT_URI = Uri.withAppendedPath(CONTENT_AUTHORITY_URI, TABLE_NAME)

    const val WORDS_MIME_TYPE = "${ContentResolver.CURSOR_DIR_BASE_TYPE}/vnd.$AUTHORITY.$TABLE_NAME"
    const val PATH_MIME_TYPE = "${ContentResolver.CURSOR_ITEM_BASE_TYPE}/vnd.$AUTHORITY.$TABLE_NAME"

    object Columns {
        const val ID = "rowid AS ${BaseColumns._ID}"
        const val KEY_WORD = SearchManager.SUGGEST_COLUMN_TEXT_1
        const val KEY_PATH = SearchManager.SUGGEST_COLUMN_INTENT_EXTRA_DATA
        const val INTENT_DATA_ID = "rowid AS ${SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID}"
    }

    fun getId(uri: Uri): Long {
        return ContentUris.parseId(uri)
    }
}