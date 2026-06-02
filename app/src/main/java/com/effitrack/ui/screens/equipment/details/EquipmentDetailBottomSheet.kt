package com.effitrack.ui.screens.equipment.details

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.effitrack.R
import com.effitrack.data.model.EquipmentDetailState
import com.effitrack.data.model.EquipmentStatus
import com.effitrack.ui.reusingComponents.AppButton
import com.effitrack.ui.reusingComponents.AppCard
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
import com.effitrack.util.Constants.DASH
import com.effitrack.util.bounceClick
import kotlinx.coroutines.delay

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EquipmentDetailBottomSheet(
    viewModel: EquipmentDetailViewModel = viewModel(),
    onDismissRequest: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scrollState = rememberScrollState()

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
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                when {
                    state.isLoading -> {
                        CircularProgressIndicator(
                            color = ContentAccent,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }

                    state.equipment != null -> {
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
                            HeaderCard(state, viewModel)

                            if (state.equipment!!.status != EquipmentStatus.RUNNING) {
                                DowntimeCard(state, onFinishClick = { viewModel.finishDowntime() })

                                EquipmentCommentCard(
                                    comment = state.comment,
                                    onCommentChange = { viewModel.updateCommentLocal(it) },
                                    onSave = { viewModel.onCommentFocusLost() }
                                )

                                EquipmentAiAnalysisCard(
                                    state = state,
                                    onHeaderClick = { viewModel.triggerAiAnalysis() }
                                )
                            }

                            StatsCard(state)

                            HistoryCard(state)

                            DocsCard(state)

                            Spacer(modifier = Modifier.height(Dimens.spaceLarge))
                        }
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun HeaderCard(state: EquipmentDetailState, viewModel: EquipmentDetailViewModel) {
    val equipment = state.equipment ?: return

    AppCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(Dimens.spaceMedium),
        ) {
            Box(
                modifier = Modifier
                    .height(Dimens.sizeHeaderImageHeight)
                    .fillMaxWidth()
                    .background(TintAccentLight, RoundedCornerShape(Dimens.spaceSmall))
                    .clip(RoundedCornerShape(Dimens.spaceSmall)),
                contentAlignment = Alignment.Center
            ) {
                if (!equipment.imageUrl.isNullOrBlank()) {
                    SubcomposeAsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(viewModel.getGoogleDriveDirectLink(equipment.imageUrl))
                            .crossfade(true)
                            .build(),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize(),
                        loading = {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = null,
                                    modifier = Modifier.size(Dimens.spaceLarge),
                                    tint = ContentSecondary
                                )
                            }
                        },
                        error = {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = null,
                                    modifier = Modifier.size(Dimens.spaceLarge),
                                    tint = TintAccentLight
                                )
                            }
                        }
                    )
                } else {
                    Text(
                        text = stringResource(R.string.stub_photo_text),
                        color = ContentBase.copy(alpha = 0.5f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(Dimens.spaceMedium))

            Text(
                text = equipment.name,
                color = ContentBase,
                fontSize = Dimens.fontSizeLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(Dimens.spaceXXSmall400))

            Text(
                text = stringResource(
                    R.string.fmt_equipment_ids,
                    equipment.inventoryNumber,
                    equipment.shopNumber
                ),
                color = ContentSecondary,
                fontSize = Dimens.fontSizeSmall,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(Dimens.spaceSmall))

            Icon(
                painter = painterResource(R.drawable.ic_status),
                contentDescription = null,
                modifier = Modifier
                    .size(Dimens.spaceSmall150)
                    .align(Alignment.End),
                tint = when (equipment.status) {
                    EquipmentStatus.RUNNING -> ContentSuccess
                    EquipmentStatus.DOWNTIME -> ContentAlert
                    EquipmentStatus.SETUP -> ContentCaution
                }
            )
        }
    }
}

@Composable
private fun EquipmentCommentCard(
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
                        contentDescription = null,
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
            focusedContainerColor = TintAccentGhost,
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
private fun EquipmentAiAnalysisCard(
    state: EquipmentDetailState,
    onHeaderClick: () -> Unit
) {
    val dots = remember { mutableStateOf(".") }
    if (state.isAiGenerating) {
        LaunchedEffect(Unit) {
            while (true) {
                dots.value = when (dots.value) {
                    "." -> ".."
                    ".." -> "..."
                    else -> "."
                }
                delay(500)
            }
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .bounceClick { onHeaderClick() },
        shape = RoundedCornerShape(Dimens.spaceSmall150),
        colors = CardDefaults.cardColors(containerColor = TintAccentGhost.copy(alpha = 0.5f)),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = Dimens.spaceXXSmall,
                    color = TintAccentLight,
                    shape = RoundedCornerShape(Dimens.spaceSmall150)
                )
                .padding(Dimens.spaceMedium)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Face,
                    contentDescription = null,
                    tint = ContentAccent,
                    modifier = Modifier.size(Dimens.spaceLarge)
                )
                Spacer(modifier = Modifier.width(Dimens.spaceSmall))
                Text(
                    text = stringResource(R.string.ai_analysis),
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontSize = 12.sp,
                        lineHeight = 16.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = ContentBase
                )
            }

            Spacer(modifier = Modifier.height(Dimens.spaceMedium))

            val displayText = when {
                state.isAiGenerating -> dots.value
                !state.equipment?.aiAnalysis.isNullOrBlank() -> state.equipment.aiAnalysis
                else -> stringResource(R.string.ai_analysis_hint)
            }

            Text(
                text = displayText,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                ),
                color = if (state.equipment?.aiAnalysis.isNullOrBlank() && !state.isAiGenerating)
                    ContentSecondary.copy(alpha = 0.6f) else ContentBase
            )
        }
    }
}

@Composable
private fun DowntimeCard(state: EquipmentDetailState, onFinishClick: () -> Unit) {
    val equipment = state.equipment ?: return

    val (statusTextRes, statusColor) = when (equipment.status) {
        EquipmentStatus.DOWNTIME -> R.string.status_stopped to ContentAlert
        EquipmentStatus.SETUP -> R.string.status_maintenance to ContentCaution
        else -> R.string.status_stopped to ContentAlert
    }

    AppCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(Dimens.spaceSmall150)
    ) {
        Column(modifier = Modifier.padding(Dimens.spaceMedium)) {
            Text(
                text = stringResource(statusTextRes),
                color = statusColor,
                fontWeight = FontWeight.Bold,
                fontSize = Dimens.fontSizeStandard
            )

            Text(
                text = state.duration,
                color = ContentSecondary,
                fontSize = Dimens.fontSizeCaption,
                modifier = Modifier.padding(top = Dimens.spaceXXSmall200)
            )

            Spacer(modifier = Modifier.height(Dimens.spaceSmall))

            Text(
                text = stringResource(
                    R.string.fmt_downtime_info,
                    state.downtimeStartTime,
                    state.currentDowntimeDuration
                ),
                color = ContentBase,
                fontSize = Dimens.fontSizeSmall,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(Dimens.spaceMedium))

            AppButton(
                text = stringResource(R.string.btn_finish_downtime),
                onClick = onFinishClick,
                type = ButtonType.FULL,
                color = ContentSuccess,
                icon = painterResource(R.drawable.ic_status)
            )
        }
    }
}

@Composable
private fun StatsCard(state: EquipmentDetailState) {
    AppCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(Dimens.spaceSmall150)
    ) {
        Column(modifier = Modifier.padding(Dimens.spaceMedium)) {
            Text(
                text = stringResource(R.string.title_stats_shift),
                color = ContentBase,
                fontWeight = FontWeight.Bold,
                fontSize = Dimens.fontSizeStandard
            )
            Spacer(modifier = Modifier.height(Dimens.spaceMedium))

            StatRow(
                label = stringResource(R.string.label_total_work_time),
                value = state.workTime,
                indicatorColor = ContentBase
            )
            StatRow(
                label = stringResource(R.string.label_total_downtime),
                value = state.totalDowntime,
                indicatorColor = ContentAlert
            )
            StatRow(
                label = stringResource(R.string.label_total_setup_time),
                value = state.setupTime,
                indicatorColor = ContentCaution
            )
        }
    }
}

@Composable
private fun StatRow(label: String, value: String, indicatorColor: Color?) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = Dimens.spaceXXSmall600),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (indicatorColor != null) {
                Box(
                    modifier = Modifier
                        .size(Dimens.spaceXXSmall400)
                        .clip(CircleShape)
                        .background(indicatorColor)
                )
                Spacer(modifier = Modifier.width(Dimens.spaceSmall))
            } else {
                Spacer(modifier = Modifier.width(Dimens.spaceSmall150))
            }
            Text(
                text = label,
                color = ContentBase.copy(alpha = 0.9f),
                fontSize = Dimens.fontSizeSmall
            )
        }
        Text(
            text = value,
            color = ContentBase,
            fontWeight = FontWeight.Medium,
            fontSize = Dimens.fontSizeSmall
        )
    }
}

@Composable
private fun HistoryCard(state: EquipmentDetailState) {
    Card(
        shape = RoundedCornerShape(Dimens.spaceSmall150),
        colors = CardDefaults.cardColors(containerColor = TintAccentGhost.copy(alpha = 0.5f)),
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = Dimens.spaceXXSmall,
                color = TintAccentLight,
                shape = RoundedCornerShape(Dimens.spaceSmall150)
            )
    ) {
        Column(modifier = Modifier.padding(Dimens.spaceMedium)) {
            Text(
                text = stringResource(R.string.title_history),
                color = ContentBase,
                fontWeight = FontWeight.Bold,
                fontSize = Dimens.fontSizeStandard
            )
            Spacer(modifier = Modifier.height(Dimens.spaceSmall))

            val historyItems = state.history

            if (historyItems.isEmpty()) {
                Text(
                    text = stringResource(R.string.label_empty_history),
                    color = ContentSecondary,
                    fontSize = Dimens.fontSizeSmall,
                    modifier = Modifier.padding(vertical = Dimens.spaceSmall)
                )
            } else {
                historyItems.forEachIndexed { index, item ->
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = item.time,
                                color = ContentBase,
                                fontWeight = FontWeight.Bold,
                                fontSize = Dimens.fontSizeSmall
                            )
                            Spacer(modifier = Modifier.width(Dimens.spaceSmall))

                            Text(
                                text = "$DASH ${item.status} $DASH ${item.description}",
                                color = ContentBase.copy(alpha = 0.9f),
                                fontSize = Dimens.fontSizeSmall,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        if (index < historyItems.lastIndex) {
                            HorizontalDivider(
                                color = TintAccentLight,
                                thickness = Dimens.spaceXXSmall,
                                modifier = Modifier.padding(vertical = Dimens.spaceSmall)
                            )
                        } else {
                            Spacer(modifier = Modifier.height(Dimens.spaceSmall))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DocsCard(state: EquipmentDetailState) {
    AppCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(Dimens.spaceSmall150)
    ) {
        Column(modifier = Modifier.padding(Dimens.spaceMedium)) {
            Text(
                text = stringResource(R.string.title_maintenance_docs),
                color = ContentBase,
                fontWeight = FontWeight.Bold,
                fontSize = Dimens.fontSizeStandard
            )
            Spacer(modifier = Modifier.height(Dimens.spaceMedium))

            DocDateRow(stringResource(R.string.label_last_maintenance), state.lastMaintenance)
            Spacer(modifier = Modifier.height(Dimens.spaceSmall))
            DocDateRow(stringResource(R.string.label_next_maintenance), state.nextMaintenance)
        }
    }
}

@Composable
private fun DocDateRow(label: String, date: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, color = ContentSecondary, fontSize = Dimens.fontSizeSmall)
        Text(text = date, color = ContentBase, fontSize = Dimens.fontSizeSmall)
    }
}