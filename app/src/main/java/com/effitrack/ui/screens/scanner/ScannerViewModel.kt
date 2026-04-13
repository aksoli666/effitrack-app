package com.effitrack.ui.screens.scanner

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.effitrack.data.local.UserSession
import com.effitrack.data.remote.RetrofitClient
import com.effitrack.util.Constants.EMPTY_STRING
import com.effitrack.util.Constants.ERR_PREFIX
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ScannerViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ScannerUiState())
    val uiState = _uiState.asStateFlow()

    private val _effect = Channel<ScannerEffect>()
    val effect = _effect.receiveAsFlow()

    fun onCodeScanned(code: String) {
        if (_uiState.value.isBottomSheetVisible) return

        viewModelScope.launch {
            _effect.send(ScannerEffect.PlayScanSound)

            _uiState.update {
                it.copy(
                    isBottomSheetVisible = true,
                    scannedCode = code,
                    isManualEntry = false,
                    error = null
                )
            }
        }
    }

    fun onManualEntryClicked() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isBottomSheetVisible = true,
                    scannedCode = EMPTY_STRING,
                    isManualEntry = true,
                    error = null
                )
            }
        }
    }

    fun onCodeChanged(newCode: String) {
        _uiState.update { it.copy(scannedCode = newCode, error = null) }
    }

    fun dismissBottomSheet() {
        if (_uiState.value.isLoading) return
        _uiState.update { it.copy(isBottomSheetVisible = false) }
    }

    fun onAddEquipment() {
        val code = _uiState.value.scannedCode
        val userId = UserSession.currentUserId

        if (code.isBlank() || userId == null) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            runCatching {
                val assignResponse = RetrofitClient.api.assignEquipmentToUser(
                    userId = userId,
                    inventoryNumber = code
                )

                if (!assignResponse.isSuccessful) {
                    throw Exception("$ERR_PREFIX${assignResponse.code()}")
                }

                val findResponse = RetrofitClient.api.findEquipmentByInv(inventoryNumber = code)

                if (findResponse.isSuccessful && findResponse.body() != null) {
                    findResponse.body()!!
                } else {
                    throw Exception(ERR_PREFIX)
                }
            }.onSuccess { equipment ->
                _uiState.update {
                    it.copy(isLoading = false, isBottomSheetVisible = false)
                }
                _effect.send(ScannerEffect.NavigateToDetails(equipment.id))

            }.onFailure { e ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.localizedMessage ?: ERR_PREFIX
                    )
                }
            }
        }
    }
}