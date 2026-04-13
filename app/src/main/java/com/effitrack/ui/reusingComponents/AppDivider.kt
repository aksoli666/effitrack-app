package com.effitrack.ui.reusingComponents

import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import com.effitrack.ui.theme.Dimens
import com.effitrack.ui.theme.TintAccentLight

@Composable
fun AppDivider(
    modifier: Modifier = Modifier,
    color: Color = TintAccentLight,
    alpha: Float = 1f,
    thickness: Dp = Dimens.spaceXXSmall
) {
    HorizontalDivider(
        modifier = modifier,
        color = color.copy(alpha = alpha),
        thickness = thickness
    )
}