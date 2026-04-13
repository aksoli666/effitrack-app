package com.effitrack.ui.reusingComponents

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.effitrack.ui.theme.ContentBase
import com.effitrack.ui.theme.ContentSecondary
import com.effitrack.ui.theme.Dimens

@Composable
fun AppInfoItem(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    alignment: Alignment.Horizontal = Alignment.Start
) {
    Column(
        modifier = modifier,
        horizontalAlignment = alignment
    ) {
        Text(text = label, color = ContentSecondary, fontSize = Dimens.fontSizeSmall)
        Text(
            text = value,
            color = ContentBase,
            fontWeight = FontWeight.Medium,
            fontSize = Dimens.fontSizeStandard
        )
    }
}