package com.effitrack

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.effitrack.data.local.UserSession
import com.effitrack.data.remote.RetrofitClient
import com.effitrack.ui.navigation.AppNavigation
import com.effitrack.ui.theme.EffiTrackComposeTheme
import com.effitrack.util.Constants.EMPTY_STRING

class MainActivity : ComponentActivity() {

    private var openStatusScreen by mutableStateOf(false)
    private var errorMessage by mutableStateOf(EMPTY_STRING)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        RetrofitClient.init(this)
        UserSession.init(this)

        handleIntent(intent)

        setContent {
            EffiTrackComposeTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation(
                        openStatusScreen = openStatusScreen,
                        initialErrorMessage = errorMessage,
                        onStatusHandled = {
                            openStatusScreen = false
                            errorMessage = EMPTY_STRING
                        }
                    )
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        if (intent != null && intent.getBooleanExtra("OPEN_STATUS_SCREEN", false)) {
            errorMessage = intent.getStringExtra("ERROR_MESSAGE") ?: EMPTY_STRING
            openStatusScreen = errorMessage.isNotEmpty()
        }
    }
}