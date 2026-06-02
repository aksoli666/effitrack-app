package com.effitrack.ui.screens.equipment.details

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.effitrack.data.model.EquipmentDetailState
import com.effitrack.data.model.EquipmentStatus
import com.effitrack.data.model.EquipmentStatusUpdate
import com.effitrack.data.model.EquipmentUpdateRequest
import com.effitrack.data.model.HistoryItem
import com.effitrack.data.remote.RetrofitClient
import com.effitrack.util.Constants
import com.effitrack.util.Constants.DIRECT_LINK_BASE
import com.effitrack.util.Constants.DRIVE_HOST
import com.effitrack.util.Constants.ERR_NETWORK
import com.effitrack.util.Constants.ERR_PREFIX
import com.effitrack.util.Constants.LABEL_STATUS_DOWNTIME
import com.effitrack.util.Constants.LABEL_STATUS_RUNNING
import com.effitrack.util.Constants.LABEL_STATUS_SETUP
import com.effitrack.util.Constants.PATH_DELIMITER
import com.effitrack.util.Constants.PATH_ID_PREFIX
import com.effitrack.util.Constants.PATTERN_TIME
import com.effitrack.util.Constants.REASON_START_BY_OPERATOR
import com.effitrack.util.toFormattedWorkTime
import com.effitrack.util.toUiDate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
class EquipmentDetailViewModel(
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val equipmentId: Long =
        checkNotNull(savedStateHandle[Constants.PARAM_ID]).toString().toLong()

    private val _uiState = MutableStateFlow(EquipmentDetailState())
    val uiState: StateFlow<EquipmentDetailState> = _uiState.asStateFlow()

    init {
        loadEquipmentDetails()
    }

    private fun loadEquipmentDetails() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            runCatching {
                RetrofitClient.api.getEquipmentById(equipmentId)
            }.onSuccess { response ->
                if (response.isSuccessful && response.body() != null) {
                    val equipment = response.body()!!

                    val durationFormatted = equipment.currentStatusDuration.toFormattedWorkTime()
                    val startTimeFormatted = calculateStartTime(equipment.currentStatusDuration)

                    val totalShiftMinutes = equipment.workTimeTodayMinutes +
                            equipment.downtimeTodayMinutes +
                            equipment.setupTodayMinutes

                    val currentHistoryItem = HistoryItem(
                        time = startTimeFormatted,
                        status = getStatusLabel(equipment.status),
                        description = equipment.activeAction ?: Constants.EMPTY_STRING
                    )

                    _uiState.update { currentState ->
                        EquipmentDetailState(
                            isLoading = false,
                            equipment = equipment,
                            comment = currentState.comment.ifEmpty {
                                equipment.operatorComment ?: Constants.EMPTY_STRING
                            },
                            isAiGenerating = false,
                            workTime = totalShiftMinutes.toFormattedWorkTime(),
                            totalDowntime = equipment.downtimeTodayMinutes.toFormattedWorkTime(),
                            setupTime = equipment.setupTodayMinutes.toFormattedWorkTime(),
                            duration = durationFormatted,
                            currentDowntimeDuration = durationFormatted,
                            downtimeStartTime = startTimeFormatted,
                            lastMaintenance = equipment.lastMaintenance.toUiDate(),
                            nextMaintenance = equipment.nextMaintenance.toUiDate(),
                            history = listOf(currentHistoryItem)
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(isLoading = false, error = "${ERR_PREFIX}${response.code()}")
                    }
                }
            }.onFailure { e ->
                _uiState.update {
                    it.copy(isLoading = false, error = e.localizedMessage ?: ERR_NETWORK)
                }
            }
        }
    }

    fun updateCommentLocal(newComment: String) {
        _uiState.update { it.copy(comment = newComment) }
    }

    fun saveCommentChanges() {
        val currentUiComment = _uiState.value.comment

        viewModelScope.launch(Dispatchers.IO) {
            runCatching {
                val request = EquipmentUpdateRequest(operatorComment = currentUiComment)
                RetrofitClient.api.updateEquipmentDetails(equipmentId, request)
            }.onSuccess { response ->
                if (response.isSuccessful && response.body() != null) {
                    val updatedEq = response.body()!!

                    _uiState.update { currentState ->
                        val eqWithPreservedAi = updatedEq.copy(
                            aiAnalysis = currentState.equipment?.aiAnalysis
                        )

                        currentState.copy(
                            equipment = eqWithPreservedAi,
                            comment = updatedEq.operatorComment ?: Constants.EMPTY_STRING
                        )
                    }
                }
            }.onFailure { e ->
                _uiState.update { it.copy(error = e.localizedMessage) }
            }
        }
    }

    fun onCommentFocusLost() {
        val currentUiComment = _uiState.value.comment
        val originalComment = _uiState.value.equipment?.operatorComment ?: Constants.EMPTY_STRING

        if (currentUiComment != originalComment) {
            saveCommentChanges()
        }
    }

    fun triggerAiAnalysis() {
        if (_uiState.value.isAiGenerating) return

        _uiState.update { it.copy(isAiGenerating = true) }

        viewModelScope.launch(Dispatchers.IO) {
            runCatching {
                RetrofitClient.api.triggerAiAnalysis(equipmentId)
            }.onSuccess { response ->
                if (response.isSuccessful && response.body() != null) {
                    val updatedEq = response.body()!!
                    _uiState.update { currentState ->
                        currentState.copy(
                            equipment = updatedEq,
                            isAiGenerating = false
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(isAiGenerating = false, error = response.message())
                    }
                }
            }.onFailure { e ->
                _uiState.update {
                    it.copy(isAiGenerating = false, error = e.localizedMessage)
                }
            }
        }
    }

    fun finishDowntime() {
        val request = EquipmentStatusUpdate(
            status = EquipmentStatus.RUNNING,
            reason = REASON_START_BY_OPERATOR
        )

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            runCatching {
                RetrofitClient.api.updateEquipmentStatus(equipmentId, request)
            }.onSuccess { response ->
                if (response.isSuccessful) {
                    loadEquipmentDetails()
                } else {
                    _uiState.update {
                        it.copy(isLoading = false, error = "${ERR_PREFIX}${response.code()}")
                    }
                }
            }.onFailure { e ->
                _uiState.update {
                    it.copy(isLoading = false, error = e.localizedMessage ?: ERR_NETWORK)
                }
            }
        }
    }

    fun getGoogleDriveDirectLink(url: String?): String? {
        if (url.isNullOrBlank()) return null

        if (url.contains(DRIVE_HOST) && url.contains(PATH_ID_PREFIX)) {
            val id = url.substringAfter(PATH_ID_PREFIX).substringBefore(PATH_DELIMITER)
            return "${DIRECT_LINK_BASE}$id"
        }
        return url
    }

    private fun calculateStartTime(durationMinutes: Int): String {
        val now = LocalDateTime.now()
        val startTime = now.minusMinutes(durationMinutes.toLong())
        val formatter = DateTimeFormatter.ofPattern(PATTERN_TIME)
        return startTime.format(formatter)
    }

    private fun getStatusLabel(status: EquipmentStatus): String {
        return when (status) {
            EquipmentStatus.RUNNING -> LABEL_STATUS_RUNNING
            EquipmentStatus.DOWNTIME -> LABEL_STATUS_DOWNTIME
            EquipmentStatus.SETUP -> LABEL_STATUS_SETUP
        }
    }
}