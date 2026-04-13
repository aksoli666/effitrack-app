package com.effitrack.ui.screens.profile

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.effitrack.data.local.UserSession
import com.effitrack.data.model.UserProfile
import com.effitrack.data.remote.RetrofitClient
import com.effitrack.util.Constants.ERR_ID_NOT_FOUND
import com.effitrack.util.Constants.ERR_LOGIN_FAILED
import com.effitrack.util.Constants.ERR_NETWORK
import com.effitrack.util.Constants.ERR_PREFIX
import com.effitrack.util.Constants.ERR_SEND_REPORT
import com.effitrack.util.filterAndSortForUi
import com.effitrack.util.toUiModel
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel() {
    var userProfile by mutableStateOf<UserProfile?>(null)
    var isLoading by mutableStateOf(false)
    var isRefreshing by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)
    var reportSentStatus by mutableStateOf<Boolean?>(null)

    @RequiresApi(Build.VERSION_CODES.O)
    fun loadProfileData(isPullRefresh: Boolean = false) {
        if (userProfile != null && !isPullRefresh) {
            return
        }

        val userId = UserSession.currentUserId

        if (userId == null) {
            errorMessage = ERR_ID_NOT_FOUND
            isRefreshing = false
            return
        }

        if (isPullRefresh) {
            isRefreshing = true
        } else {
            isLoading = true
        }

        errorMessage = null

        viewModelScope.launch {
            try {
                val userDeferred = async { RetrofitClient.api.getUserProfile(userId) }
                val tasksDeferred = async { RetrofitClient.api.getUserTasks(userId) }

                val userResponse = userDeferred.await()
                val tasksResponse = tasksDeferred.await()

                if (userResponse.isSuccessful && tasksResponse.isSuccessful) {
                    val user = userResponse.body()!!
                    val rawTasks = tasksResponse.body() ?: emptyList()
                    val processedTasks = rawTasks.filterAndSortForUi()
                    userProfile = user.toUiModel(processedTasks)
                } else {
                    errorMessage = ERR_LOGIN_FAILED
                }
            } catch (e: Exception) {
                errorMessage = ERR_NETWORK + e.localizedMessage
            } finally {
                isLoading = false
                isRefreshing = false
            }
        }
    }

    fun sendReport() {
        val userId = UserSession.currentUserId ?: return
        isLoading = true
        errorMessage = null
        reportSentStatus = null

        viewModelScope.launch {
            try {
                val response = RetrofitClient.api.sendReport(userId)
                if (response.isSuccessful) {
                    reportSentStatus = true
                } else {
                    errorMessage = ERR_SEND_REPORT
                    reportSentStatus = false
                }
            } catch (e: Exception) {
                errorMessage = ERR_PREFIX + e.localizedMessage
                reportSentStatus = false
            } finally {
                isLoading = false
            }
        }
    }

    fun clearReportStatus() {
        reportSentStatus = null
        errorMessage = null
    }
}