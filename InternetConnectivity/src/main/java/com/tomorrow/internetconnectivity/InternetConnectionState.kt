package com.tomorrow.internetconnectivity

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.produceState
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow


sealed class ConnectionState {
    object Available : ConnectionState()
    object Unavailable : ConnectionState()
    object Connecting : ConnectionState()
}

/**
 * Network utility to get current state of internet connection
 */
val Context.currentConnectivityState: ConnectionState
    get() {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return getConnectivityStatus((connectivityManager))
    }

fun getConnectivityStatus(connectivityManager: ConnectivityManager?): ConnectionState {
    if (connectivityManager == null) return ConnectionState.Connecting

    return if (isInternetAvailable(connectivityManager)) ConnectionState.Available else ConnectionState.Unavailable
}


fun isInternetAvailable(connectivityManager: ConnectivityManager): Boolean {
    var result = false

    // activeNetworkInfo is deprecated in new version
    connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)?.run {
        result = hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                || hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
                || hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
    }

    return result
}


@ExperimentalCoroutinesApi
fun Context.observeConnectivityAsFlow() = callbackFlow {
    val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    val callback = networkCallback { trySend(it) }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
        connectivityManager.registerDefaultNetworkCallback(callback)
    else connectivityManager.registerNetworkCallback(NetworkRequest.Builder().build(), callback)


    // Set current state
    val currentState = getConnectivityStatus(connectivityManager)
    trySend(currentState)

    // Remove callback when not used
    awaitClose { connectivityManager.unregisterNetworkCallback(callback) }
}

fun networkCallback(callback: (ConnectionState) -> Unit): ConnectivityManager.NetworkCallback {
    return object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            callback(ConnectionState.Available)
        }

        override fun onLost(network: Network) {
            callback(ConnectionState.Unavailable)
        }

        override fun onUnavailable() {
            callback(ConnectionState.Unavailable)
        }
    }
}

@ExperimentalCoroutinesApi
@Composable
fun connectivityState(): State<ConnectionState> {
    val context = LocalContext.current

    // Creates a State<ConnectionState> with current connectivity state as initial value
    return produceState(initialValue = context.currentConnectivityState) {
        // In a coroutine, can make suspend calls
        context.observeConnectivityAsFlow().collect { value = it }
    }
}


