package com.effitrack.data.local

import android.content.Context
import android.content.SharedPreferences

object UserSession {
    private const val PREF_NAME = "effitrack_prefs"
    private const val KEY_TOKEN = "auth_token"
    private const val KEY_USER_ID = "user_id"

    private lateinit var prefs: SharedPreferences

    var token: String? = null
        private set
    var currentUserId: Long? = null
        private set

    val isLoggedIn: Boolean
        get() = !token.isNullOrEmpty() && currentUserId != null

    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        token = prefs.getString(KEY_TOKEN, null)
        val savedId = prefs.getLong(KEY_USER_ID, -1L)
        currentUserId = if (savedId != -1L) savedId else null
    }

    fun saveSession(newToken: String, userId: Long) {
        token = newToken
        currentUserId = userId
        prefs.edit()
            .putString(KEY_TOKEN, newToken)
            .putLong(KEY_USER_ID, userId)
            .apply()
    }

    fun clear() {
        token = null
        currentUserId = null
        prefs.edit().clear().apply()
    }
}
