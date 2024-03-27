package com.tomorrow.internetconnectivity

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.ExperimentalCoroutinesApi

@OptIn(ExperimentalCoroutinesApi::class)
@Composable
fun ConnectivityStatusWrapper(
    modifier: Modifier = Modifier,
    surfaceColor: Color = MaterialTheme.colorScheme.surface,
    content: @Composable (ColumnScope) -> Unit
) = Column(modifier) {
    val systemUiController = rememberSystemUiController()

    LaunchedEffect(key1 = "") {
        systemUiController.setNavigationBarColor(surfaceColor)
        systemUiController.setSystemBarsColor(surfaceColor)
    }

    ConnectivityStatus()

    content(this)
}