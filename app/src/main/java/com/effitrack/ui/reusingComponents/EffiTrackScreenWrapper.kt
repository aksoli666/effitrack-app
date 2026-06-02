package com.effitrack.ui.reusingComponents

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.effitrack.R
import com.effitrack.ui.theme.ContentAccent
import com.effitrack.ui.theme.Dimens
import com.effitrack.ui.theme.TintAccentGhost
import com.effitrack.ui.theme.TintAccentHard

@Composable
fun EffiTrackScreenWrapper(
    modifier: Modifier = Modifier,
    titleText: String = stringResource(R.string.app_name),
    mainGearsSlot: @Composable ColumnScope.() -> Unit,
    bottomPanelContent: (@Composable ColumnScope.() -> Unit)? = null
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(TintAccentGhost.copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start
            ) {
                Spacer(modifier = Modifier.height(Dimens.spaceLarge187))

                Text(
                    text = titleText,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontSize = Dimens.fontSizeTitle,
                        fontWeight = FontWeight.Bold,
                        color = ContentAccent
                    ),
                    modifier = Modifier.padding(horizontal = Dimens.spaceLarge)
                )

                Spacer(modifier = Modifier.height(Dimens.spaceLarge))
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    mainGearsSlot()
                }
            }

            bottomPanelContent?.let {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(
                        topStart = Dimens.spaceMedium187,
                        topEnd = Dimens.spaceMedium187
                    ),
                    color = TintAccentHard,
                    shadowElevation = Dimens.default
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .navigationBarsPadding()
                            .padding(top = Dimens.spaceMedium)
                            .padding(horizontal = Dimens.spaceLarge),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        bottomPanelContent()
                    }
                }
            }
        }
    }
}