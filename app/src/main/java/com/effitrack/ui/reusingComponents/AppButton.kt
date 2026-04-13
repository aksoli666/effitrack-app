package com.effitrack.ui.reusingComponents

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.font.FontWeight
import com.effitrack.ui.theme.ContentBase
import com.effitrack.ui.theme.ContentSecondary
import com.effitrack.ui.theme.Dimens
import com.effitrack.ui.theme.TintAccentHard

@Composable
fun AppButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    color: Color = Color.Transparent,
    type: ButtonType = ButtonType.PRIMARY,
    icon: Painter? = null,
    isLoading: Boolean = false
) {
    val (containerColor, contentColor, borderColor) = when (type) {
        ButtonType.PRIMARY -> Triple(
            TintAccentHard,
            ContentBase,
            Color.Transparent
        )
        ButtonType.SECONDARY -> Triple(
            ContentSecondary,
            ContentBase,
            Color.Transparent
        )
        ButtonType.FULL -> Triple(
            color.copy(alpha = 0.2f),
            color,
            color
        )
        ButtonType.OUTLINE -> Triple(
            Color.Transparent,
            color,
            color
        )
    }

    Button(
        onClick = onClick,
        modifier = modifier
            .height(Dimens.spaceLarge156)
            .fillMaxWidth(),
        shape = RoundedCornerShape(Dimens.spaceMedium),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor,
            disabledContainerColor = containerColor.copy(alpha = 0.5f),
            disabledContentColor = contentColor.copy(alpha = 0.5f)
        ),
        border = if (borderColor != Color.Transparent)
            BorderStroke(Dimens.spaceXXSmall, borderColor)
        else null,
        enabled = !isLoading
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(Dimens.spaceMedium112),
                color = contentColor,
                strokeWidth = Dimens.spaceXXSmall200
            )
        } else {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                if (icon != null) {
                    Icon(
                        painter = icon,
                        contentDescription = null,
                        modifier = Modifier.size(Dimens.spaceMedium112)
                    )
                    Spacer(modifier = Modifier.width(Dimens.spaceSmall))
                }

                Text(
                    text = text,
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
    }
}