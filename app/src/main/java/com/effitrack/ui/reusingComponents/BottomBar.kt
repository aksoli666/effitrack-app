package com.effitrack.ui.reusingComponents

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import com.effitrack.R
import com.effitrack.ui.theme.ContentBase
import com.effitrack.ui.theme.Dimens
import com.effitrack.ui.theme.TintAccentHard
import com.effitrack.ui.theme.TintAccentLight

@Composable
fun BottomBar(
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit
) {
    val items = listOf(
        R.drawable.ic_list,
        R.drawable.ic_profile,
        R.drawable.ic_add,
    )

    Box(
        modifier = Modifier
            .width(Dimens.sizeBottomBarWidth)
            .height(Dimens.spaceLarge200)
            .clip(RoundedCornerShape(Dimens.spaceMedium))
            .background(TintAccentHard)
            .border(
                width = Dimens.spaceXXSmall,
                color = TintAccentLight.copy(alpha = 0.3f),
                shape = RoundedCornerShape(Dimens.spaceMedium)
            ),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEachIndexed { index, icon ->
                val isSelected = index == selectedIndex

                Box(
                    modifier = Modifier
                        .size(Dimens.spaceLarge137)
                        .clip(RoundedCornerShape(Dimens.spaceSmall175))
                        .background(
                            if (isSelected) TintAccentLight else Color.Transparent
                        )
                        .clickable { onItemSelected(index) },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(icon),
                        contentDescription = null,
                        tint = ContentBase,
                        modifier = Modifier.size(Dimens.spaceMedium162)
                    )
                }
            }
        }
    }
}