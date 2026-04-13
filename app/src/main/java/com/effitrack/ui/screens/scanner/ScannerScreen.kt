package com.effitrack.ui.screens.scanner

import android.Manifest
import android.media.AudioManager
import android.media.ToneGenerator
import android.util.Size
import android.view.ViewGroup
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.effitrack.R
import com.effitrack.ui.theme.ContentBase
import com.effitrack.ui.theme.Dimens
import com.effitrack.ui.theme.TintAccentGhost
import com.effitrack.ui.theme.TintAccentHard
import com.effitrack.util.TextAnalyzer
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import java.util.concurrent.Executors

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ScannerScreen(
    viewModel: ScannerViewModel = viewModel(),
    onScanSuccess: (String) -> Unit
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current

    val uiState by viewModel.uiState.collectAsState()

    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is ScannerEffect.PlayScanSound -> {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    val toneGen = ToneGenerator(AudioManager.STREAM_MUSIC, 100)
                    toneGen.startTone(ToneGenerator.TONE_PROP_BEEP)
                }
                is ScannerEffect.NavigateToDetails -> {
                    Toast.makeText(
                        context,
                        context.getString(R.string.msg_equipment_added),
                        Toast.LENGTH_SHORT
                    ).show()

                    onScanSuccess(effect.equipmentId.toString())
                }
            }
        }
    }

    LaunchedEffect(uiState.error) {
        uiState.error?.let { errorMessage ->
            Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
        }
    }

    LaunchedEffect(Unit) {
        if (!cameraPermissionState.status.isGranted) {
            cameraPermissionState.launchPermissionRequest()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        CameraPreview(
            onTextFound = { code ->
                viewModel.onCodeScanned(code)
            }
        )

        ScannerOverlay()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = Dimens.spaceExtraLarge312, horizontal = Dimens.spaceMedium),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(R.string.title_scanning),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = ContentBase
                ),
                modifier = Modifier.statusBarsPadding()
            )

            Box(
                modifier = Modifier
                    .offset(y = -Dimens.spaceExtraLarge312)
                    .background(TintAccentHard.copy(alpha = 0.6f), RoundedCornerShape(50))
                    .padding(horizontal = Dimens.spaceLarge, vertical = Dimens.spaceSmall)
            ) {
                Text(
                    text = stringResource(R.string.hint_scan),
                    color = ContentBase,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(Dimens.spaceMedium))
                    .background(TintAccentHard)
                    .clickable { viewModel.onManualEntryClicked() }
                    .padding(horizontal = Dimens.spaceLarge, vertical = Dimens.spaceMedium)
            ) {
                Text(
                    text = stringResource(R.string.btn_enter_manually),
                    color = ContentBase,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        if (uiState.isBottomSheetVisible) {
            ScannerBottomSheet(
                uiState = uiState,
                onDismissRequest = { viewModel.dismissBottomSheet() },
                onCodeChanged = { viewModel.onCodeChanged(it) },
                onAddClick = { viewModel.onAddEquipment() }
            )
        }
    }
}

@Composable
fun CameraPreview(
    onTextFound: (String) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }

    AndroidView(
        factory = { ctx ->
            PreviewView(ctx).apply {
                this.scaleType = PreviewView.ScaleType.FILL_CENTER
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            }
        },
        modifier = Modifier.fillMaxSize(),
        update = { previewView ->
            val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

            cameraProviderFuture.addListener({
                val cameraProvider = cameraProviderFuture.get()

                val preview = Preview.Builder().build().also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }

                @Suppress("DEPRECATION") val imageAnalyzer = ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .setTargetResolution(Size(1280, 720))
                    .build()
                    .also {
                        it.setAnalyzer(cameraExecutor, TextAnalyzer(onTextFound))
                    }

                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    CameraSelector.DEFAULT_BACK_CAMERA,
                    preview,
                    imageAnalyzer
                )
            }, ContextCompat.getMainExecutor(context))
        }
    )
}

@Composable
fun ScannerOverlay() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val overlayColor = TintAccentGhost.copy(alpha = 0.9f)
        val cornerRadius = Dimens.spaceMedium150.toPx()

        val scanAreaWidth = size.width * 0.85f
        val scanAreaHeight = Dimens.spaceExtraXLarge170.toPx()

        val left = (size.width - scanAreaWidth) / 2
        val top = (size.height - scanAreaHeight) / 2
        val right = left + scanAreaWidth
        val bottom = top + scanAreaHeight

        val rectPath = Path().apply {
            addRect(Rect(0f, 0f, size.width, size.height))
        }

        val cutoutPath = Path().apply {
            addRoundRect(
                RoundRect(
                    left = left,
                    top = top,
                    right = right,
                    bottom = bottom,
                    cornerRadius = CornerRadius(cornerRadius, cornerRadius)
                )
            )
        }

        val path = Path.combine(PathOperation.Difference, rectPath, cutoutPath)

        drawPath(
            path = path,
            color = overlayColor,
            style = Fill
        )
    }
}