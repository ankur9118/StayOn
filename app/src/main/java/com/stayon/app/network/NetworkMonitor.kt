package com.stayon.app.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.util.Log

class NetworkMonitor(private val context: Context) {

    private val TAG = "StayOnNetwork"

    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    private lateinit var networkCallback: ConnectivityManager.NetworkCallback

    fun startMonitoring() {
        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()
        networkCallback = object : ConnectivityManager.NetworkCallback() {

            override fun onAvailable(network: Network) {
                Log.d(TAG, "Network available: $network")
                Log.d("StayOn", "Network became available")
            }

            override fun onLost(network: Network) {
                Log.d(TAG, "Network lost: $network")
                Log.d("StayOn", "Network disconnected")
            }

            override fun onCapabilitiesChanged(
                network: Network,
                networkCapabilities: NetworkCapabilities
            ) {

                if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    Log.d("StayOn", "WiFi connected")
                }

                if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                    Log.d("StayOn", "Mobile data connected")
                }
            }
        }

        connectivityManager.registerNetworkCallback(request, networkCallback)
    }

    fun stopMonitoring() {

        connectivityManager.unregisterNetworkCallback(networkCallback)

        Log.d("StayOn", "Network monitor stopped")
    }
}