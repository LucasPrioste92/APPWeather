package com.lucasprioste.weatherapp.utils

import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

class ApiKeyInterceptor: Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val original: Request = chain.request()
        val originalHttpUrl: HttpUrl = original.url()

        val url = originalHttpUrl.newBuilder()
            .addQueryParameter("appid", API_KEY)
            .build()

        val request: Request = original
            .newBuilder()
            .url(url)
            .build()

        return chain.proceed(request)
    }
}