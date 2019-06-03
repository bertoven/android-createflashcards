package com.example.bertoven.createflashcards.di.module

import com.example.bertoven.createflashcards.data.entity.DefinitionsLexicalEntry
import com.example.bertoven.createflashcards.data.entity.InflectionsLexicalEntry
import com.example.bertoven.createflashcards.di.scope.PerApplication
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializer
import com.google.gson.reflect.TypeToken
import dagger.Module
import dagger.Provides
import org.json.JSONObject
import retrofit2.converter.gson.GsonConverterFactory

@Module
class GsonModule {

    @Provides
    @PerApplication
    internal fun provideGson(): Gson = Gson()

    @Provides
    @PerApplication
    internal fun provideGsonConverterFactory(inflectionsDeserializer: JsonDeserializer<ArrayList<InflectionsLexicalEntry>>,
                                             definitionsDeserializer: JsonDeserializer<ArrayList<DefinitionsLexicalEntry>>)
            : GsonConverterFactory {

        val gsonBuilder = GsonBuilder()

        val inflectionsType = object : TypeToken<ArrayList<InflectionsLexicalEntry>>() {}.type
        gsonBuilder.registerTypeAdapter(inflectionsType, inflectionsDeserializer)

        val definitionsType = object : TypeToken<ArrayList<DefinitionsLexicalEntry>>() {}.type
        gsonBuilder.registerTypeAdapter(definitionsType, definitionsDeserializer)

        val gson = gsonBuilder.create()
        return GsonConverterFactory.create(gson)
    }

    private inline fun <reified T> Gson.fromJson(json: String): T = this.fromJson<T>(json, object : TypeToken<T>() {}.type)

    @Provides
    @PerApplication
    internal fun provideInflectionsJsonDeserializer(gson: Gson): JsonDeserializer<ArrayList<InflectionsLexicalEntry>> {
        return JsonDeserializer { json, _, _ ->
            val jsonObject = JSONObject(json.toString())
            val jsonArray = jsonObject.getJSONArray("results").getJSONObject(0).getJSONArray("lexicalEntries")

            gson.fromJson<ArrayList<InflectionsLexicalEntry>>(jsonArray.toString())
        }
    }

    @Provides
    @PerApplication
    internal fun provideDefinitionsJsonDeserializer(gson: Gson): JsonDeserializer<ArrayList<DefinitionsLexicalEntry>> {
        return JsonDeserializer { json, _, _ ->
            val jsonObject = JSONObject(json.toString())
            val jsonArray = jsonObject.getJSONArray("results").getJSONObject(0).getJSONArray("lexicalEntries")

            gson.fromJson<ArrayList<DefinitionsLexicalEntry>>(jsonArray.toString())
        }
    }
}