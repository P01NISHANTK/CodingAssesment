package com.discovery.codingassesment.module

import com.discovery.codingassesment.net.Api
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    companion object {
        private const val BASE_URL = "https://newsapi.org/"
    }

    @Provides
    @Singleton
    fun provideApiInterface(): Api {
        val retrofit = getRetrofit(getOkHttpClient())
        return retrofit.create(Api::class.java)
    }

    private fun getRetrofit(okHttpClient: OkHttpClient?): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient!!)
            .build()
    }

    private fun getOkHttpClient() : OkHttpClient {
        val okHttpClientBuilder = OkHttpClient.Builder()
        okHttpClientBuilder.addInterceptor(getHttpLoggingInterceptor())
        okHttpClientBuilder.hostnameVerifier{ _, _ -> true }
        okHttpClientBuilder.connectTimeout(20, TimeUnit.SECONDS)
        okHttpClientBuilder.readTimeout(20, TimeUnit.SECONDS)
        okHttpClientBuilder.writeTimeout(20, TimeUnit.SECONDS)
        return okHttpClientBuilder.build()
    }

    private fun getHttpLoggingInterceptor(): Interceptor {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        return interceptor
    }

}