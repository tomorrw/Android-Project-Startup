package com.tomorrow.internetconnectivity


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@Composable
fun ConnectivityStatus() {
    val connection by connectivityState()

    if (connection === ConnectionState.Unavailable) {

        Text(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = Color.Red
                ),
            style = MaterialTheme.typography.labelLarge.copy(textAlign = TextAlign.Center),
            text = "No connection!"
        )
    }
}