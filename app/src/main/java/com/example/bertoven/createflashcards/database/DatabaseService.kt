package com.example.bertoven.createflashcards.database

import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import com.example.bertoven.createflashcards.R
import timber.log.Timber
import java.io.IOException
import android.app.IntentService
import android.content.Context
import com.example.bertoven.createflashcards.presentation.view.activity.LINES_EXTRA
import com.example.bertoven.createflashcards.presentation.view.activity.SHARED_PREFS_NAME
import com.example.bertoven.createflashcards.presentation.view.activity.SHARED_PREFS_SERVICE_COMPLETED

class DatabaseService : IntentService(DatabaseService::class.java.simpleName) {

    private lateinit var mDatabase: SQLiteDatabase

    private var readLines: Int = 0

    override fun onHandleIntent(intent: Intent?) {
        isServiceRunning = true

        mDatabase = DictionaryDatabase.getInstance(this).writableDatabase

        try {
            readLines = intent?.getIntExtra(LINES_EXTRA, 0) ?: 0
            loadWords()
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
    }

    @Throws(IOException::class)
    private fun loadWords() {
        val resources = this.resources
        val inputStream = resources.openRawResource(R.raw.dictionary)

        inputStream.bufferedReader().use {
            val statement = mDatabase.compileStatement(
                """INSERT INTO ${DictionaryContract.TABLE_NAME} (
                        ${DictionaryContract.Columns.KEY_WORD},
                        ${DictionaryContract.Columns.KEY_PATH}
                    ) VALUES (?, ?)""".replaceIndent(" ")
            )

            var currentLineNumber = 0

            var line = it.readLine()
            mDatabase.beginTransaction()

            while (line != null) {
                if (currentLineNumber >= readLines) {
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

                readLines = ++currentLineNumber
            }
            mDatabase.setTransactionSuccessful()
            mDatabase.endTransaction()
        }
        Timber.d("loadWords(): done loading words to database")

        val prefs = getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE)

        prefs.edit().apply {
            putBoolean(SHARED_PREFS_SERVICE_COMPLETED, true)
            apply()
        }
    }

    companion object {
        var isServiceRunning = false
            private set(value) {
                field = value
            }
    }
}