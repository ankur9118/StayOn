package com.stayon.app.engine

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.net.wifi.WifiManager
import android.net.wifi.WifiNetworkSpecifier
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.stayon.app.config.StayOnConfig
import com.stayon.app.model.NetworkInfo

class SwitchController(
    private val context: Context,
    private val config: StayOnConfig,
    private val connectivityManager: ConnectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager,
    private val timeProvider: () -> Long = System::currentTimeMillis
) {

    private val TAG = "StayOnSwitch"
    private val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
    private var lastSwitchMillis: Long = 0L
    private var currentRequestCallback: ConnectivityManager.NetworkCallback? = null
    private var timeoutHandler: Handler? = null
    private var timeoutRunnable: Runnable? = null

    fun canSwitch(): Boolean {
        return timeProvider() - lastSwitchMillis >= config.switchCooldown
    }

    fun isCooldownActive(): Boolean {
        return !canSwitch()
    }

    /**
     * Attempt to switch to the target WiFi network using WifiNetworkSpecifier.
     * Password is optional, but note that this approach only supports networks
     * compatible with WifiNetworkSpecifier and the platform restrictions.
     */
    fun attemptSwitch(
        targetNetwork: NetworkInfo,
        password: String? = null,
        onResult: (success: Boolean) -> Unit
    ) {
        Log.d(TAG, "Attempting switch to ${targetNetwork.ssid}")

        if (targetNetwork.isConnected) {
            Log.d(TAG, "Target network already connected: ${targetNetwork.ssid}")
            onResult(true)
            return
        }

        if (!canSwitch()) {
            Log.d(TAG, "Switch cooldown active, skipping switch")
            onResult(false)
            return
        }

        lastSwitchMillis = timeProvider()
        cancelPendingRequest()

        val specifier = buildNetworkSpecifier(targetNetwork.ssid, password)
        if (specifier == null) {
            Log.w(TAG, "Cannot build network specifier for ${targetNetwork.ssid}")
            onResult(false)
            return
        }

        Log.d(TAG, "Requesting network: ${targetNetwork.ssid}")
        val request = NetworkRequest.Builder()
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .setNetworkSpecifier(specifier)
            .build()

        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                Log.d(TAG, "Network available: ${targetNetwork.ssid}")
                verifyConnection(targetNetwork.ssid, onResult)
            }

            override fun onUnavailable() {
                Log.w(TAG, "Network unavailable: ${targetNetwork.ssid}")
                onResult(false)
                cancelPendingRequest()
            }

            override fun onLost(network: Network) {
                Log.w(TAG, "Network lost before verification: ${targetNetwork.ssid}")
                onResult(false)
                cancelPendingRequest()
            }
        }

        currentRequestCallback = callback
        connectivityManager.requestNetwork(request, callback)
        scheduleTimeout(targetNetwork.ssid, onResult)
    }

    @SuppressLint("MissingPermission")
    private fun verifyConnection(targetSsid: String, onResult: (Boolean) -> Unit) {
        val activeNetwork = connectivityManager.activeNetwork
        val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork)
        val isWifi = capabilities?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true
        val hasInternet = capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
        val connectedSsid = getConnectedSsid()
        val ssidMatches = connectedSsid?.equals(targetSsid, ignoreCase = false) == true

        Log.d(
            TAG,
            "Verifying connection for $targetSsid: connectedSsid=$connectedSsid, isWifi=$isWifi, hasInternet=$hasInternet, ssidMatches=$ssidMatches"
        )

        if (isWifi && hasInternet && ssidMatches) {
            Log.d(TAG, "Connection verification passed for $targetSsid")
            onResult(true)
        } else {
            Log.w(TAG, "Connection verification failed for $targetSsid")
            onResult(false)
        }

        cancelPendingRequest()
    }

    private fun getConnectedSsid(): String? {
        return try {
            wifiManager.connectionInfo?.ssid?.replace("\"", "")
        } catch (exception: Exception) {
            Log.w(TAG, "Unable to read current SSID", exception)
            null
        }
    }

    private fun scheduleTimeout(targetSsid: String, onResult: (Boolean) -> Unit) {
        Log.d(TAG, "Scheduling timeout for $targetSsid: ${config.connectionTimeout}ms")
        timeoutHandler = Handler(Looper.getMainLooper())
        val runnable = Runnable {
            Log.w(TAG, "Connection timeout after ${config.connectionTimeout}ms for $targetSsid")
            onResult(false)
            cancelPendingRequest()
        }
        timeoutRunnable = runnable
        timeoutHandler?.postDelayed(runnable, config.connectionTimeout)
    }

    private fun cancelPendingRequest() {
        timeoutRunnable?.let { timeoutHandler?.removeCallbacks(it) }
        timeoutRunnable = null
        timeoutHandler = null

        currentRequestCallback?.let {
            try {
                connectivityManager.unregisterNetworkCallback(it)
            } catch (ignored: Exception) {
                Log.w(TAG, "Failed to unregister network callback", ignored)
            }
        }
        currentRequestCallback = null
    }

    private fun buildNetworkSpecifier(ssid: String, password: String?): WifiNetworkSpecifier? {
        return try {
            val builder = WifiNetworkSpecifier.Builder()
                .setSsid(ssid)

            if (!password.isNullOrBlank()) {
                builder.setWpa2Passphrase(password)
            }

            builder.build()
        } catch (exception: Exception) {
            Log.w(TAG, "Could not create WifiNetworkSpecifier", exception)
            null
        }
    }

    fun cleanup() {
        cancelPendingRequest()
    }
}
