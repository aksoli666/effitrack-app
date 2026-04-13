package com.effitrack.util

import android.util.Base64
import com.effitrack.util.Constants.DOT
import com.effitrack.util.Constants.PARAM_USER_ID
import org.json.JSONObject
import java.math.BigInteger.ONE

object JwtUtil {
    fun getUserId(token: String): Long? {
        val parts = token.split(DOT)
        if (parts.size < 2) return null

        val payload = String(Base64.decode(parts[ONE.toInt()], Base64.URL_SAFE), Charsets.UTF_8)
        val json = JSONObject(payload)

        return if (json.has(PARAM_USER_ID )) {
            json.getLong(PARAM_USER_ID )
        } else {
            null
        }
    }
}