package com.stayon.app
import androidx.compose.material3.Button
import androidx.compose.runtime.*
import androidx.compose.material3.Surface
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf

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

           /*FastUITheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}*/




    @Composable
    fun HomeScreen() {
        var count by remember{mutableStateOf(0)}

        Column {

           // Text("Be Online Always")

            Button(onClick = {count++}) {
                Column {
                Text("Click me")
                Text("clicks : $count")
            }
            }
        }
    }