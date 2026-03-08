package com.stayon.app
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

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //enableEdgeToEdge()
        setContent {

            Surface {

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
        var stayOnEnabled by remember { mutableStateOf(false) }

        Button(onClick = {

            stayOnEnabled = !stayOnEnabled
            //val monitor = remember{ NetworkMonitor(context) }
            if (stayOnEnabled) {
                monitor.startMonitoring()
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
