package com.stayon.app.model

data class NetworkInfo(
    val ssid: String,
    val rssi: Int,
    val signalLevel: SignalLevel,
    val isConnected: Boolean,
    val isSaved: Boolean
)