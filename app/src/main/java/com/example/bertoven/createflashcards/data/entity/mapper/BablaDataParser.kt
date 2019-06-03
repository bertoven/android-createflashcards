package com.example.bertoven.createflashcards.data.entity.mapper

import com.example.bertoven.createflashcards.data.entity.*
import com.example.bertoven.createflashcards.di.scope.PerApplication
import io.reactivex.Single
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Node
import javax.inject.Inject

@PerApplication
class BablaDataParser @Inject constructor() {

    fun parse(data: String): Single<BablaTranslation> {
        var quickResultsEntries: ArrayList<QuickResultsEntry>? = null
        var translationDetails: ArrayList<TranslationDetails>? = null
        var synonyms: ArrayList<SynonymsEntry>? = null
        var contextTranslations: ArrayList<ContextTranslation>? = null

        try {
            val doc = Jsoup.parse(data)

            quickResultsEntries = getQuickResultsEntries(doc)

            if (quickResultsEntries != null) {
                translationDetails = getTranslationDetails(doc)
                synonyms = getSynonyms(doc)
            }

            contextTranslations = getContextTranslations(doc)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return Single.just(BablaTranslation(quickResultsEntries, translationDetails, synonyms, contextTranslations))
    }

    private fun getContextTranslations(doc: Document): ArrayList<ContextTranslation>? {
        val contextTranslationsElement = doc.getElementById("practicalexamples") ?: return null
        val contextTranslationsExamples = contextTranslationsElement.getElementsByClass("dict-example")
        val contextTranslations = ArrayList<ContextTranslation>()

        for (contextTranslationsExample in contextTranslationsExamples) {
            val wordWithContext = getTranslationDetailsExample(
                contextTranslationsExample.getElementsByClass("dict-source").first().childNodes()
            )
            val translation = getTranslationDetailsExample(
                contextTranslationsExample.getElementsByClass("dict-result").first().childNodes()
            )

            contextTranslations.add(ContextTranslation(wordWithContext, translation))
        }

        return contextTranslations
    }

    private fun getSynonyms(doc: Document): ArrayList<SynonymsEntry>? {
        val synonymsBaseElement = doc.getElementById("synonyms") ?: return null
        val synonymEntries = ArrayList<SynonymsEntry>()

        val synonymEntryElements = synonymsBaseElement.getElementsByClass("quick-result-entry")

        for (synonymEntryElement in synonymEntryElements) {
            val baseWord = synonymEntryElement.getElementsByClass("quick-result-option").text()
            val synonymsWordsElements = synonymEntryElement.getElementsByTag("ul").first().children()

            val synonymsWords = ArrayList<String>().apply {
                for (synonymsWordsElement in synonymsWordsElements) add(synonymsWordsElement.text())
            }

            synonymEntries.add(SynonymsEntry(baseWord, synonymsWords))
        }

        return synonymEntries
    }

    private fun getTranslationDetails(doc: Document): ArrayList<TranslationDetails> {
        val translationDetailsElements = doc.getElementsByAttributeValueStarting("name", "translationsdetails")
        val translationDetails = ArrayList<TranslationDetails>()

        for (translationDetailsElement in translationDetailsElements) {
            val translationDetailsElementHeader = translationDetailsElement.getElementsByClass("result-block-header")
                .first().child(0)
            val baseWord = translationDetailsElementHeader.ownText()
            val suffix = translationDetailsElementHeader.getElementsByClass("suffix").last()?.text()

            val senseGroupElements = translationDetailsElement.getElementsByClass("sense-group")
            val senseGroups = ArrayList<SenseGroup>()

            for (senseGroupElement in senseGroupElements) {
                val context = senseGroupElement.getElementsByClass("sense-group-header").getOrNull(0)?.text()

                val senseGroupEntryElements = senseGroupElement.getElementsByClass("dict-entry")
                val senseGroupEntries = ArrayList<SenseGroupEntry>()

                for (senseGroupEntryElement in senseGroupEntryElements) {
                    val baseWordSuffix = senseGroupEntryElement.getElementsByClass("dict-translation")
                        .first().getElementsByClass("dict-source")
                        .first().getElementsByClass("suffix").getOrNull(0)?.text()
                    val translation = senseGroupEntryElement.getElementsByTag("strong").last().text()

                    val exampleElements = senseGroupEntryElement.getElementsByClass("dict-example")
                    val examples = ArrayList<Pair<String, String>>()

                    for (exampleElement in exampleElements) {
                        val source = getTranslationDetailsExample(
                            exampleElement.getElementsByClass("dict-source").first().childNodes()
                        )
                        val result = getTranslationDetailsExample(
                            exampleElement.getElementsByClass("dict-result").first().childNodes()
                        )
                        examples.add(Pair(source, result))
                    }
                    senseGroupEntries.add(SenseGroupEntry("$baseWord ${baseWordSuffix
                        ?: ""}", translation, examples))
                }
                senseGroups.add(SenseGroup(context
                    ?: "", senseGroupEntries))
            }
            translationDetails.add(TranslationDetails("$baseWord ${suffix
                ?: ""}", senseGroups))
        }
        return translationDetails
    }

    private fun getTranslationDetailsExample(exampleElements: List<Node>): String {
        var i = exampleElements.size - 1
        while (i >= 0) {
            if (exampleElements[i].nodeName() != "#text" && exampleElements[i].nodeName() != "b") {
                exampleElements[i].remove()
            }
            --i
        }
        var text = ""
        for (exampleElement in exampleElements) {
            text += exampleElement.outerHtml()
        }
        return text.trim().replace("<[^>]*>".toRegex(), "")
    }

    private fun getQuickResultsEntries(doc: Document): ArrayList<QuickResultsEntry>? {
        val translationContents = doc.select("div.content-column > div.content").first()

        val quickResultEntryElements = translationContents.getElementsByClass("quick-result-entry")
        val overviewText = quickResultEntryElements.first().getElementsByClass("quick-result-overview")
            .text()
        val isTranslationAvailable = !(overviewText
            .contains("Nasz zespół został poinformowany o brakującym tłumaczeniu dla") ||
            overviewText.contains("Przejrzyj przykłady użycia poszukiwanego hasła"))

        val isContextAvailable = quickResultEntryElements.first().getElementsByClass("quick-result-option").size <= 0

        val quickResultsEntries = ArrayList<QuickResultsEntry>()

        if (isTranslationAvailable) {
            val size = quickResultEntryElements.size

            for (i in 0 until size - 1) {
                val baseWord = quickResultEntryElements[i].getElementsByClass("babQuickResult").text()
                val suffix = quickResultEntryElements[i].getElementsByClass("suffix").text()

                val translationElements = quickResultEntryElements[i].getElementsByTag("ul").first().children()
                val translationWords = ArrayList<String>()

                for (translationElement in translationElements) {
                    translationWords.add(translationElement.child(0).text())
                }

                quickResultsEntries.add(QuickResultsEntry("$baseWord $suffix", translationWords))
            }
        } else if (!isTranslationAvailable && isContextAvailable) {
            return null
        } else {
            throw IllegalStateException("Error: translation not exists")
        }
        return quickResultsEntries
    }
}