package com.effitrack.data.model

import com.effitrack.util.Constants.FIELD_ID
import com.squareup.moshi.Json

data class Task(
    @Json(name = FIELD_ID) val id: Long,
    @Json(name = "title") val title: String,
    @Json(name = "description") val description: String? = null,
    @Json(name = "status") val status: TaskStatus,
    @Json(name = "plannedDate") val plannedDate: String,
    @Json(name = "completedAt") val completedAt: String? = null,
    @Json(name = "estimatedMinutes") val estimatedMinutes: Int = 0,
    @Json(name = "actualMinutes") val actualMinutes: Int = 0,
    @Json(name = "operatorComment") val operatorComment: String? = null,
    @Json(name = "equipment") val equipment: Equipment? = null
)

data class TaskCompleteRequest(
    @Json(name = "actualMinutes") val actualMinutes: Int,
    @Json(name = "operatorComment") val operatorComment: String
)

data class TaskUpdateRequest(
    val plannedDate: String? = null,
    val actualMinutes: Int? = null,
    val operatorComment: String? = null
)

enum class TaskStatus {
    TODO,
    IN_PROGRESS,
    DONE
}