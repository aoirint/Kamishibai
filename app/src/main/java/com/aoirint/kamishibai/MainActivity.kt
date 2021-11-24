package com.aoirint.kamishibai

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aoirint.kamishibai.musicplayer.MusicMetadata
import com.aoirint.kamishibai.musicplayer.MusicPlayer
import com.aoirint.kamishibai.musicplayer.MusicPlayerListener
import com.aoirint.kamishibai.musicplayer.OnMusicChangedContext
import com.aoirint.kamishibai.ui.theme.KamishibaiTheme

class MainActivity : ComponentActivity() {
    companion object {
        const val TAG = "MainActivity"
    }

    var musicPlayerListener: MusicPlayerListener? = null

    fun getMusicPlayer(): MusicPlayer {
        return MusicPlayer.getInstance(this@MainActivity)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val (musicUri, setMusicUri) = rememberSaveable { mutableStateOf<Uri?>(null) }
            val (musicMetadata, setMusicMetadata) = rememberSaveable { mutableStateOf<MusicMetadata?>(null) }

            musicPlayerListener = object : MusicPlayerListener {
                override fun onMusicChanged(oldContext: OnMusicChangedContext?, newContext: OnMusicChangedContext?) {
                    setMusicUri(newContext?.uri)
                    setMusicMetadata(newContext?.metadata)
                }
            }

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
                            SelectMusicButton(onSelected = { fileUri: Uri ->
                                // TODO: validate uri is music
                                getMusicPlayer().setListenerAsWeakRef(musicPlayerListener)
                                getMusicPlayer().setMusicUri(fileUri)
                            })
                            Spacer(Modifier.size(8.dp))
                            Text(musicUri?.path ?: "No Selected")
                        }
                        Spacer(Modifier.size(8.dp))
                        Button(
                            onClick = {
                                Log.d(TAG, "Play Music")
                                getMusicPlayer().start()
                            },
                        ) {
                            Text("Play Music")
                        }
                        Spacer(Modifier.size(8.dp))
                        Button(
                            onClick = {
                                Log.d(TAG, "Pause Music")
                                getMusicPlayer().pause()
                            },
                        ) {
                            Text("Pause Music")
                        }
                        Spacer(Modifier.size(8.dp))
                        Button(
                            onClick = {
                                Log.d(TAG, "Stop Music")
                                getMusicPlayer().stopAndRelease()
                            },
                        ) {
                            Text("Stop Music")
                        }
                        Spacer(Modifier.size(8.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text("Title")
                            Spacer(Modifier.size(8.dp))
                            Text(musicMetadata?.title ?: "")
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text("Album")
                            Spacer(Modifier.size(8.dp))
                            Text(musicMetadata?.album ?: "")
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text("Artist")
                            Spacer(Modifier.size(8.dp))
                            Text(musicMetadata?.artist ?: "")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SelectMusicButton(onSelected: (fileUri: Uri) -> Unit) {
    val selectMusic = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { fileUri: Uri ->
        onSelected(fileUri)
    }

    Button(
        onClick = {
            Log.d(MainActivity.TAG, "Set Music")
            selectMusic.launch("audio/*")
        },
    ) {
        Text("Set Music")
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