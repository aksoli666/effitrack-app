package com.effitrack.ui.screens.login

import android.widget.Toast
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.lifecycle.viewmodel.compose.viewModel
import com.effitrack.R
import com.effitrack.ui.reusingComponents.AppButton
import com.effitrack.ui.reusingComponents.AppTextField
import com.effitrack.ui.reusingComponents.ButtonType
import com.effitrack.ui.reusingComponents.EffiTrackGearsComponent
import com.effitrack.ui.reusingComponents.EffiTrackScreenWrapper
import com.effitrack.ui.theme.ContentSuccess
import com.effitrack.ui.theme.Dimens

@Composable
fun LoginScreen(
    viewModel: LoginViewModel = viewModel(),
    onLoginSuccess: () -> Unit
) {
    val context = LocalContext.current

    LaunchedEffect(viewModel.errorMessage, viewModel.loginSuccess) {
        viewModel.errorMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            viewModel.errorMessage = null
        }
        if (viewModel.loginSuccess) {
            onLoginSuccess()
        }
    }

    EffiTrackScreenWrapper(
        mainGearsSlot = {
            EffiTrackGearsComponent()
        },
        bottomPanelContent = {
            Spacer(modifier = Modifier.height(Dimens.spaceMedium))

            AppTextField(
                value = viewModel.tableNumber,
                onValueChange = { viewModel.tableNumber = it },
                label = stringResource(R.string.label_table_number),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
            )

            Spacer(modifier = Modifier.height(Dimens.spaceMedium))

            AppTextField(
                value = viewModel.pinCode,
                onValueChange = { viewModel.pinCode = it },
                label = stringResource(R.string.label_pin_code),
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
            )

            Spacer(modifier = Modifier.height(Dimens.spaceLarge))

            AppButton(
                text = stringResource(R.string.btn_login),
                onClick = { viewModel.onLoginClick() },
                color = ContentSuccess,
                type = ButtonType.OUTLINE,
                isLoading = viewModel.isLoading,
            )

            Spacer(modifier = Modifier.height(Dimens.spaceLarge))
        }
    )
}