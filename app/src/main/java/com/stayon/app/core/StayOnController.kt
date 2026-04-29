package com.stayon.app.core

import android.util.Log
import com.stayon.app.config.StayOnConfig
import com.stayon.app.engine.DecisionEngine
import com.stayon.app.engine.SwitchController
import com.stayon.app.model.NetworkInfo
import com.stayon.app.network.NetworkMonitor
import com.stayon.app.network.WifiScanner

class StayOnController(
    private val monitor: NetworkMonitor,
    private val wifiScanner: WifiScanner,
    private val decisionEngine: DecisionEngine,
    private val switchController: SwitchController,
    private val config: StayOnConfig
) : NetworkMonitor.Listener {

    private val TAG = "StayOnController"

    var state: StayOnState = StayOnState.IDLE
        private set

    private var scanInProgress = false

    fun start() {
        if (state != StayOnState.IDLE) {
            Log.d(TAG, "Already started. Current state: $state")
            return
        }

        Log.d(TAG, "Starting StayOn")
        monitor.addListener(this)
        monitor.startMonitoring()
        state = StayOnState.MONITORING
    }

    fun stop() {
        if (state == StayOnState.IDLE) {
            Log.d(TAG, "Already stopped")
            return
        }

        Log.d(TAG, "Stopping StayOn")
        monitor.removeListener(this)
        monitor.stopMonitoring()
        cleanup()
        state = StayOnState.IDLE
    }

    override fun onNetworkAvailable() {
        Log.d(TAG, "Network available event received")
        triggerScan("available")
    }

    override fun onNetworkLost() {
        Log.d(TAG, "Network lost event received")
        triggerScan("lost")
    }

    override fun onNetworkCapabilitiesChanged(isWifi: Boolean, isCellular: Boolean) {
        Log.d(TAG, "Network capabilities changed: isWifi=$isWifi, isCellular=$isCellular")
        triggerScan("capabilities")
    }

    private fun triggerScan(reason: String) {
        if (state == StayOnState.SWITCHING) {
            Log.d(TAG, "Skipping scan while switching")
            return
        }

        if (scanInProgress) {
            Log.d(TAG, "Duplicate scan prevented: already scanning")
            return
        }

        Log.d(TAG, "Triggering scan because: $reason")
        scanNetworks()
    }

    private fun scanNetworks() {
        scanInProgress = true
        state = StayOnState.SCANNING

        try {
            val currentNetwork = wifiScanner.getCurrentNetworkInfo()
            val availableNetworks = wifiScanner.scanWifi()

            Log.d(TAG, "Scan complete: ${availableNetworks.size} networks found")
            evaluateNetworks(currentNetwork, availableNetworks)
        } catch (exception: Exception) {
            Log.w(TAG, "WiFi scan failed", exception)
            completeScan()
        }
    }

    private fun evaluateNetworks(currentNetwork: NetworkInfo?, availableNetworks: List<NetworkInfo>) {
        val targetNetwork = decisionEngine.chooseBestNetwork(currentNetwork, availableNetworks, config.rssiThreshold)

        if (targetNetwork == null) {
            Log.d(TAG, "No better network chosen")
            completeScan()
            return
        }

        if (!switchController.canSwitch()) {
            Log.d(TAG, "Switch cooldown active. Skipping switch to ${targetNetwork.ssid}")
            completeScan()
            return
        }

        Log.d(TAG, "Switching to target network: ${targetNetwork.ssid}")
        state = StayOnState.SWITCHING
        switchController.attemptSwitch(targetNetwork, null) { success ->
            Log.d(TAG, "Switch to ${targetNetwork.ssid} completed: $success")
            completeScan()
        }
    }

    private fun completeScan() {
        scanInProgress = false
        if (state != StayOnState.IDLE) {
            state = StayOnState.MONITORING
        }
        Log.d(TAG, "StayOn state restored to $state")
    }

    private fun cleanup() {
        scanInProgress = false
        switchController.cleanup()
    }
}
