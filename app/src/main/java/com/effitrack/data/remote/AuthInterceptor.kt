package com.effitrack.data.remote

import com.effitrack.data.local.UserSession
import com.effitrack.util.Constants.HEADER_AUTHORIZATION
import com.effitrack.util.Constants.TOKEN_PREFIX_BEARER
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val token = UserSession.token

        if (token.isNullOrBlank()) {
            return chain.proceed(originalRequest)
        }

        val newRequest = originalRequest.newBuilder()
            .header(HEADER_AUTHORIZATION, "${TOKEN_PREFIX_BEARER}$token")
            .build()

        return chain.proceed(newRequest)
    }
}