package com.effitrack.ui.reusingComponents

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
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
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

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
        keyboardActions = KeyboardActions(
            onSearch = {
                keyboardController?.hide()
                focusManager.clearFocus()
            }
        ),
        singleLine = true,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = TintAccentLight,
            unfocusedContainerColor = TintAccentLight.copy(alpha = 0.5f),
            disabledContainerColor = TintAccentLight.copy(alpha = 0.5f),
            errorContainerColor = TintAccentLight.copy(alpha = 0.5f),

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
        ),
    )
}