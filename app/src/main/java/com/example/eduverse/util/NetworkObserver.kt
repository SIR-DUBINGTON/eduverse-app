package com.example.eduverse.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Network
import android.net.NetworkRequest
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*

/**
 * @brief Represents the status of a network connection.
 * This enum class defines two possible states for a network connection:
 * - [Available]: The network connection is currently available.
 * - [Unavailable]: The network connection is currently unavailable.
 */
enum class ConnectionStatus { Available, Unavailable }


/**
 * @brief Observes the status of a network connection.
 */
class NetworkObserver(context: Context) {
    private val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    val status: Flow<ConnectionStatus> = callbackFlow {
        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                trySend(ConnectionStatus.Available)
            }
            override fun onLost(network: Network) {
                trySend(ConnectionStatus.Unavailable)
            }
        }

        cm.registerNetworkCallback(
            NetworkRequest.Builder().build(),
            callback
        )
        val hasNet = cm.getNetworkCapabilities(cm.activeNetwork)?.run {
            hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
                    || hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                    || hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
        } ?: false

        trySend(if (hasNet) ConnectionStatus.Available else ConnectionStatus.Unavailable)

        awaitClose { cm.unregisterNetworkCallback(callback) }
    }
        .distinctUntilChanged()
        .conflate()
}
