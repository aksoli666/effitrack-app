package com.effitrack.ui.screens.status

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextAlign
import com.effitrack.ui.reusingComponents.AppButton
import com.effitrack.ui.reusingComponents.ButtonType
import com.effitrack.ui.reusingComponents.EffiTrackGearsComponent
import com.effitrack.ui.reusingComponents.EffiTrackScreenWrapper
import com.effitrack.ui.theme.ContentCaution
import com.effitrack.ui.theme.Dimens
import com.effitrack.ui.theme.TintBase
import com.effitrack.ui.theme.Typography
import com.effitrack.util.Constants.EMPTY_STRING
import com.effitrack.util.Constants.SERVER_RETRY_BTN

@Composable
fun EffiTrackUniversalStatusScreen(
    errorMessage: String = EMPTY_STRING,
    onRetryClick: (() -> Unit)? = null
) {
    EffiTrackScreenWrapper(
        modifier = Modifier
            .fillMaxSize()
            .background(TintBase)
            .pointerInput(Unit) {},
        mainGearsSlot = {
            EffiTrackGearsComponent()
            Spacer(modifier = Modifier.height(Dimens.spaceLarge112))
            Text(
                text = errorMessage,
                style = Typography.titleLarge,
                color = ContentCaution,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = Dimens.spaceMedium)
            )

            onRetryClick?.let {
                Spacer(modifier = Modifier.height(Dimens.spaceMedium125))
                AppButton(
                    text = SERVER_RETRY_BTN,
                    onClick = it,
                    type = ButtonType.SECONDARY,
                    modifier = Modifier.padding(horizontal = Dimens.spaceMedium)
                )
            }

            Spacer(modifier = Modifier.height(Dimens.spaceExtraXLarge142))
        }
    )
}