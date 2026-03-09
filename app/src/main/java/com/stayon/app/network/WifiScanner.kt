package com.stayon.app.network

import android.annotation.SuppressLint
import android.content.Context
import android.net.wifi.WifiManager
import android.util.Log

class WifiScanner(private val context: Context) {

    private val wifiManager =
        context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
    @android.annotation.SuppressLint("MissingPermission")

/*    fun scanWifi() {


        val results = wifiManager.scanResults
        Log.d("StayOn", "Scan result size: ${results.size}")
        val configuredNetworks = wifiManager.configuredNetworks
        configuredNetworks?.forEach {
            Log.d("StayOn", "Saved network: ${it.SSID}")
        }

        Log.d("StayOn", "Available networks:")

        val bestNetworks = mutableMapOf<String, Int>()

     /*   results.forEach {

                    val ssid = it.SSID
                    val rssi = it.level
            val security = it.capabilities
                    //if (ssid.isNotEmpty()) {
            if (ssid.isNotEmpty() && !security.contains("ESS")) {
                        val existing = bestNetworks[ssid]

                        if (existing == null || rssi > existing) {
                            bestNetworks[ssid] = rssi
                        }
                    }
        }*/
        var bestSSID: String? = null
        var bestRSSI = -100

        results.forEach {

            val ssid = it.SSID
            val rssi = it.level

            if (ssid.isNotEmpty() && rssi > bestRSSI) {
                bestRSSI = rssi
                bestSSID = ssid
            }
        }

        Log.d("StayOn", "Best network: $bestSSID | RSSI: $bestRSSI")
        bestNetworks.forEach {
            Log.d("StayOn", "SSID: ${it.key} | Best RSSI: ${it.value}")

        }
    }*/

    fun scanWifi() {

        val currentSSID = wifiManager.connectionInfo.ssid
        Log.d("StayOn", "Current WiFi: $currentSSID")

        val results = wifiManager.scanResults

        Log.d("StayOn", "Available networks:")

        var bestSSID: String? = null
        var bestRSSI = -100
        var currentRSSI = -100
        val cleanCurrent = currentSSID.replace("\"", "")

        results.forEach {

            val ssid = it.SSID
            val rssi = it.level
            if (it.SSID == cleanCurrent) {
                currentRSSI = it.level
            }
            if (ssid.isNotEmpty() && rssi > bestRSSI) {
                bestRSSI = rssi
                bestSSID = ssid
            }
        }

        Log.d("StayOn", "Best network: $bestSSID | RSSI: $bestRSSI")

        if (bestSSID != null && cleanCurrent != bestSSID) {

            Log.d("StayOn", "Better network available → $bestSSID")

        } else {

            Log.d("StayOn", "Already connected to best network")
        }
        val difference = bestRSSI - currentRSSI

        Log.d("StayOn", "RSSI difference: $difference")
    }
}