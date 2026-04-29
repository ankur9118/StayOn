package com.stayon.app.config

data class StayOnConfig(
    val rssiThreshold: Int = 10,
    val switchCooldown: Long = 15000L, // 15 seconds
    val connectionTimeout: Long = 5000L  // 5 seconds
)