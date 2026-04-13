package com.effitrack.data.model

import com.effitrack.util.Constants.EMPTY_STRING
import com.effitrack.util.Constants.FIELD_ID
import com.squareup.moshi.Json

data class Equipment(
    @Json(name = FIELD_ID) val id: Long,
    @Json(name = "name") val name: String,
    @Json(name = "inventoryNumber") val inventoryNumber: String,
    @Json(name = "shopNumber") val shopNumber: String,
    @Json(name = "status") val status: EquipmentStatus,
    @Json(name = "imageUrl") val imageUrl: String? = null,
    @Json(name = "activeAction") val activeAction: String? = null,
    @Json(name = "workTimeTodayMinutes") val workTimeTodayMinutes: Int = 0,
    @Json(name = "downtimeTodayMinutes") val downtimeTodayMinutes: Int = 0,
    @Json(name = "setupTodayMinutes") val setupTodayMinutes: Int = 0,
    @Json(name = "currentStatusDuration") val currentStatusDuration: Int = 0,
    @Json(name = "lastMaintenance") val lastMaintenance: String? = null,
    @Json(name = "nextMaintenance") val nextMaintenance: String? = null,
    @Json(name = "lastStatusChange") val lastStatusChange: String? = null
)

enum class EquipmentStatus {
    @Json(name = "RUNNING") RUNNING,
    @Json(name = "DOWNTIME") DOWNTIME,
    @Json(name = "SETUP") SETUP
}

data class EquipmentStatusUpdate(
    @Json(name = "status") val status: EquipmentStatus,
    @Json(name = "reason") val reason: String
)

data class EquipmentDetailState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val equipment: Equipment? = null,
    val workTime: String = EMPTY_STRING,
    val setupTime: String = EMPTY_STRING,
    val totalDowntime: String = EMPTY_STRING,
    val currentDowntimeDuration: String = EMPTY_STRING,
    val downtimeStartTime: String = EMPTY_STRING,
    val duration: String = EMPTY_STRING,
    val lastMaintenance: String = EMPTY_STRING,
    val nextMaintenance: String = EMPTY_STRING,
    val history: List<HistoryItem> = emptyList()
)

data class HistoryItem(
    val time: String,
    val status: String,
    val description: String
)