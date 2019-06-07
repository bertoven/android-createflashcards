package com.example.bertoven.createflashcards.di.module

import com.example.bertoven.createflashcards.BuildConfig
import com.example.bertoven.createflashcards.di.qualifier.BablaRetrofit
import com.example.bertoven.createflashcards.di.qualifier.OxfordRetrofit
import com.example.bertoven.createflashcards.di.scope.PerApplication
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import timber.log.Timber

@Module(includes = [GsonModule::class])
class NetworkModule {

    private fun getBablaBaseUrl() = BuildConfig.BASE_BABLA_URL
    private fun getOxfordBaseUrl() = BuildConfig.BASE_OXF_URL

    @Provides
    @PerApplication
    @BablaRetrofit
    internal fun provideBablaRetrofit(okHttpClient: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl(getBablaBaseUrl())
            .client(okHttpClient)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()

    @Provides
    @PerApplication
    @OxfordRetrofit
    internal fun provideOxfordRetrofit(okHttpClient: OkHttpClient,
                                       gsonConverterFactory: GsonConverterFactory): Retrofit =
        Retrofit.Builder()
            .baseUrl(getOxfordBaseUrl())
            .client(okHttpClient)
            .addConverterFactory(gsonConverterFactory)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()

    @Provides
    @PerApplication
    internal fun provideOkHttpClient(httpLoggingInterceptor: HttpLoggingInterceptor): OkHttpClient {
        val httpClientBuilder = OkHttpClient.Builder()
        if (BuildConfig.DEBUG) {
            httpClientBuilder.addInterceptor(httpLoggingInterceptor)
        }
        return httpClientBuilder.build()
    }

    @Provides
    @PerApplication
    internal fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor =
        HttpLoggingInterceptor {message ->
            Timber.d(message)
        }.setLevel(HttpLoggingInterceptor.Level.BODY)
}