package com.effitrack.ui.screens.equipment

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.effitrack.R
import com.effitrack.data.model.Equipment
import com.effitrack.data.model.EquipmentStatus
import com.effitrack.ui.reusingComponents.AppCard
import com.effitrack.ui.reusingComponents.AppDivider
import com.effitrack.ui.screens.status.EffiTrackUniversalStatusScreen
import com.effitrack.ui.theme.ContentAccent
import com.effitrack.ui.theme.ContentAlert
import com.effitrack.ui.theme.ContentBase
import com.effitrack.ui.theme.ContentCaution
import com.effitrack.ui.theme.ContentSecondary
import com.effitrack.ui.theme.ContentSuccess
import com.effitrack.ui.theme.Dimens
import com.effitrack.ui.theme.TintAccentGhost
import com.effitrack.ui.theme.TintAccentLight
import com.effitrack.util.Constants.MSG_REPORT_SENT
import com.effitrack.util.bounceClick
import com.effitrack.util.toFormattedWorkTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EquipmentListScreen(
    viewModel: EquipmentViewModel = viewModel(),
    onItemClick: (Long) -> Unit
) {
    val context = LocalContext.current
    val equipmentList = viewModel.displayedEquipment
    val searchQuery = viewModel.searchQuery
    val selectedFilter = viewModel.selectedFilter
    val isLoading = viewModel.isLoading
    val errorMessage = viewModel.errorMessage
    val reportStatus = viewModel.reportSentStatus
    val isSendingReport = viewModel.isSendingReport

    LaunchedEffect(Unit) {
        viewModel.loadEquipment()
    }

    LaunchedEffect(reportStatus, errorMessage) {
        if (reportStatus == true) {
            Toast.makeText(context, MSG_REPORT_SENT, Toast.LENGTH_SHORT).show()
            viewModel.clearReportStatus()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = Dimens.spaceMedium, vertical = Dimens.spaceMedium150)
    ) {
        Column {
            HeaderSection(
                count = equipmentList.size,
                isSending = isSendingReport,
                sendReport = { viewModel.sendReport() }
            )

            Spacer(modifier = Modifier.height(Dimens.spaceMedium))

            SearchField(
                query = searchQuery,
                onQueryChange = { viewModel.onSearchQueryChange(it) }
            )

            Spacer(modifier = Modifier.height(Dimens.spaceMedium))

            FilterSection(
                selectedStatus = selectedFilter,
                onSelect = { viewModel.onFilterSelect(it) }
            )

            Spacer(modifier = Modifier.height(Dimens.spaceMedium125))

            PullToRefreshBox(
                isRefreshing = isLoading,
                onRefresh = { viewModel.loadEquipment() },
                modifier = Modifier.fillMaxSize()
            ) {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(Dimens.spaceSmall150),
                    contentPadding = PaddingValues(bottom = Dimens.spaceExtraXLarge170),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(equipmentList) { equipment ->
                        EquipmentItem(
                            equipment = equipment,
                            onClick = { onItemClick(equipment.id) }
                        )
                    }
                }
            }
        }
    }
    AnimatedVisibility(
        visible = isLoading && equipmentList.isEmpty(),
        enter = fadeIn(),
        exit = fadeOut(animationSpec = tween(durationMillis = 800))
    ) {
        EffiTrackUniversalStatusScreen()
    }
}

@Composable
fun HeaderSection(
    count: Int,
    isSending: Boolean,
    sendReport: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row {
            Text(
                text = stringResource(R.string.title_equipment),
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = ContentBase
                )
            )

            Spacer(modifier = Modifier.width(Dimens.spaceSmall))

            Text(
                text = count.toString(),
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = ContentAccent
                )
            )
        }

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .background(
                    color = TintAccentGhost.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(Dimens.spaceSmall150)
                )
                .size(Dimens.spaceLarge125)
        ) {
            if (isSending) {
                CircularProgressIndicator(
                    modifier = Modifier.size(Dimens.spaceMedium),
                    color = ContentBase,
                    strokeWidth = Dimens.spaceXXSmall200
                )
            } else {
                IconButton(
                    onClick = sendReport,
                    modifier = Modifier.matchParentSize()
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_outbox),
                        contentDescription = stringResource(R.string.btn_export),
                        tint = ContentBase
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchField(query: String, onQueryChange: (String) -> Unit) {
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    TextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(Dimens.spaceMedium)),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = TintAccentLight,
            unfocusedContainerColor = TintAccentLight.copy(alpha = 0.5f),
            disabledContainerColor = TintAccentLight.copy(alpha = 0.5f),

            focusedTextColor = ContentBase,
            unfocusedTextColor = ContentBase,

            cursorColor = ContentAccent,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent
        ),
        placeholder = {
            Text(text = stringResource(R.string.hint_search), color = ContentSecondary)
        },
        leadingIcon = {
            Icon(
                painter = painterResource(R.drawable.ic_search),
                contentDescription = null,
                tint = ContentSecondary
            )
        },
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(
            onSearch = {
                keyboardController?.hide()
                focusManager.clearFocus()
            }
        ),
        singleLine = true
    )
}

@Composable
fun FilterSection(selectedStatus: EquipmentStatus?, onSelect: (EquipmentStatus) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        FilterCapsule(
            color = ContentSuccess,
            isSelected = selectedStatus == EquipmentStatus.RUNNING,
            onClick = { onSelect(EquipmentStatus.RUNNING) }
        )
        FilterCapsule(
            color = ContentCaution,
            isSelected = selectedStatus == EquipmentStatus.SETUP,
            onClick = { onSelect(EquipmentStatus.SETUP) }
        )
        FilterCapsule(
            color = ContentAlert,
            isSelected = selectedStatus == EquipmentStatus.DOWNTIME,
            onClick = { onSelect(EquipmentStatus.DOWNTIME) }
        )
    }
}

@Composable
fun FilterCapsule(
    color: Color,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .width(Dimens.spaceExtraLarge312)
            .height(Dimens.spaceLarge112)
            .clip(RoundedCornerShape(Dimens.spaceMedium))
            .border(
                width = if (isSelected) Dimens.spaceXXSmall200 else Dimens.spaceXXSmall,
                color = if (isSelected) color else color.copy(alpha = 0.3f),
                shape = RoundedCornerShape(Dimens.spaceMedium)
            )
            .background(if (isSelected) color.copy(alpha = 0.1f) else Color.Transparent)
            .bounceClick { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_status),
            contentDescription = null,
            tint = if (isSelected) color else color.copy(alpha = 0.5f)
        )
    }
}

@Composable
fun EquipmentItem(equipment: Equipment, onClick: () -> Unit) {
    val totalShiftMinutes = equipment.workTimeTodayMinutes +
            equipment.downtimeTodayMinutes +
            equipment.setupTodayMinutes

    val durationString = equipment.currentStatusDuration.toFormattedWorkTime()

    val (color, statusLabel) = when (equipment.status) {
        EquipmentStatus.RUNNING -> ContentSuccess to stringResource(R.string.status_running)
        EquipmentStatus.DOWNTIME -> ContentAlert to stringResource(R.string.status_stopped)
        EquipmentStatus.SETUP -> ContentCaution to stringResource(R.string.status_maintenance)
    }

    val activeActionText = if (!equipment.activeAction.isNullOrBlank()) {
        equipment.activeAction
    } else {
        when (equipment.status) {
            EquipmentStatus.RUNNING -> stringResource(R.string.action_default_order)
            EquipmentStatus.DOWNTIME -> stringResource(
                R.string.template_action_downtime,
                durationString
            )

            EquipmentStatus.SETUP -> stringResource(
                R.string.template_action_maintenance,
                durationString
            )
        }
    }

    AppCard(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Column(modifier = Modifier.padding(Dimens.spaceMedium)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = equipment.name,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = ContentBase,
                            fontSize = 18.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(Dimens.spaceXXSmall400))
                    Text(
                        text = "${stringResource(R.string.prefix_inv)} ${equipment.inventoryNumber} / ${
                            stringResource(
                                R.string.prefix_shop
                            )
                        }${equipment.shopNumber}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = ContentSecondary,
                            fontSize = 13.sp
                        )
                    )
                }

                Icon(
                    painter = painterResource(R.drawable.ic_status),
                    contentDescription = statusLabel,
                    tint = color,
                    modifier = Modifier.size(Dimens.spaceMedium112)
                )
            }

            Spacer(modifier = Modifier.height(Dimens.spaceSmall150))
            AppDivider(color = ContentBase, alpha = 0.1f)
            Spacer(modifier = Modifier.height(Dimens.spaceSmall150))

            Row {
                Text(
                    text = stringResource(R.string.label_action_prefix),
                    style = MaterialTheme.typography.bodyMedium.copy(color = ContentBase)
                )

                Spacer(modifier = Modifier.width(Dimens.spaceXXSmall400))

                Text(
                    text = activeActionText,
                    style = MaterialTheme.typography.bodyMedium.copy(color = color)
                )
            }

            Spacer(modifier = Modifier.height(Dimens.spaceXXSmall600))

            Text(
                text = "${stringResource(R.string.label_work_today)} ${totalShiftMinutes.toFormattedWorkTime()}",
                style = MaterialTheme.typography.bodyMedium.copy(color = ContentBase)
            )
        }
    }
}