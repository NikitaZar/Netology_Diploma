package ru.nikitazar.netology_diploma.api

import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import ru.nikitazar.netology_diploma.BuildConfig
import ru.nikitazar.netology_diploma.auth.AppAuth

fun loggingInterceptor() = HttpLoggingInterceptor()
    .apply {
        if (BuildConfig.DEBUG) {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

fun authInterceptor(auth: AppAuth) =
    fun(chain: Interceptor.Chain): Response {
        auth.authStateFlow.value.token?.let { token ->
            val newRequest = chain.request().newBuilder()
                .addHeader("Authorization", token)
                .build()
            return chain.proceed(newRequest)
        }

        return chain.proceed(chain.request())
    }