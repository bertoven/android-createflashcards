package com.example.bertoven.createflashcards.ext

import timber.log.Timber

class TimberDebugTreeImpl : Timber.DebugTree() {
    override fun createStackElementTag(element: StackTraceElement): String? {
        return "(${element.fileName}:${element.lineNumber})"
    }
}