package com.effitrack.data.remote

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import com.effitrack.MainActivity
import com.effitrack.data.local.UserSession
import com.effitrack.util.Constants.EMPTY_STRING
import com.effitrack.util.Constants.HEADER_AUTHORIZATION
import com.effitrack.util.Constants.TOAST_LOGIN_AGAIN
import com.effitrack.util.Constants.TOKEN_PREFIX_BEARER
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val context: Context) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val token = UserSession.token

        val requestBuilder = originalRequest.newBuilder()
        if (!token.isNullOrBlank()) {
            requestBuilder.header(HEADER_AUTHORIZATION, "${TOKEN_PREFIX_BEARER}$token")
        }

        val response = try {
            chain.proceed(requestBuilder.build())
        } catch (e: java.io.IOException) {
            // Разделяем падение сети и выключенный сервер
            if (isNetworkAvailable()) {
                redirectToStatusScreen("Сервер тимчасово не працює. Спробуйте пізніше")
            } else {
                redirectToStatusScreen("З'єднання відсутнє, підключіться до мережі")
            }

            return okhttp3.Response.Builder()
                .request(requestBuilder.build())
                .protocol(okhttp3.Protocol.HTTP_1_1)
                .code(504)
                .message("No Internet Connection")
                .body(okhttp3.ResponseBody.create(null, EMPTY_STRING))
                .build()
        }

        if (response.code == 401 || response.code == 403) {
            UserSession.clear()

            Handler(Looper.getMainLooper()).post {
                Toast.makeText(context, TOAST_LOGIN_AGAIN, Toast.LENGTH_LONG).show()
            }

            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            context.startActivity(intent)
            return response
        }

        if (response.code >= 500) {
            redirectToStatusScreen("Сервер тимчасово не працює. Спробуйте пізніше")
        }

        return response
    }

    private fun redirectToStatusScreen(message: String) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            putExtra("OPEN_STATUS_SCREEN", true)
            putExtra("ERROR_MESSAGE", message)
        }
        context.startActivity(intent)
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
    }
}