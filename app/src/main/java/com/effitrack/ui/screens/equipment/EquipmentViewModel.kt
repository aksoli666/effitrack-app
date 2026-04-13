package com.effitrack.ui.screens.equipment

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.effitrack.data.local.UserSession
import com.effitrack.data.model.Equipment
import com.effitrack.data.model.EquipmentStatus
import com.effitrack.data.remote.RetrofitClient
import com.effitrack.util.Constants.EMPTY_STRING
import com.effitrack.util.Constants.ERR_LOGIN_FAILED
import com.effitrack.util.Constants.ERR_PREFIX
import com.effitrack.util.Constants.ERR_SEND_REPORT
import kotlinx.coroutines.launch

class EquipmentViewModel : ViewModel() {

    private var allEquipment = listOf<Equipment>()

    var displayedEquipment by mutableStateOf<List<Equipment>>(emptyList())
    var searchQuery by mutableStateOf(EMPTY_STRING)
    var selectedFilter by mutableStateOf<EquipmentStatus?>(null)
    var reportSentStatus by mutableStateOf<Boolean?>(null)

    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    var isSendingReport by mutableStateOf(false)
        private set

    fun loadEquipment() {
        val userId = UserSession.currentUserId ?: return
        isLoading = true

        viewModelScope.launch {
            val response = RetrofitClient.api.getUserEquipment(userId)

            if (response.isSuccessful && response.body() != null) {
                allEquipment = response.body()!!
                applyFilters()
            } else {
                errorMessage = "$ERR_PREFIX ${response.code()}"
            }
            isLoading = false
        }
    }

    fun onSearchQueryChange(query: String) {
        searchQuery = query
        applyFilters()
    }

    fun onFilterSelect(status: EquipmentStatus) {
        selectedFilter = if (selectedFilter == status) null else status
        applyFilters()
    }

    fun sendReport() {
        val userId = UserSession.currentUserId ?: return
        errorMessage = null
        reportSentStatus = null
        isSendingReport = true
        viewModelScope.launch {
            val response = RetrofitClient.api.sendEquipmentReport(userId)
            if (response.isSuccessful) {
                reportSentStatus = true
            } else {
                errorMessage = "${ERR_SEND_REPORT}: ${response.code()}"
                reportSentStatus = false
            }
            isSendingReport = false
        }
    }

    private fun applyFilters() {
        displayedEquipment = allEquipment.filter { equipment ->
            val matchesSearch = equipment.name.contains(searchQuery, ignoreCase = true) ||
                    equipment.inventoryNumber.contains(searchQuery, ignoreCase = true)

            val matchesFilter = when (selectedFilter) {
                null -> true
                else -> equipment.status == selectedFilter
            }

            matchesSearch && matchesFilter
        }
    }

    fun clearReportStatus() {
        reportSentStatus = null
        errorMessage = null
    }
}