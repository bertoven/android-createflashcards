package com.example.bertoven.createflashcards.utils

import timber.log.Timber

class TimberDebugTreeImpl : Timber.DebugTree() {
    override fun createStackElementTag(element: StackTraceElement): String? {
        return "(${element.fileName}:${element.lineNumber})"
    }
}