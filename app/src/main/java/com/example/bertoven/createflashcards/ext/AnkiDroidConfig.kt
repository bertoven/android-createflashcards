package com.example.bertoven.createflashcards.ext

import java.util.*

/** Some fields to store configuration details for AnkiDroid  */
internal object AnkiDroidConfig {
    // Name of deck which will be created in AnkiDroid
    val DECK_NAME = "CreateFlashcards"
    // Name of model which will be created in AnkiDroid
    val MODEL_NAME = "com.example.bertoven.createflashcards"
    // Optional space separated list of tags to add to every note
    val TAGS: Set<String> = HashSet(listOf("CreateFlashcards"))
    // List of field names that will be used in AnkiDroid model
    val FIELDS = arrayOf("expression", "meaning", "definitions", "images", "fileName")
    // List of card names that will be used in AnkiDroid (one for each direction of learning)
    val CARD_NAMES = arrayOf("English>Polish")
    // CSS to share between all the cards (optional)
    val CSS = ".card {\n" +
        " font-size: 24px;\n" +
        " text-align: center;\n" +
        " color: black;\n" +
        " background-color: white;\n" +
        " word-wrap: break-word;\n" +
        "}\n" +
        ".big { font-size: 48px; }\n" +
        ".small { font-size: 14px;}\n" +
        "table, td, th { border: 1px solid black; }\n" +
        "table { border-collapse: collapse; width: 100%; }\n" +
        "th, td { text-align: left; padding: 2px; }\n" +
        "th { font-weight: bold; }" +
        ".images { display: flex; flex-wrap: wrap; justify-content: space-evenly; }\n" +
        "img { width: auto; height: 100px; margin: 2px; }\n" +
        ".definition {  }\n" +
        ".example { color: gray; }\n" +
        ".link { position: relative; text-decoration: none; }" +
        ".link::after { content: ''; position: absolute; width: 100%; height: 0; left: 0; bottom: 8px; border-bottom: 2px solid #000; }" +
        "a:link, a:visited, a:hover, a:active { color: black; }"
    // Template for the question of each card
    val QFMT1 = "<div class=big>{{expression}}</div>"
    val QFMT = arrayOf(QFMT1)
    // Template for the answer (use identical for both sides)
    val AFMT1 =
        "<a class=\"big link\" href=\"http://www.example.com/create-flashcard?phrase={{expression}}\">{{expression}}</a><br>{{#fileName}}[sound:{{fileName}}]{{/fileName}}\n" +
            "<div class=small>{{meaning}}</div><br>\n" +
            "<div class=small>{{definitions}}</div><br>\n" +
            "{{#images}}<div class=images>{{images}}</div><br>{{/images}}"
    val AFMT = arrayOf(AFMT1)
    // Define two keys which will be used when using legacy ACTION_SEND intent
    val FRONT_SIDE_KEY = FIELDS[0]
    val BACK_SIDE_KEY = FIELDS[2]
}
