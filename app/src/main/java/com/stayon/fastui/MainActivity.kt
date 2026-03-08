package com.stayon.fastui
import androidx.compose.material3.Button
import androidx.compose.runtime.*
import androidx.compose.material3.Surface
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.stayon.fastui.ui.theme.FastUITheme

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