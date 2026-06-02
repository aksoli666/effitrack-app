package com.effitrack.ui.reusingComponents

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import com.effitrack.R
import com.effitrack.ui.theme.ContentAccent
import com.effitrack.ui.theme.Dimens

@Composable
fun EffiTrackGearsComponent(
    modifier: Modifier = Modifier,
    tint: Color = ContentAccent
) {
    val infiniteTransition = rememberInfiniteTransition(label = "gears")
    val angleRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(5000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    val extraSpace = Dimens.spaceMedium

    Box(
        modifier = modifier.size(Dimens.spaceExtraLarge312 + Dimens.spaceExtraXLarge142),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_gear),
            contentDescription = null,
            modifier = Modifier
                .size(Dimens.spaceExtraLarge312)
                .offset(
                    x = -(Dimens.spaceLarge),
                    y = Dimens.spaceLarge137
                )
                .rotate(angleRotation * 2f),
            tint = tint.copy(alpha = 0.7f)
        )

        Icon(
            painter = painterResource(id = R.drawable.ic_gear),
            contentDescription = null,
            modifier = Modifier
                .size(Dimens.spaceExtraXLarge142)
                .offset(
                    x = Dimens.spaceLarge200 + extraSpace,
                    y = -(Dimens.spaceLarge200 + extraSpace)
                )
                .rotate(-angleRotation),
            tint = tint
        )
    }
}