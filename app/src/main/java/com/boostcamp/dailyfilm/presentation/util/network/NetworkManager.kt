package com.boostcamp.dailyfilm.presentation.util.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.NetworkCallback
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest

object NetworkManager {
    private lateinit var connectivityManager: ConnectivityManager
    private lateinit var networkRequest: NetworkRequest
    private lateinit var network: Network
    lateinit var actNetwork: NetworkCapabilities

    fun initNetwork(context: Context?) {
        context ?: return
        connectivityManager = context.getSystemService(ConnectivityManager::class.java)
        networkRequest = NetworkRequest.Builder()
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
            .build()
    }

    fun checkNetwork(): NetworkState {
        network = connectivityManager.activeNetwork ?: return NetworkState.LOST
        actNetwork = connectivityManager.getNetworkCapabilities(network) ?: return NetworkState.LOST

        return actNetwork.let { network ->
            when {
                network.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> NetworkState.AVAILABLE
                network.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> NetworkState.AVAILABLE
                else -> NetworkState.LOST
            }
        }
    }

    fun registerNetworkCallback(networkCallback: NetworkCallback) {
        connectivityManager.registerNetworkCallback(networkRequest, networkCallback)
    }

    fun terminateNetworkCallback(networkCallback: NetworkCallback) {
        connectivityManager.unregisterNetworkCallback(networkCallback)
    }
}