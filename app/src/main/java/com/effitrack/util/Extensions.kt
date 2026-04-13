package com.effitrack.util

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.ui.Modifier
import com.effitrack.data.model.Task
import com.effitrack.data.model.TaskStatus
import com.effitrack.util.Constants.DASH
import com.effitrack.util.Constants.DOT
import com.effitrack.util.Constants.TIME_H_SHORT
import com.effitrack.util.Constants.TIME_M_SHORT
import java.time.LocalDateTime

fun Int.toFormattedWorkTime(): String {
    if (this <= 0) return "0${TIME_H_SHORT} 0${TIME_M_SHORT}"
    val hours = this / 60
    val minutes = this % 60
    return "$hours${TIME_H_SHORT} $minutes${TIME_M_SHORT}"
}

fun Long.toFormattedWorkTime(): String = this.toInt().toFormattedWorkTime()

fun String?.toUiDate(): String {
    if (this.isNullOrBlank()) return DASH
    val cleanDate = this.take(10)
    val parts = cleanDate.split(DASH)
    return if (parts.size == 3) {
        "${parts[2]}$DOT${parts[1]}$DOT${parts[0]}"
    } else {
        this
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun List<Task>.filterAndSortForUi(): List<Task> {
    val now = LocalDateTime.now()
    return this
        .filter { task ->
            if (task.status == TaskStatus.DONE && task.completedAt != null) {
                runCatching {
                    LocalDateTime.parse(task.completedAt).plusHours(24).isAfter(now)
                }.getOrDefault(true)
            } else {
                true
            }
        }
        .sortedWith(compareBy<Task> { task ->
            when (task.status) {
                TaskStatus.IN_PROGRESS -> 1
                TaskStatus.TODO -> 2
                TaskStatus.DONE -> 3
            }
        }.thenBy { it.plannedDate })
}

fun Modifier.clickableIf(onClick: (() -> Unit)?): Modifier {
    return if (onClick != null) {
        this.clickable(onClick = onClick)
    } else {
        this
    }
}