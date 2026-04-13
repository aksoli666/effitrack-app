package com.effitrack.util

import com.effitrack.data.model.EquipmentStatus
import com.effitrack.data.model.Task
import com.effitrack.data.model.User
import com.effitrack.data.model.UserProfile
import com.effitrack.util.Constants.DASH
import com.effitrack.util.Constants.EMPTY_STRING
import com.effitrack.util.Constants.SPACE
import com.effitrack.util.Constants.STUB_INITIALS

fun User.toUiModel(tasks: List<Task>): UserProfile {
    val initials = fullName.split(SPACE)
        .filter { it.isNotEmpty() }
        .take(2)
        .mapNotNull { it.firstOrNull()?.uppercase() }
        .joinToString(EMPTY_STRING)

    val activeCount = monitoredEquipment.count { it.status == EquipmentStatus.RUNNING }

    return UserProfile(
        id = id.toString(),
        name = fullName,
        tableNumber = tableNumber,
        shopNumber = shopNumber ?: DASH,
        activeEquipment = activeCount,
        initials = initials.ifEmpty { STUB_INITIALS },
        tasks = tasks,
        profession = profession
    )
}
