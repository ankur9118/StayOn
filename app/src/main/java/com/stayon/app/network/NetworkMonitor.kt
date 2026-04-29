package com.stayon.app.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.util.Log

class NetworkMonitor(private val context: Context) {

    interface Listener {
        fun onNetworkAvailable()
        fun onNetworkLost()
        fun onNetworkCapabilitiesChanged(isWifi: Boolean, isCellular: Boolean)
    }

    private val TAG = "StayOnNetwork"
    private val listeners = mutableSetOf<Listener>()
    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    private lateinit var networkCallback: ConnectivityManager.NetworkCallback

    fun addListener(listener: Listener) {
        listeners.add(listener)
    }

    fun removeListener(listener: Listener) {
        listeners.remove(listener)
    }

    fun startMonitoring() {
        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()
        networkCallback = object : ConnectivityManager.NetworkCallback() {

            override fun onAvailable(network: Network) {
                Log.d(TAG, "Network available: $network")
                Log.d("StayOn", "Network became available")
                listeners.forEach { it.onNetworkAvailable() }
            }

            override fun onLost(network: Network) {
                Log.d(TAG, "Network lost: $network")
                Log.d("StayOn", "Network disconnected")
                listeners.forEach { it.onNetworkLost() }
            }

            override fun onCapabilitiesChanged(
                network: Network,
                networkCapabilities: NetworkCapabilities
            ) {
                val wifi = networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                val cellular = networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)

                if (wifi) {
                    Log.d("StayOn", "WiFi connected")
                }

                if (cellular) {
                    Log.d("StayOn", "Mobile data connected")
                }

                listeners.forEach { it.onNetworkCapabilitiesChanged(wifi, cellular) }
            }
        }

        connectivityManager.registerNetworkCallback(request, networkCallback)
    }

    fun stopMonitoring() {
        try {
            connectivityManager.unregisterNetworkCallback(networkCallback)
        } catch (ignored: Exception) {
            Log.w(TAG, "Failed to unregister network callback", ignored)
        }

        Log.d("StayOn", "Network monitor stopped")
    }
}