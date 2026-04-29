package com.stayon.app.ui
import androidx.compose.material3.Button
import androidx.compose.runtime.*
import androidx.compose.material3.Surface
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.LocalContext
import com.stayon.app.network.NetworkMonitor

import androidx.compose.material3.ButtonDefaults
import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.*
import android.Manifest
import androidx.activity.result.contract.ActivityResultContracts
import com.stayon.app.network.WifiScanner
import androidx.activity.result.ActivityResultLauncher

class MainActivity : ComponentActivity() {
    private val locationPermission =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) {}
    private val wifiPermission =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //enableEdgeToEdge()
        val wifiScanner = WifiScanner(this)
        setContent {

            Surface {
                locationPermission.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                wifiPermission.launch(Manifest.permission.NEARBY_WIFI_DEVICES)
                HomeScreen()

                    // Greeting("Android")

                }// Both will appear
            }
        }


        }


    @Composable
    fun HomeScreen() {

        val context = LocalContext.current
        val monitor = remember { NetworkMonitor(context) }
        val wifiScanner = WifiScanner(context)
        var stayOnEnabled by remember { mutableStateOf(false) }



        Button(onClick = {

            stayOnEnabled = !stayOnEnabled
            //val monitor = remember{ NetworkMonitor(context) }
            if (stayOnEnabled) {
                monitor.startMonitoring()
                wifiScanner.scanWifi()
            } else {
                monitor.stopMonitoring()
            }

            Log.d("StayOn", "StayOn enabled: $stayOnEnabled")

        },
            colors = ButtonDefaults.buttonColors(
                containerColor = if (stayOnEnabled) Color.Green else Color.Red
            )
        ) {
            Text(if (stayOnEnabled) "StayOn ON" else "StayOn OFF")
        }        }