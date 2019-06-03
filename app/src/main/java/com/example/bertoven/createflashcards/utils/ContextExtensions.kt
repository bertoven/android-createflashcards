package com.example.bertoven.createflashcards.utils

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper

fun Context.getActivity(): Activity? {
    var newContext = this
    while (newContext is ContextWrapper) {
        if (newContext is Activity) {
            return newContext
        }
        newContext = newContext.baseContext
    }
    return null
}
