package com.example.bertoven.createflashcards.ext

import android.content.ComponentName
import android.content.Intent

fun createGoogleTranslateIntent(text: String, baseLang: String = "en", targetLang: String = "pl")
    : Intent {

    return Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, text)
        putExtra("key_text_input", text)
        putExtra("key_text_output", "")
        putExtra("key_language_from", baseLang)
        putExtra("key_language_to", targetLang)
        putExtra("key_suggest_translation", "")
        putExtra("key_from_floating_window", false)
        component = ComponentName(
            "com.google.android.apps.translate",
            "com.google.android.apps.translate.TranslateActivity"
        )
    }
}