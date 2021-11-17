package com.aoirint.kamishibai

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.aoirint.kamishibai.ui.theme.KamishibaiTheme

class MainActivity : ComponentActivity() {
    companion object {
        const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KamishibaiTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    Column(
                        Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.Center,
                    ) {
                        Greeting("Android")
                        Spacer(Modifier.size(8.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Button(
                                onClick = {
                                    Log.d(TAG, "Set Music")

                                },
                            ) {
                                Text("Set Music")
                            }
                            Spacer(Modifier.size(8.dp))
                            Text("No selected")
                        }
                        Spacer(Modifier.size(8.dp))
                        Button(
                            onClick = {
                                Log.d(TAG, "Play Music")

                            },
                        ) {
                            Text("Play Music")
                        }
                        Spacer(Modifier.size(8.dp))
                        Button(
                            onClick = {
                                Log.d(TAG, "Stop Music")

                            },
                        ) {
                            Text("Stop Music")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    KamishibaiTheme {
        Greeting("Android")
    }
}