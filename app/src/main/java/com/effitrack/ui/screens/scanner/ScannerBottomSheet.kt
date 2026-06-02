package com.effitrack.ui.screens.scanner

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import com.effitrack.R
import com.effitrack.ui.reusingComponents.AppButton
import com.effitrack.ui.reusingComponents.AppTextField
import com.effitrack.ui.reusingComponents.ButtonType
import com.effitrack.ui.theme.ContentAlert
import com.effitrack.ui.theme.ContentBase
import com.effitrack.ui.theme.Dimens
import com.effitrack.ui.theme.TintAccentGhost
import com.effitrack.ui.theme.TintAccentHard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScannerBottomSheet(
    uiState: ScannerUiState,
    onDismissRequest: () -> Unit,
    onCodeChanged: (String) -> Unit,
    onAddClick: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        containerColor = TintAccentHard,
        dragHandle = null,
        shape = RoundedCornerShape(topStart = Dimens.spaceLarge, topEnd = Dimens.spaceLarge)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimens.spaceLarge),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = stringResource(
                    if (uiState.isManualEntry) R.string.title_enter_number
                    else R.string.title_check_number
                ),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = ContentBase
                )
            )

            Spacer(modifier = Modifier.height(Dimens.spaceMedium))

            AppTextField(
                value = uiState.scannedCode,
                onValueChange = onCodeChanged,
                label = stringResource(R.string.label_inv_number),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = Dimens.spaceMedium),
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_gears),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = Dimens.spaceLarge125)
                        .offset(x = Dimens.spaceLarge187, y = Dimens.spaceLarge156),
                    contentScale = ContentScale.Fit,
                    colorFilter = ColorFilter.tint(TintAccentGhost.copy(alpha = 0.3f)),
                )

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(Dimens.spaceMedium),
                    modifier = Modifier
                        .padding(vertical = Dimens.spaceMedium)
                ) {
                    AppButton(
                        text = stringResource(R.string.btn_add_equipment),
                        onClick = onAddClick,
                        type = ButtonType.OUTLINE,
                        color = ContentBase
                    )

                    AppButton(
                        text = stringResource(R.string.btn_cancel),
                        onClick = onDismissRequest,
                        type = ButtonType.OUTLINE,
                        color = ContentAlert
                    )
                }
            }
        }
    }
}