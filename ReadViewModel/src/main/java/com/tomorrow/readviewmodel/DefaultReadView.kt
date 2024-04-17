package com.tomorrow.readviewmodel

import android.widget.Toast
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.tomorrow.readviewmodel.components.GeneralError
import com.tomorrow.readviewmodel.components.Loader
import com.tomorrow.readviewmodel.utils.Loaded


/**
 * has error handling and loading
 * */
@Composable
fun <D> DefaultReadView(
    viewModel: ReadViewModel<D>,
    loader: @Composable () -> Unit = { Loader() },
    error: @Composable (String) -> Unit = {
        GeneralError(
            modifier = Modifier.padding(16.dp),
            message = it,
            description = "Please check your internet connection and try again.",
            onButtonClick = { viewModel.on(ReadViewModel.Event.OnRefresh) },
        )
    },
    emptyState: @Composable () -> Unit = {
        GeneralError(
            modifier = Modifier.padding(16.dp),
            message = "No data found",
            description = "Stay Tuned for more updates!",
            onButtonClick = { viewModel.on(ReadViewModel.Event.OnRefresh) },
        )
    },
    view: @Composable (D) -> Unit,
) {
    val context = LocalContext.current

    LaunchedEffect(key1 = viewModel.state.error) {
        viewModel.state.error?.let {
            if (it.isNotEmpty()) Toast.makeText(context, it, Toast.LENGTH_LONG).show()
        }
    }

    DisposableEffect(key1 = "") {
        onDispose { viewModel.on(ReadViewModel.Event.OnDismiss) }
    }

    val viewData = viewModel.state.viewData

    if (viewModel.state.isLoading) loader()
    else if (viewModel.state.isEmpty && viewData is Loaded) emptyState()
    else if (viewData is Loaded) {
        view(viewData.get())
    } else if (viewModel.state.error != null) error(viewModel.state.error ?: "")
    else loader()
}