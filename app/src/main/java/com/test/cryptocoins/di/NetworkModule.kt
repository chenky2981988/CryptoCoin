package com.test.cryptocoins.di

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.test.cryptocoins.BuildConfig
import com.test.cryptocoins.common.NetworkUtils
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton


/**
 * Created by Chirag Sidhiwala on 04/04/24.
 */
@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {

    @Provides
    @Named(NetworkUtils.GSON)
    fun provideGson(): Gson = GsonBuilder().create()

    @Provides
    @Named(NetworkUtils.GSON_CONVERTER)
    fun provideGsonConverterFactory(@Named(NetworkUtils.GSON) gson: Gson): GsonConverterFactory =
        GsonConverterFactory.create(gson)

    @Provides
    @Singleton
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor = HttpLoggingInterceptor().apply {
        level =
            if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
    }

    @Provides
    @Singleton
    @Named(NetworkUtils.OK_HTTP_CLIENT)
    fun provideOkHttpClient(
        httpLoggingInterceptor: HttpLoggingInterceptor,
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(NetworkUtils.CONNECTION_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(NetworkUtils.READ_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(NetworkUtils.WRITE_TIMEOUT, TimeUnit.SECONDS)
            .addInterceptor(httpLoggingInterceptor)
            .followSslRedirects(true).followRedirects(true).protocols(listOf(Protocol.HTTP_1_1))
            .build()
    }

    @Provides
    @Singleton
    @Named(NetworkUtils.RETROFIT_BUILDER)
    fun provideBaseRetrofitBuilder(
        @Named(NetworkUtils.OK_HTTP_CLIENT) client: OkHttpClient,
        @Named(NetworkUtils.GSON_CONVERTER) gsonConverter: GsonConverterFactory,
    ): Retrofit.Builder {
        return Retrofit.Builder().addConverterFactory(gsonConverter)
            .client(client)
    }
}