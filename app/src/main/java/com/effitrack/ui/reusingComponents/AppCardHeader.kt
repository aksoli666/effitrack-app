package com.effitrack.ui.reusingComponents

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import com.effitrack.ui.theme.ContentBase
import com.effitrack.ui.theme.Dimens

@Composable
fun AppCardHeader(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = ContentBase
) {
    Text(
        text = text,
        color = color,
        fontWeight = FontWeight.Bold,
        fontSize = Dimens.fontSizeStandard,
        modifier = modifier
    )
}