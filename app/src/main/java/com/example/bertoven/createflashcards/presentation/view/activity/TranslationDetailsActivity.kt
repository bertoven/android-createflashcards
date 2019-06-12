package com.example.bertoven.createflashcards.presentation.view.activity

import android.Manifest
import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.speech.tts.TextToSpeech
import android.text.InputType
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
import com.example.bertoven.createflashcards.BaseApplication
import com.example.bertoven.createflashcards.R
import com.example.bertoven.createflashcards.data.entity.DefinitionsLexicalEntry
import com.example.bertoven.createflashcards.data.entity.ImagesData
import com.example.bertoven.createflashcards.data.entity.QuickResultsEntry
import com.example.bertoven.createflashcards.database.DatabaseService
import com.example.bertoven.createflashcards.di.component.ActivityComponent
import com.example.bertoven.createflashcards.di.component.ApplicationComponent
import com.example.bertoven.createflashcards.di.component.DaggerActivityComponent
import com.example.bertoven.createflashcards.di.module.ActivityModule
import com.example.bertoven.createflashcards.domain.Translation
import com.example.bertoven.createflashcards.ext.AnkiDroidConfig
import com.example.bertoven.createflashcards.ext.AnkiDroidHelper
import com.example.bertoven.createflashcards.ext.dpToPx
import com.example.bertoven.createflashcards.presentation.presenter.TranslationDetailsPresenter
import com.example.bertoven.createflashcards.presentation.view.TranslationDetailsView
import com.example.bertoven.createflashcards.presentation.view.adapter.TranslationPagerAdapter
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_translation_details.*
import kotlinx.android.synthetic.main.content_translation_details.*
import java.io.File
import java.util.*
import javax.inject.Inject

const val PHRASE_EXTRA = "phrase"
const val TRANSLATION_EXTRA = "translation"
const val LINES_EXTRA = "readLines"

const val SHARED_PREFS_NAME = "sharedPrefs"
private const val SHARED_PREFS_TRANSLATION = "sharedTranslation"
const val SHARED_PREFS_SERVICE_COMPLETED = "sharedServiceCompleted"

const val ACTION_SHOW_TRANSLATION = "showTranslation"

private const val FILE_ALREADY_EXISTS = -2

class
TranslationDetailsActivity : AppCompatActivity(), TranslationDetailsView {

    interface OnFabClickListener {
        fun onFabClicked()
    }

    @Inject
    lateinit var translationDetailsPresenter: TranslationDetailsPresenter

    @Inject
    lateinit var gson: Gson

    @Inject
    lateinit var ankiDroidHelper: AnkiDroidHelper

    private lateinit var activityComponent: ActivityComponent
    private var mTranslation: Translation? = null
    private var tts: TextToSpeech? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_translation_details)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        val prefs = getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE)
        val isServiceCompleted = prefs.getBoolean(SHARED_PREFS_SERVICE_COMPLETED, false)

        if (!isServiceCompleted && !DatabaseService.isServiceRunning) {
            startService(Intent(this, DatabaseService::class.java))
        }

        initializeTts()
        initializeInjector()
        activityComponent.inject(this)
        translationDetailsPresenter.setView(this)
        handleIntent(intent)
    }

    private fun initializeTts() {
        tts = TextToSpeech(this, TextToSpeech.OnInitListener { status ->
            if (status != TextToSpeech.ERROR) {
                tts?.language = Locale.UK
            }
        })
    }

    private fun getApplicationComponent(): ApplicationComponent {
        return (application as BaseApplication).getApplicationComponent()
    }

    fun getActivityComponent() = activityComponent

    private fun initializeInjector() {
        activityComponent = DaggerActivityComponent.builder()
            .activityModule(ActivityModule(this))
            .applicationComponent(getApplicationComponent())
            .build()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_translation, menu)
        menu?.findItem(R.id.translation_add)?.isVisible = mTranslation?.quickResultsEntries != null
        menu?.findItem(R.id.translation_add_with)?.isVisible =
            mTranslation?.quickResultsEntries != null
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.translation_add -> {
                if (checkAnkiPermissions() && isStoragePermissionGranted(RC_WRITE_EXTERNAL_DATA) &&
                    mTranslation?.quickResultsEntries != null) {

                    addCardToAnkiDroid(getCardData("-"))
                }
                true
            }
            R.id.translation_add_with -> {
                if (checkAnkiPermissions() && isStoragePermissionGranted(
                        RC_WRITE_EXTERNAL_DATA_WITH_CUSTOM_TEXT
                    ) && mTranslation?.quickResultsEntries != null) {

                    showAddWithDialog()
                }
                true
            }
            R.id.translation_search -> {
                startActivity(Intent(this, SearchActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun checkAnkiPermissions(): Boolean {
        return if (ankiDroidHelper.shouldRequestPermission()) {
            ankiDroidHelper.requestPermission(this, RC_ANKI_PERM_REQUEST)
            false
        } else {
            true
        }
    }

    private fun showAddWithDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Wpisz wyrażenie, które chcesz dodać do karty.")

        // Set up the input
        val input = EditText(this)
        input.setSingleLine()
        val container = FrameLayout(this)
        val params = FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
        )
        params.marginStart = dpToPx(this, 16)
        params.marginEnd = dpToPx(this, 16)
        input.layoutParams = params
        container.addView(input)

        // Specify the type of input expected
        input.inputType = InputType.TYPE_CLASS_TEXT
        builder.setView(container)

        // Set up the buttons
        builder.setPositiveButton("OK") { _, _ ->
            addCardToAnkiDroid(getCardData(input.text.toString()))
        }
        builder.setNegativeButton("Anuluj") { dialog, _ -> dialog.cancel() }

        builder.show()
    }

    override fun onPause() {
        if (tts != null) {
            tts?.stop()
            tts?.shutdown()
        }
        super.onPause()
    }

    override fun onStop() {
        translationDetailsPresenter.unsubscribe()

        val prefs = getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE)

        prefs.edit()
            .apply {
                if (mTranslation != null) {
                    val translation = gson.toJson(mTranslation)
                    putString(SHARED_PREFS_TRANSLATION, translation)
                }
                apply()
            }

        super.onStop()
    }

    override fun hideProgressBar() {
        progressBar.visibility = View.GONE
    }

    override fun showTranslation(translation: Translation) {
        hideProgressBar()
        layoutStub.layoutResource = R.layout.content_translation_details
        layoutStub.inflate()

        mTranslation = translation
        invalidateOptionsMenu()

        val itemCount = getPageCount(translation)
        val pagerAdapter =
            TranslationPagerAdapter(this, itemCount, translation, supportFragmentManager)
        viewPager.adapter = pagerAdapter
        tabLayout.setupWithViewPager(viewPager)
        fab.setOnClickListener {
            val currentFragment = pagerAdapter.currentFragment
            if (currentFragment is OnFabClickListener) {
                currentFragment.onFabClicked()
            }
        }
        for (i in 0..tabLayout.tabCount) {
            tabLayout.getTabAt(i)?.customView = pagerAdapter.getTabView(this, i)
        }
        tabLayout.visibility = View.VISIBLE
    }

    override fun showNoTranslation() {
        hideProgressBar()
        invalidateOptionsMenu()
        layoutStub.layoutResource = R.layout.content_translation_details_empty
        layoutStub.inflate()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        if ((requestCode == RC_ANKI_PERM_REQUEST ||
                requestCode == RC_ANKI_PERM_REQUEST_WITH_TEXT) &&
            grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            if (requestCode == RC_ANKI_PERM_REQUEST) {
                isStoragePermissionGranted(RC_WRITE_EXTERNAL_DATA)
            } else {
                isStoragePermissionGranted(RC_WRITE_EXTERNAL_DATA_WITH_CUSTOM_TEXT)
            }
        } else if ((requestCode == RC_WRITE_EXTERNAL_DATA ||
                requestCode == RC_WRITE_EXTERNAL_DATA_WITH_CUSTOM_TEXT) &&
            grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            if (requestCode == RC_WRITE_EXTERNAL_DATA) {
                addCardToAnkiDroid(getCardData("-"))
            } else {
                showAddWithDialog()
            }
        } else {
            Toast.makeText(
                this,
                R.string.permission_denied, Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun getCardData(extra: String): HashMap<String, String> = hashMapOf(
        "expression" to mTranslation!!.translatingPhrase,
        "meaning" to getQuickResultsHtml(
            listOf(
                listOf(QuickResultsEntry("Moje", arrayListOf(extra))),
                mTranslation!!.quickResultsEntries!!
            ).flatten()
        ),
        "definitions" to getDefinitionsHtml(mTranslation!!.definitions),
        "images" to getImagesHtml(mTranslation!!.imagesData)
    )

    private fun getQuickResultsHtml(quickResultsEntries: List<QuickResultsEntry>?): String {
        if (quickResultsEntries == null) {
            return "brak tłumaczeń"
        }
        var str = "<table>"
        for (entry in quickResultsEntries) {
            str += "<tr><td>${entry.baseWord}</td><td>"
            for (i in entry.translations.indices) {
                str += "${entry.translations[i]}${if (i < entry.translations.size - 1) ", " else ""}"
            }
            str += "</td></tr>"
        }
        str += "</table>"
        return str
    }

    private fun getImagesHtml(imagesData: ImagesData?): String {
        if (imagesData == null) {
            return ""
        }
        var str = ""
        for (item in imagesData.items) {
            str += "<img src=\"${item.link}\">"
        }
        return str
    }

    private fun getDefinitionsHtml(definitions: ArrayList<DefinitionsLexicalEntry>?): String {
        if (definitions == null) {
            return "brak definicji"
        }
        var str = "<table>"
        for (lexicalEntry in definitions) {
            str += "<tr><th>${lexicalEntry.lexicalCategory}</th></tr>"
            for (entry in lexicalEntry.entries) {
                if (entry.senses == null) {
                    continue
                }
                for (sense in entry.senses) {
                    if (sense.definitions != null) {
                        str += "<tr><td>"
                        for (definition in sense.definitions) {
                            str += "<span class=definition>$definition</span><br>"
                        }
                        if (sense.examples != null) {
                            for (example in sense.examples) {
                                str += "<span class=example>${example.text}</span><br>"
                            }
                        }
                        str += "</td></tr>"
                    }
                }
            }
        }
        str += "</table>"
        return str
    }

    private fun isStoragePermissionGranted(requestCode: Int): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                true
            } else {
                ActivityCompat.requestPermissions(
                    this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), requestCode
                )
                false
            }
        } else {
            true
        }
    }

    private fun createAudioFileFromText(text: String): Int {
        val fileName = text.replace(" ", "_")
        val extStorageDir = Environment.getExternalStorageDirectory().path
        val destFileName = "$extStorageDir/AnkiDroid/collection.media/$fileName.mp3"
        val file = File(destFileName)
        if (file.exists()) {
            return FILE_ALREADY_EXISTS
        }
        val params = Bundle().apply { putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, text) }
        return tts?.synthesizeToFile(text, params, File(destFileName), text) ?: TextToSpeech.ERROR
    }

    private fun getPageCount(translation: Translation): Int {
        with(translation) {
            return if (quickResultsEntries != null && definitions != null && contextTranslations != null) 3
            else if (quickResultsEntries != null && definitions != null || quickResultsEntries != null && contextTranslations != null) 2
            else {
                1
            }
        }
    }

    private fun handleIntent(intent: Intent) {
        when (intent.action) {
            Intent.ACTION_SEARCH -> {
                val phrase = intent.getStringExtra(SearchManager.QUERY)
                translationDetailsPresenter.getTranslationData(phrase)
            }
            Intent.ACTION_VIEW -> {
                val data = intent.data
                val urlPathSuffix = data?.getQueryParameter("phrase") ?: intent.getStringExtra(
                    SearchManager.EXTRA_DATA_KEY
                )
                val phrase = urlPathSuffix.replace("-", " ")

                translationDetailsPresenter.getTranslationData(phrase)
            }
            Intent.ACTION_MAIN, null -> {
                val prefs = getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE)

                val translationJson = prefs.getString(SHARED_PREFS_TRANSLATION, "")

                val translation = gson.fromJson(translationJson, Translation::class.java)

                if (translation == null) {
                    showNoTranslation()
                } else {
                    showTranslation(translation)
                }
            }
            ACTION_SHOW_TRANSLATION -> {
                val phrase = intent.getStringExtra(PHRASE_EXTRA)
                translationDetailsPresenter.getTranslationData(phrase)
            }
        }
    }

    /**
     * get the deck id
     * @return might be null if there was a problem
     */
    private fun getDeckId(): Long? {
        var did = ankiDroidHelper.findDeckIdByName(AnkiDroidConfig.DECK_NAME)
        if (did == null) {
            did = ankiDroidHelper.api.addNewDeck(AnkiDroidConfig.DECK_NAME)
            ankiDroidHelper.storeDeckReference(AnkiDroidConfig.DECK_NAME, did)
        }
        return did
    }

    /**
     * get model id
     * @return might be null if there was an error
     */
    private fun getModelId(): Long? {
        var mid = ankiDroidHelper.findModelIdByName(
            AnkiDroidConfig.MODEL_NAME, AnkiDroidConfig.FIELDS.size
        )
        if (mid == null) {
            mid = ankiDroidHelper.api.addNewCustomModel(
                AnkiDroidConfig.MODEL_NAME,
                AnkiDroidConfig.FIELDS,
                AnkiDroidConfig.CARD_NAMES,
                AnkiDroidConfig.QFMT,
                AnkiDroidConfig.AFMT,
                AnkiDroidConfig.CSS,
                getDeckId(),
                null
            )
            ankiDroidHelper.storeModelReference(AnkiDroidConfig.MODEL_NAME, mid)
        }
        return mid
    }

    /**
     * Use the instant-add API to add flashcards directly to AnkiDroid.
     * @param card List of cards to be added. Each card has a HashMap of field name / field value pairs.
     */
    private fun addCardToAnkiDroid(card: HashMap<String, String>) {
        when (createAudioFileFromText(mTranslation!!.translatingPhrase)) {
            TextToSpeech.SUCCESS -> {
                Toast.makeText(
                    this, resources.getString(R.string.create_audio_success), Toast.LENGTH_LONG
                ).show()
                card["fileName"] = mTranslation!!.translatingPhrase.replace(" ", "_") + ".mp3"
            }
            FILE_ALREADY_EXISTS -> {
                Toast.makeText(
                    this,
                    resources.getString(R.string.create_audio_file_already_exists),
                    Toast.LENGTH_LONG
                ).show()
                card["fileName"] = mTranslation!!.translatingPhrase.replace(" ", "_") + ".mp3"
            }
            TextToSpeech.ERROR -> {
                Toast.makeText(
                    this, resources.getString(R.string.create_audio_fail), Toast.LENGTH_LONG
                ).show()
                card["fileName"] = ""
            }
        }
        val deckId = getDeckId()
        val modelId = getModelId()
        if (deckId == null || modelId == null) {
            // we had an API error, report failure and return
            Toast.makeText(this, resources.getString(R.string.card_add_fail), Toast.LENGTH_LONG)
                .show()
            return
        }
        val fieldNames = ankiDroidHelper.api.getFieldList(modelId)
        if (fieldNames == null) {
            // we had an API error, report failure and return
            Toast.makeText(this, resources.getString(R.string.card_add_fail), Toast.LENGTH_LONG)
                .show()
            return
        }
        // Build list of fields and tags
        val fields = LinkedList<Array<String>>()
        val tags = LinkedList<Set<String>>()

        // Build a field map accounting for the fact that the user could have changed the fields in the model
        val flds = arrayOfNulls<String>(fieldNames.size)
        for (i in flds.indices) {
            // Fill up the fields one-by-one until either all fields are filled or we run out of fields to send
            if (i < AnkiDroidConfig.FIELDS.size) {
                val field = card[AnkiDroidConfig.FIELDS[i]]
                if (field != null) {
                    flds[i] = field
                }
            }
        }
        tags.add(AnkiDroidConfig.TAGS)
        fields.add(flds.filterNotNull().toTypedArray())

        // Remove any duplicates from the LinkedLists and then add over the API
        ankiDroidHelper.removeDuplicates(fields, tags, modelId)
        val added = ankiDroidHelper.api.addNotes(modelId, deckId, fields, tags)
        if (added != 0) {
            Toast.makeText(
                this, resources.getString(R.string.n_items_added, added), Toast.LENGTH_LONG
            ).show()
        } else {
            // API indicates that a 0 return value is an error
            Toast.makeText(this, resources.getString(R.string.card_add_fail), Toast.LENGTH_LONG)
                .show()
        }
    }

    override fun setFabVisibility(visible: Boolean) {
        fab.isVisible = visible
    }

    companion object {
        private const val RC_ANKI_PERM_REQUEST = 9001
        private const val RC_ANKI_PERM_REQUEST_WITH_TEXT = 9002
        private const val RC_WRITE_EXTERNAL_DATA = 9003
        private const val RC_WRITE_EXTERNAL_DATA_WITH_CUSTOM_TEXT = 9004
    }
}