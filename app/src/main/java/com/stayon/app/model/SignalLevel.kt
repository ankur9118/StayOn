package com.stayon.app.model

enum class SignalLevel {
    EXCELLENT,
    STRONG,
    MEDIUM,
    WEAK;

    companion object {
        fun fromRssi(rssi: Int): SignalLevel {
            return when {
                rssi >= -50 -> EXCELLENT
                rssi >= -60 -> STRONG
                rssi >= -70 -> MEDIUM
                else -> WEAK
            }
        }
    }
}