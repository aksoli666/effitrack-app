package com.effitrack.ui.screens.profile.taskDetails

import android.app.DatePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.viewmodel.compose.viewModel
import com.effitrack.R
import com.effitrack.data.model.Equipment
import com.effitrack.data.model.EquipmentStatus
import com.effitrack.data.model.Task
import com.effitrack.data.model.TaskStatus
import com.effitrack.ui.reusingComponents.AppButton
import com.effitrack.ui.reusingComponents.AppCard
import com.effitrack.ui.reusingComponents.AppDivider
import com.effitrack.ui.reusingComponents.AppInfoItem
import com.effitrack.ui.reusingComponents.ButtonType
import com.effitrack.ui.theme.ContentAccent
import com.effitrack.ui.theme.ContentAlert
import com.effitrack.ui.theme.ContentBase
import com.effitrack.ui.theme.ContentCaution
import com.effitrack.ui.theme.ContentSecondary
import com.effitrack.ui.theme.ContentSuccess
import com.effitrack.ui.theme.Dimens
import com.effitrack.ui.theme.TintAccentGhost
import com.effitrack.ui.theme.TintAccentHard
import com.effitrack.ui.theme.TintAccentLight
import com.effitrack.util.Constants.DATE_FORMAT_ISO
import com.effitrack.util.Constants.OLD_VALUE
import com.effitrack.util.Constants.SLASH
import com.effitrack.util.Constants.TIME_M_SHORT
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TasksDetailBottomSheet(
    task: Task,
    viewModel: TaskDetailsViewModel = viewModel(),
    onDismissRequest: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scrollState = rememberScrollState()
    val focusManager = LocalFocusManager.current

    LaunchedEffect(task) {
        viewModel.setTask(task)
    }

    LaunchedEffect(state.isSaved) {
        if (state.isSaved) {
            viewModel.consumeSavedEvent()
        }
    }

    val currentTask = state.task ?: task

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        containerColor = TintAccentHard,
        dragHandle = { BottomSheetDefaults.DragHandle(color = TintAccentLight) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight(0.92f)
                .fillMaxWidth()
                .pointerInput(Unit) {
                    detectTapGestures(onTap = { focusManager.clearFocus() })
                }
        ) {
            Box(modifier = Modifier.weight(1f)) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                        .padding(
                            horizontal = Dimens.spaceMedium,
                            vertical = Dimens.spaceMedium
                        ),
                    verticalArrangement = Arrangement.spacedBy(Dimens.spaceMedium)
                ) {
                    TaskHeaderCard(
                        task = currentTask,
                        onDateChange = { newDate -> viewModel.saveTaskChanges(newDate = newDate) },
                        onTimeChange = { newTime -> viewModel.saveTaskChanges(newActualTime = newTime) }
                    )

                    TaskActionsCard(currentTask, state.isLoading, viewModel)

                    if (currentTask.equipment != null) {
                        TaskEquipmentCard(currentTask.equipment)
                    }

                    TaskCommentCard(
                        comment = state.comment,
                        onCommentChange = { viewModel.updateCommentLocal(it) },
                        onSave = { viewModel.onCommentFocusLost() }
                    )

                    Spacer(modifier = Modifier.height(Dimens.spaceLarge))
                }
            }
        }
    }
}

@Composable
private fun TaskHeaderCard(
    task: Task,
    onDateChange: (String) -> Unit,
    onTimeChange: (Int) -> Unit
) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    var showTimeDialog by remember { mutableStateOf(false) }

    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, day ->
            val formattedDate = String.format(DATE_FORMAT_ISO, year, month + 1, day)
            onDateChange(formattedDate)
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    if (showTimeDialog) {
        EditTimeDialog(
            initialMinutes = task.actualMinutes,
            onDismiss = { showTimeDialog = false },
            onConfirm = {
                onTimeChange(it)
                showTimeDialog = false
            }
        )
    }

    AppCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(Dimens.spaceMedium)) {
            Text(
                text = stringResource(R.string.title_about_task),
                color = ContentSecondary,
                fontSize = Dimens.fontSizeSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(Dimens.spaceMedium))

            Text(
                text = task.title,
                color = ContentBase,
                fontSize = Dimens.fontSizeLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(Dimens.spaceMedium))
            AppDivider(alpha = 0.5f)
            Spacer(modifier = Modifier.height(Dimens.spaceMedium))

            AppInfoItem(
                label = stringResource(R.string.label_description),
                value = task.description ?: stringResource(R.string.action_default_order)
            )

            Spacer(modifier = Modifier.height(Dimens.spaceSmall))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.clickable { datePickerDialog.show() }) {
                    Text(
                        text = stringResource(R.string.label_plan_date),
                        color = ContentSecondary,
                        fontSize = Dimens.fontSizeSmall
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = task.plannedDate.substringBefore(OLD_VALUE),
                            color = ContentBase,
                            fontWeight = FontWeight.Medium,
                            fontSize = Dimens.fontSizeStandard
                        )
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = null,
                            tint = ContentAccent,
                            modifier = Modifier
                                .size(Dimens.spaceSmall)
                                .padding(start = Dimens.spaceXXSmall400)
                        )
                    }
                }

                Column(
                    horizontalAlignment = Alignment.End,
                    modifier = Modifier.clickable { showTimeDialog = true }
                ) {
                    Text(
                        text = stringResource(R.string.label_time_plan_fact),
                        color = ContentSecondary,
                        fontSize = Dimens.fontSizeSmall
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "${task.estimatedMinutes} $SLASH ${task.actualMinutes} $TIME_M_SHORT",
                            color = ContentBase,
                            fontWeight = FontWeight.Medium,
                            fontSize = Dimens.fontSizeStandard
                        )
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = null,
                            tint = ContentAccent,
                            modifier = Modifier
                                .size(Dimens.spaceSmall)
                                .padding(start = Dimens.spaceXXSmall400)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TaskActionsCard(
    task: Task,
    isLoading: Boolean,
    viewModel: TaskDetailsViewModel
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(Dimens.spaceMedium)
    ) {
        val isInProgress = task.status == TaskStatus.IN_PROGRESS
        val isDone = task.status == TaskStatus.DONE

        AppButton(
            text = stringResource(R.string.status_in_progress),
            onClick = {
                if (!isInProgress) {
                    viewModel.changeStatus(TaskStatus.IN_PROGRESS)
                }
            },
            modifier = Modifier.weight(1f),
            color = if (isInProgress) ContentCaution else ContentSecondary,
            type = if (isInProgress) ButtonType.FULL else ButtonType.OUTLINE,
            isLoading = isLoading && isInProgress
        )

        AppButton(
            text = stringResource(R.string.status_done),
            onClick = {
                viewModel.changeStatus(TaskStatus.DONE)
            },
            modifier = Modifier.weight(1f),
            color = if (isDone) ContentSuccess else ContentSecondary,
            type = if (isDone) ButtonType.FULL else ButtonType.OUTLINE,
            isLoading = isLoading && isDone
        )
    }
}

@Composable
private fun TaskEquipmentCard(equipment: Equipment) {
    AppCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .padding(Dimens.spaceMedium)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = stringResource(R.string.label_equipment),
                    color = ContentSecondary,
                    fontSize = Dimens.fontSizeSmall
                )
                Spacer(modifier = Modifier.height(Dimens.spaceXXSmall400))
                Text(
                    text = equipment.name,
                    color = ContentBase,
                    fontWeight = FontWeight.Bold,
                    fontSize = Dimens.fontSizeStandard
                )
                Text(
                    text = "${stringResource(R.string.prefix_inv)} ${equipment.inventoryNumber} $SLASH ${stringResource(R.string.prefix_shop)}${equipment.shopNumber}",
                    color = ContentSecondary,
                    fontSize = Dimens.fontSizeSmall
                )
            }

            val statusColor = when (equipment.status) {
                EquipmentStatus.RUNNING -> ContentSuccess
                EquipmentStatus.DOWNTIME -> ContentAlert
                EquipmentStatus.SETUP -> ContentCaution
            }

            Icon(
                painter = painterResource(R.drawable.ic_status),
                contentDescription = null,
                modifier = Modifier.size(Dimens.spaceMedium112),
                tint = statusColor
            )
        }
    }
}

@Composable
private fun TaskCommentCard(
    comment: String,
    onCommentChange: (String) -> Unit,
    onSave: () -> Unit
) {
    val focusManager = LocalFocusManager.current
    var isFocused by remember { mutableStateOf(false) }

    TextField(
        value = comment,
        onValueChange = onCommentChange,
        label = {
            Text(
                text = stringResource(R.string.label_comment),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = Dimens.spaceXXSmall400)
            )
        },
        placeholder = {
            Text(
                stringResource(R.string.hint_enter_comment),
                color = ContentSecondary.copy(alpha = 0.5f)
            )
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(Dimens.spaceExtraLarge312)
            .onFocusChanged { focusState ->
                isFocused = focusState.isFocused
                if (!focusState.isFocused) {
                    onSave()
                }
            },
        trailingIcon = if (isFocused) {
            {
                IconButton(onClick = {
                    onSave()
                    focusManager.clearFocus()
                }) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Save",
                        tint = ContentSecondary
                    )
                }
            }
        } else null,
        textStyle = MaterialTheme.typography.bodyLarge,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(onDone = {
            onSave()
            focusManager.clearFocus()
        }),
        shape = RoundedCornerShape(Dimens.spaceMedium),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = TintAccentGhost.copy(alpha = 0.5f),
            unfocusedContainerColor = TintAccentGhost.copy(alpha = 0.5f),
            disabledContainerColor = TintAccentGhost.copy(alpha = 0.5f),
            errorContainerColor = TintAccentGhost.copy(alpha = 0.5f),

            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            errorIndicatorColor = Color.Transparent,

            focusedTextColor = ContentBase,
            unfocusedTextColor = ContentBase,
            cursorColor = ContentAccent,

            focusedLabelColor = ContentSecondary,
            unfocusedLabelColor = ContentSecondary
        )
    )
}

@Composable
fun EditTimeDialog(
    initialMinutes: Int,
    onDismiss: () -> Unit,
    onConfirm: (Int) -> Unit
) {
    var text by remember { mutableStateOf(initialMinutes.toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = TintAccentHard,
        title = {
            Text(stringResource(R.string.label_fact_time), color = ContentBase)
        },
        text = {
            OutlinedTextField(
                value = text,
                onValueChange = { if (it.all { char -> char.isDigit() }) text = it },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = ContentBase,
                    unfocusedTextColor = ContentBase,
                    cursorColor = ContentAccent,
                    focusedBorderColor = ContentAccent,
                    unfocusedBorderColor = TintAccentLight
                )
            )
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(text.toIntOrNull() ?: 0) }) {
                Text(stringResource(R.string.btn_ok), color = ContentSuccess)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.btn_cancel), color = ContentSecondary)
            }
        }
    )
}
