package com.effitrack.ui.reusingComponents

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.VisualTransformation
import com.effitrack.ui.theme.ContentAccent
import com.effitrack.ui.theme.ContentAlert
import com.effitrack.ui.theme.ContentBase
import com.effitrack.ui.theme.ContentSecondary
import com.effitrack.ui.theme.Dimens
import com.effitrack.ui.theme.TintAccentLight

@Composable
fun AppTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        label = {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium
            )
        },
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(Dimens.spaceMedium),
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        singleLine = true,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = TintAccentLight,
            unfocusedContainerColor = TintAccentLight,
            disabledContainerColor = TintAccentLight,
            errorContainerColor = TintAccentLight,

            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            errorIndicatorColor = Color.Transparent,

            focusedTextColor = ContentBase,
            unfocusedTextColor = ContentBase,
            errorTextColor = ContentAlert,
            cursorColor = ContentAccent,
            errorCursorColor = ContentAlert,

            focusedLabelColor = ContentSecondary,
            unfocusedLabelColor = ContentSecondary,
            errorLabelColor = ContentAlert
        )
    )
}