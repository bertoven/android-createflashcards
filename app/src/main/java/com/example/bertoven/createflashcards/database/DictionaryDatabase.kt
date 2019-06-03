package com.example.bertoven.createflashcards.database

import android.annotation.SuppressLint
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.bertoven.createflashcards.R
import timber.log.Timber
import java.io.IOException

private const val DATABASE_NAME = "Dictionary.db"
private const val DATABASE_VERSION = 1

class DictionaryDatabase private constructor(private val mContext: Context)
    : SQLiteOpenHelper(mContext, DATABASE_NAME, null, DATABASE_VERSION) {

    private lateinit var mDatabase: SQLiteDatabase

    override fun onCreate(db: SQLiteDatabase) {
        mDatabase = db

        val sSQL = """CREATE VIRTUAL TABLE ${DictionaryContract.TABLE_NAME} USING fts4(
            ${DictionaryContract.Columns.KEY_WORD},
            ${DictionaryContract.Columns.KEY_PATH},
            matchinfo=fts3
        );""".replaceIndent(" ")

        mDatabase.execSQL(sSQL)
//        loadDictionary()
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS ${DictionaryContract.TABLE_NAME}")
        onCreate(db)
    }

    private fun loadDictionary() {
        Thread(Runnable {
            try {
                loadWords()
            } catch (e: IOException) {
                throw RuntimeException(e)
            }
        }).start()
    }

    @Throws(IOException::class)
    private fun loadWords() {
        val resources = mContext.resources
        val inputStream = resources.openRawResource(R.raw.dictionary)

        inputStream.bufferedReader().use {
            val statement = mDatabase.compileStatement(
                """INSERT INTO ${DictionaryContract.TABLE_NAME} (
                        ${DictionaryContract.Columns.KEY_WORD},
                        ${DictionaryContract.Columns.KEY_PATH}
                    ) VALUES (?, ?)""".replaceIndent(" ")
            )
            var line = it.readLine()
            mDatabase.beginTransaction()

            while (line != null) {
                val strings = line.split("|")
                if (strings.size < 2) {
                    line = it.readLine()
                    continue
                }
                statement.bindString(1, strings[0].trim())
                statement.bindString(2, strings[1].trim())

                val id = statement.executeInsert()
                statement.clearBindings()
                if (id < 0) {
                    Timber.e("Unable to add word: ${strings[0].trim()}")
                }
                line = it.readLine()
            }
            mDatabase.setTransactionSuccessful()
            mDatabase.endTransaction()
        }
        Timber.d("loadWords(): done loading words to database")
    }

    companion object {

        @SuppressLint("StaticFieldLeak")
        @Volatile
        private var instance: DictionaryDatabase? = null

        fun getInstance(context: Context): DictionaryDatabase =
            instance ?: synchronized(this) {
                instance
                    ?: DictionaryDatabase(context.applicationContext).also { instance = it }
            }
    }
}