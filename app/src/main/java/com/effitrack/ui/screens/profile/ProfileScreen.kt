package com.effitrack.ui.screens.profile

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.effitrack.R
import com.effitrack.data.model.Task
import com.effitrack.data.model.TaskStatus
import com.effitrack.data.model.UserProfile
import com.effitrack.ui.reusingComponents.AppButton
import com.effitrack.ui.reusingComponents.AppCard
import com.effitrack.ui.reusingComponents.AppCardHeader
import com.effitrack.ui.reusingComponents.AppDivider
import com.effitrack.ui.reusingComponents.ButtonType
import com.effitrack.ui.screens.profile.taskDetails.TasksDetailBottomSheet
import com.effitrack.ui.theme.ContentAccent
import com.effitrack.ui.theme.ContentAlert
import com.effitrack.ui.theme.ContentBase
import com.effitrack.ui.theme.ContentCaution
import com.effitrack.ui.theme.ContentSecondary
import com.effitrack.ui.theme.ContentSuccess
import com.effitrack.ui.theme.Dimens
import com.effitrack.ui.theme.TintAccentGhost
import com.effitrack.ui.theme.TintAccentLight
import com.effitrack.ui.theme.TintBase
import com.effitrack.util.Constants
import com.effitrack.util.Constants.SLASH
import com.effitrack.util.Constants.TIME_H_SHORT
import com.effitrack.util.Constants.TIME_M_SHORT

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = viewModel(),
    onLogout: () -> Unit
) {
    val context = LocalContext.current
    val state = viewModel.userProfile
    val isLoading = viewModel.isLoading
    val isRefreshing = viewModel.isRefreshing
    val errorMessage = viewModel.errorMessage
    val reportStatus = viewModel.reportSentStatus
    var selectedTask by remember { mutableStateOf<Task?>(null) }

    LaunchedEffect(Unit) {
        viewModel.loadProfileData()
    }

    LaunchedEffect(reportStatus, errorMessage) {
        if (reportStatus == true) {
            Toast.makeText(context, Constants.MSG_REPORT_SENT, Toast.LENGTH_SHORT).show()
            viewModel.clearReportStatus()
        } else if (errorMessage != null) {
            Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
            viewModel.clearReportStatus()
        }
    }

    if (selectedTask != null) {
        TasksDetailBottomSheet(
            task = selectedTask!!,
            onDismissRequest = {
                selectedTask = null
                viewModel.loadProfileData(isPullRefresh = true)
            }
        )
    }

    if (state == null && isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(TintBase),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = ContentAccent)
        }
    } else {
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = { viewModel.loadProfileData(isPullRefresh = true) },
            modifier = Modifier
                .fillMaxSize()
                .background(TintBase)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = Dimens.spaceMedium, vertical = Dimens.spaceMedium150)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.title_profile),
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = ContentBase
                        )
                    )

                    AppButton(
                        text = stringResource(R.string.btn_logout),
                        onClick = onLogout,
                        color = ContentAlert,
                        type = ButtonType.OUTLINE,
                        modifier = Modifier
                            .height(Dimens.spaceLarge156)
                            .width(Dimens.sizeButtonWidthSmall)
                    )
                }

                Spacer(modifier = Modifier.height(Dimens.spaceMedium150))

                state?.let { profile ->
                    UserHeaderCard(profile)

                    Spacer(modifier = Modifier.height(Dimens.spaceMedium150))

                    WorkInfoCard(profile)

                    Spacer(modifier = Modifier.height(Dimens.spaceMedium150))

                    TasksListCard(
                        tasks = profile.tasks,
                        onTaskClick = { taskId ->
                            selectedTask = profile.tasks.find { it.id == taskId }
                        }
                    )
                }

                Spacer(modifier = Modifier.height(Dimens.spaceLarge))

                AppButton(
                    text = if (isLoading && !isRefreshing) stringResource(R.string.msg_sending) else stringResource(
                        R.string.btn_send_report
                    ),
                    onClick = { viewModel.sendReport() },
                    color = ContentBase,
                    type = ButtonType.OUTLINE,
                    modifier = Modifier.fillMaxWidth(),
                )

                Spacer(modifier = Modifier.height(Dimens.spaceExtraLarge312))
            }
        }
    }
}

@Composable
fun UserHeaderCard(user: UserProfile) {
    AppCard(
        modifier = Modifier.fillMaxWidth(),
        containerColor = TintAccentLight,
        borderColor = Color.Transparent
    ) {
        Row(
            modifier = Modifier.padding(Dimens.spaceMedium150),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(Dimens.spaceLarge187)
                    .background(ContentAccent, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = user.initials,
                    style = MaterialTheme.typography.titleLarge.copy(
                        color = ContentBase,
                        fontWeight = FontWeight.Bold
                    )
                )
            }

            Spacer(modifier = Modifier.width(Dimens.spaceMedium))

            Column {
                Text(
                    text = user.name,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = ContentBase
                    )
                )
                Text(
                    text = user.profession ?: stringResource(R.string.msg_operator_profession),
                    style = MaterialTheme.typography.bodyMedium.copy(color = ContentBase)
                )
            }
        }
    }
}

@Composable
fun WorkInfoCard(user: UserProfile) {
    AppCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(Dimens.spaceMedium150)) {
            AppCardHeader(
                text = stringResource(R.string.title_work_info),
                color = ContentAccent
            )
            Spacer(modifier = Modifier.height(Dimens.spaceSmall))
            InfoRow("${stringResource(R.string.label_table_number)} ${user.tableNumber}")
            InfoRow("${stringResource(R.string.prefix_shop)}${user.shopNumber}")
            InfoRow("${stringResource(R.string.prefix_active_equipment)}${user.activeEquipment}")
        }
    }
}

@Composable
fun InfoRow(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium.copy(color = ContentBase),
        modifier = Modifier.padding(vertical = Dimens.spaceXXSmall200)
    )
}

@Composable
fun TasksListCard(
    tasks: List<Task>,
    onTaskClick: (Long) -> Unit
) {
    AppCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(Dimens.spaceMedium150)) {
            AppCardHeader(
                text = stringResource(R.string.title_work_plan),
                color = ContentAccent
            )

            Spacer(modifier = Modifier.height(Dimens.spaceMedium))
            if (tasks.isEmpty()) {
                Text(
                    text = stringResource(R.string.stub_no_tasks),
                    style = MaterialTheme.typography.bodyMedium.copy(color = ContentSecondary),
                    modifier = Modifier.padding(vertical = Dimens.spaceSmall)
                )
            } else {
                tasks.forEachIndexed { index, task ->
                    TaskItem(
                        task = task,
                        onClick = { onTaskClick(task.id) }
                    )

                    if (index < tasks.lastIndex) {
                        AppDivider(
                            color = TintAccentGhost,
                            thickness = Dimens.spaceXXSmallHalf,
                            modifier = Modifier.padding(vertical = Dimens.spaceXXSmall)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TaskItem(
    task: Task,
    onClick: () -> Unit
) {
    val (statusText, statusColor) = when (task.status) {
        TaskStatus.IN_PROGRESS -> stringResource(R.string.status_in_progress) to ContentCaution
        TaskStatus.TODO -> stringResource(R.string.status_assigned) to ContentSecondary
        TaskStatus.DONE -> stringResource(R.string.status_done) to ContentSuccess
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = Dimens.spaceSmall),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = task.title,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = ContentBase,
                    fontWeight = FontWeight.Medium
                )
            )
            Spacer(modifier = Modifier.height(2.dp))

            val subtitle = if (task.estimatedMinutes >= 60) {
                "${task.equipment?.name} $SLASH ${task.estimatedMinutes / 60} $TIME_H_SHORT ${task.estimatedMinutes % 60} $TIME_M_SHORT"
            } else {
                "${task.equipment?.name} $SLASH ${task.estimatedMinutes} $TIME_M_SHORT"
            }

            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall.copy(color = ContentSecondary)
            )
        }

        Text(
            text = statusText,
            style = MaterialTheme.typography.bodySmall.copy(
                fontWeight = FontWeight.Bold
            ),
            color = statusColor,
            modifier = Modifier.padding(start = Dimens.spaceSmall)
        )
    }
}