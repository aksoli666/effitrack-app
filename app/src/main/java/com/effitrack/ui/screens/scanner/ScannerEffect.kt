package com.effitrack.ui.screens.scanner

sealed interface ScannerEffect {
    data object PlayScanSound : ScannerEffect
    data class NavigateToDetails(val equipmentId: Long) : ScannerEffect
}