package com.stayon.app.engine

import com.stayon.app.model.NetworkInfo
import com.stayon.app.model.SignalLevel

class DecisionEngineTest {

    fun runTests() {
        val decisionEngine = DecisionEngine()
        val threshold = 10

        // Sample networks
        val network1 = NetworkInfo("Network1", -40, SignalLevel.EXCELLENT, false, true) // Best RSSI
        val network2 = NetworkInfo("Network2", -50, SignalLevel.STRONG, false, true)
        val network3 = NetworkInfo("Network3", -60, SignalLevel.STRONG, false, true)
        val network4 = NetworkInfo("Network4", -70, SignalLevel.MEDIUM, false, false) // Not saved
        val availableNetworks = listOf(network1, network2, network3, network4)

        // Test 1: No current network
        val result1 = decisionEngine.chooseBestNetwork(null, availableNetworks, threshold)
        println("Test 1 (No current network): Selected ${result1?.ssid ?: "None"}")

        // Test 2: Current network weaker than best by threshold
        val currentWeak = NetworkInfo("CurrentWeak", -80, SignalLevel.WEAK, true, true)
        val result2 = decisionEngine.chooseBestNetwork(currentWeak, availableNetworks, threshold)
        println("Test 2 (Current weaker by threshold): Selected ${result2?.ssid ?: "None"}")

        // Test 3: Current network slightly weaker (below threshold)
        val currentSlight = NetworkInfo("CurrentSlight", -45, SignalLevel.STRONG, true, true)
        val result3 = decisionEngine.chooseBestNetwork(currentSlight, availableNetworks, threshold)
        println("Test 3 (Current slightly weaker): Selected ${result3?.ssid ?: "None"}")
        // Test 4: No saved networks
val noSaved = listOf(
    NetworkInfo("Open1", -40, SignalLevel.EXCELLENT, false, false)
)
val result4 = decisionEngine.chooseBestNetwork(null, noSaved, threshold)
println("Test 4 (No saved networks): Selected ${result4?.ssid ?: "None"}")
    }
}