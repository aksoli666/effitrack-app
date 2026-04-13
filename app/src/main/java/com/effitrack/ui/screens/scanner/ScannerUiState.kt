package com.effitrack.ui.screens.scanner

import com.effitrack.util.Constants.EMPTY_STRING

data class ScannerUiState(
    val isBottomSheetVisible: Boolean = false,
    val scannedCode: String = EMPTY_STRING,
    val isManualEntry: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null
)