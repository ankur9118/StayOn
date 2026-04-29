package com.stayon.app.network

import android.annotation.SuppressLint
import android.content.Context
import android.net.wifi.WifiManager
import android.util.Log
import com.stayon.app.model.NetworkInfo
import com.stayon.app.model.SignalLevel

class WifiScanner(private val context: Context) {

    private val wifiManager =
        context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

    @SuppressLint("MissingPermission")
    fun scanWifi(): List<NetworkInfo> {
        val configuredSsids = wifiManager.configuredNetworks
            ?.mapNotNull { it.SSID?.replace('"', '') }
            ?.toSet()
            .orEmpty()

        val currentSsid = wifiManager.connectionInfo.ssid?.replace('"', '')
        val results = wifiManager.scanResults.orEmpty()

        Log.d("StayOn", "Scanning WiFi: currentSsid=$currentSsid, results=${results.size}")

        return results.mapNotNull { result ->
            val ssid = result.SSID
            if (ssid.isEmpty()) return@mapNotNull null

            NetworkInfo(
                ssid = ssid,
                rssi = result.level,
                signalLevel = SignalLevel.fromRssi(result.level),
                isConnected = ssid == currentSsid,
                isSaved = configuredSsids.contains(ssid)
            )
        }.also { networks ->
            networks.forEach {
                Log.d(
                    "StayOn",
                    "Found network: ${it.ssid}, rssi=${it.rssi}, saved=${it.isSaved}, connected=${it.isConnected}"
                )
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun getCurrentNetworkInfo(): NetworkInfo? {
        val ssid = wifiManager.connectionInfo.ssid?.replace('"', '') ?: return null
        val rssi = wifiManager.connectionInfo.rssi
        val configuredSsids = wifiManager.configuredNetworks
            ?.mapNotNull { it.SSID?.replace('"', '') }
            ?.toSet()
            .orEmpty()

        return NetworkInfo(
            ssid = ssid,
            rssi = rssi,
            signalLevel = SignalLevel.fromRssi(rssi),
            isConnected = true,
            isSaved = configuredSsids.contains(ssid)
        )
    }
}
