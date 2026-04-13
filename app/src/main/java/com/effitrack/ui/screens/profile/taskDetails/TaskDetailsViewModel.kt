package com.effitrack.ui.screens.profile.taskDetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.effitrack.data.model.Task
import com.effitrack.data.model.TaskCompleteRequest
import com.effitrack.data.model.TaskStatus
import com.effitrack.data.model.TaskUpdateRequest
import com.effitrack.data.remote.RetrofitClient
import com.effitrack.util.Constants.EMPTY_STRING
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TaskDetailsViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(TaskDetailsState())
    val uiState: StateFlow<TaskDetailsState> = _uiState.asStateFlow()

    fun setTask(task: Task) {
        _uiState.update {
            it.copy(
                task = task,
                comment = task.operatorComment ?: EMPTY_STRING,
                error = null,
                isSaved = false
            )
        }
    }

    fun updateCommentLocal(newComment: String) {
        _uiState.update { it.copy(comment = newComment) }
    }

    fun saveTaskChanges(
        newDate: String? = null,
        newActualTime: Int? = null,
        newComment: String? = null
    ) {
        val currentTask = _uiState.value.task ?: return

        if (newDate == null && newActualTime == null && newComment == null) return

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val request = TaskUpdateRequest(
                    plannedDate = newDate,
                    actualMinutes = newActualTime,
                    operatorComment = newComment
                )
                val response = RetrofitClient.api.updateTaskDetails(currentTask.id, request)

                if (response.isSuccessful && response.body() != null) {
                    val updatedTask = response.body()!!
                    _uiState.update {
                        it.copy(task = updatedTask, comment = updatedTask.operatorComment ?: EMPTY_STRING)
                    }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.localizedMessage) }
            }
        }
    }

    fun onCommentFocusLost() {
        val currentUiComment = _uiState.value.comment
        val originalComment = _uiState.value.task?.operatorComment ?: EMPTY_STRING

        if (currentUiComment != originalComment) {
            saveTaskChanges(newComment = currentUiComment)
        }
    }

    fun changeStatus(newStatus: TaskStatus) {
        val currentTask = _uiState.value.task ?: return
        val currentComment = _uiState.value.comment

        _uiState.update { it.copy(isLoading = true) }

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = when (newStatus) {
                    TaskStatus.IN_PROGRESS -> RetrofitClient.api.startTask(currentTask.id)
                    TaskStatus.DONE -> {
                        val request = TaskCompleteRequest(
                            actualMinutes = currentTask.estimatedMinutes,
                            operatorComment = currentComment
                        )
                        RetrofitClient.api.completeTask(currentTask.id, request)
                    }
                    else -> null
                }

                if (response != null && response.isSuccessful) {
                    val updatedTask = response.body()
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isSaved = true,
                            task = updatedTask
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(isLoading = false, error = response?.message())
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, error = e.localizedMessage)
                }
            }
        }
    }

    fun consumeSavedEvent() {
        _uiState.update { it.copy(isSaved = false) }
    }
}
