package com.effitrack.util

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
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

fun Modifier.bounceClick(
    scaleDown: Float = 0.92f,
    onClick: () -> Unit
) = composed {
    var isPressed by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isPressed) scaleDown else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "bounceAnimation"
    )

    this
        .graphicsLayer {
            scaleX = scale
            scaleY = scale
        }
        .semantics { role = Role.Button }
        .pointerInput(Unit) {
            detectTapGestures(
                onPress = {
                    isPressed = true
                    tryAwaitRelease()
                    isPressed = false
                },
                onTap = {
                    onClick()
                }
            )
        }
}

fun Modifier.clickableIf(onClick: (() -> Unit)?): Modifier {
    return if (onClick != null) {
        this.bounceClick(onClick = onClick)
    } else {
        this
    }
}