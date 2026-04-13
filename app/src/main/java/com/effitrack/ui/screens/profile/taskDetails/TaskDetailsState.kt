package com.effitrack.ui.screens.profile.taskDetails

import com.effitrack.data.model.Task
import com.effitrack.util.Constants.EMPTY_STRING

data class TaskDetailsState(
    val isLoading: Boolean = false,
    val task: Task? = null,
    val comment: String = EMPTY_STRING,
    val error: String? = null,
    val isSaved: Boolean = false
)