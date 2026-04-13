package com.effitrack.data.model

import com.effitrack.util.Constants.FIELD_ID
import com.squareup.moshi.Json

data class User(
    @Json(name = FIELD_ID) val id: Long,
    @Json(name = "fullName") val fullName: String,
    @Json(name = "tableNumber") val tableNumber: String,
    @Json(name = "shopNumber") val shopNumber: String?,
    @Json(name = "profession") val profession: String?,
    @Json(name = "email") val email: String?,
    @Json(name = "monitoredEquipment") val monitoredEquipment: List<Equipment> = emptyList()
)

data class LoginRequest(
    @Json(name = "tableNumber") val tableNumber: String,
    @Json(name = "pinCode") val pinCode: String
)

data class UserProfile(
    val id: String,
    val name: String,
    val tableNumber: String,
    val shopNumber: String,
    val profession: String?,
    val activeEquipment: Int,
    val initials: String,
    val tasks: List<Task>
)