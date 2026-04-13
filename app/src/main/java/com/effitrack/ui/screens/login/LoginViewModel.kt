package com.effitrack.ui.screens.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.effitrack.data.local.UserSession
import com.effitrack.data.model.LoginRequest
import com.effitrack.data.remote.RetrofitClient
import com.effitrack.util.Constants.EMPTY_STRING
import com.effitrack.util.Constants.ERR_EMPTY_FIELDS
import com.effitrack.util.Constants.ERR_ID_NOT_FOUND
import com.effitrack.util.Constants.ERR_LOGIN_FAILED
import com.effitrack.util.Constants.ERR_NETWORK
import com.effitrack.util.JwtUtil
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    var tableNumber by mutableStateOf(EMPTY_STRING)
    var pinCode by mutableStateOf(EMPTY_STRING)

    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)
    var loginSuccess by mutableStateOf(false)


    fun onLoginClick() {
        if (tableNumber.isBlank() || pinCode.isBlank()) {
            errorMessage = ERR_EMPTY_FIELDS
            return
        }

        isLoading = true
        errorMessage = null

        viewModelScope.launch {
            try {
                val request = LoginRequest(tableNumber, pinCode)
                val response = RetrofitClient.api.login(request)

                if (response.isSuccessful && response.body() != null) {
                    val tokenString = response.body()!!.string()
                    val userId = JwtUtil.getUserId(tokenString)

                    if (userId != null) {
                        UserSession.saveSession(tokenString, userId)
                        loginSuccess = true
                    } else {
                        errorMessage = ERR_ID_NOT_FOUND
                    }
                } else {
                    errorMessage = ERR_LOGIN_FAILED
                }
            } catch (e: Exception) {
                errorMessage = e.localizedMessage ?: ERR_NETWORK
            } finally {
                isLoading = false
            }
        }
    }
}