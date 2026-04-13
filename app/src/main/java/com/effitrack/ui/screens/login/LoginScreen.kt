package com.effitrack.ui.screens.login

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.lifecycle.viewmodel.compose.viewModel
import com.effitrack.R
import com.effitrack.ui.reusingComponents.AppButton
import com.effitrack.ui.reusingComponents.ButtonType
import com.effitrack.ui.reusingComponents.AppTextField
import com.effitrack.ui.theme.ContentAccent
import com.effitrack.ui.theme.ContentSuccess
import com.effitrack.ui.theme.Dimens
import com.effitrack.ui.theme.TintAccentGhost
import com.effitrack.ui.theme.TintAccentHard

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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(TintAccentGhost.copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(Dimens.spaceLarge187))

            Text(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontSize = Dimens.fontSizeTitle,
                    fontWeight = FontWeight.Bold,
                    color = ContentAccent
                ),
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(horizontal = Dimens.spaceLarge)
            )

            Spacer(modifier = Modifier.height(Dimens.spaceLarge187))

            Image(
                painter = painterResource(id = R.drawable.img_gears),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Dimens.spaceLarge),
                contentScale = ContentScale.Fit,
                colorFilter = ColorFilter.tint(ContentAccent),
                alignment = Alignment.CenterEnd
            )

            Spacer(modifier = Modifier.weight(1f))

            Surface(
                modifier = Modifier.fillMaxWidth(),
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
                        .padding(Dimens.spaceLarge),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(Dimens.spaceMedium))

                    AppTextField(
                        value = viewModel.tableNumber,
                        onValueChange = { viewModel.tableNumber = it },
                        label = stringResource(R.string.label_table_number),
                    )

                    Spacer(modifier = Modifier.height(Dimens.spaceMedium))

                    AppTextField(
                        value = viewModel.pinCode,
                        onValueChange = { viewModel.pinCode = it },
                        label = stringResource(R.string.label_pin_code),
                        visualTransformation = PasswordVisualTransformation(),
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
            }
        }
    }
}