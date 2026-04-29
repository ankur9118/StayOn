package com.stayon.app.engine

import com.stayon.app.model.NetworkInfo

class DecisionEngine {

    fun chooseBestNetwork(
        currentNetwork: NetworkInfo?,
        availableNetworks: List<NetworkInfo>,
        threshold: Int
    ): NetworkInfo? {
        // Filter only saved networks
        val savedNetworks = availableNetworks.filter { it.isSaved }

        // Find network with best RSSI (highest RSSI value)
        val bestNetwork = savedNetworks.maxByOrNull { it.rssi }

        return when {
            bestNetwork == null -> null // No saved networks
            currentNetwork == null -> bestNetwork // No current network, return best
            bestNetwork.rssi > currentNetwork.rssi + threshold -> bestNetwork // Best is significantly better
            else -> null // No switch needed
        }
    }
}