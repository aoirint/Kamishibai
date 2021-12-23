package com.aoirint.kamishibai

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
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
import com.aoirint.kamishibai.utility.UriUtility
import com.aoirint.kamishibai.components.MusicList
import com.aoirint.kamishibai.components.SelectMusicButton
import com.aoirint.kamishibai.musicplayer.*
import com.aoirint.kamishibai.musicregistry.Music
import com.aoirint.kamishibai.musicregistry.MusicViewModel
import com.aoirint.kamishibai.musicregistry.MusicViewModelFactory
import com.aoirint.kamishibai.ui.theme.KamishibaiTheme
import com.aoirint.kamishibai.utility.BitmapUtility
import java.time.ZonedDateTime

class MainActivity : ComponentActivity() {
    companion object {
        const val TAG = "MainActivity"
    }

    private val musicViewModel: MusicViewModel by viewModels {
        MusicViewModelFactory((application as KamishibaiApp).repository)
    }

    var musicPlayerListener: MusicPlayerListener? = null
    val musicPlayer: MusicPlayer
        get() = (application as KamishibaiApp).musicPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val (music, setMusic) = rememberSaveable { mutableStateOf<Music?>(null) }
            val (musics, setMusics) = rememberSaveable { mutableStateOf<List<Music>>(emptyList()) }

            musicViewModel.allMusics.observe(this@MainActivity, { retMusics ->
                Log.d(TAG, "musics fetched")
                setMusics(retMusics)
            })

            musicPlayerListener = object : MusicPlayerListener {
                override fun onMusicChanged(oldContext: Music?, newContext: Music?) {
                    setMusic(newContext)
                    (application as KamishibaiApp).sendUpdateNotification()
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
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text("Music")
                            Spacer(Modifier.size(8.dp))
                            Text(music?.title ?: "No Selected")
                        }
                        Spacer(Modifier.size(8.dp))
                        Button(
                            onClick = {
                                Log.d(TAG, "Play Music")
                                musicPlayer.start()
                                (application as KamishibaiApp).sendUpdateNotification()
                            },
                        ) {
                            Text("Play Music")
                        }
                        Spacer(Modifier.size(8.dp))
                        Button(
                            onClick = {
                                Log.d(TAG, "Pause Music")
                                musicPlayer.pause()
                                (application as KamishibaiApp).sendUpdateNotification()
                            },
                        ) {
                            Text("Pause Music")
                        }
                        Spacer(Modifier.size(8.dp))
                        Button(
                            onClick = {
                                Log.d(TAG, "Stop Music")
                                musicPlayer.stopAndRelease()
                                (application as KamishibaiApp).sendUpdateNotification()
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
                            Text(music?.title ?: "")
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text("Album")
                            Spacer(Modifier.size(8.dp))
                            Text(music?.album ?: "")
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text("Artist")
                            Spacer(Modifier.size(8.dp))
                            Text(music?.artist ?: "")
                        }
                        Spacer(Modifier.size(8.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            SelectMusicButton(
                                title = "Add Music",
                                multiple = true,
                                onSelected = { fileUris: List<Uri> ->
                                    fileUris.forEach { fileUri ->
                                        // Persistence URI Access Permission
                                        val contentResolver = applicationContext.contentResolver
                                        val takeFlags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                                        contentResolver.takePersistableUriPermission(fileUri, takeFlags)

                                        // TODO: validate uri is music
                                        val meta = MusicMetadataUtility.loadMusicMetaDataFromUri(this@MainActivity, fileUri)
                                        val lastModified = UriUtility.queryLastModified(this@MainActivity, fileUri)

                                        val artwork = (application as KamishibaiApp).artworkCacheManager.loadOrCreate(fileUri)
                                        val commonColor = artwork?.let { BitmapUtility.extractCommonColor(artwork) }

                                        val music = Music(
                                            id = 0,
                                            uri = fileUri,
                                            title = meta.title,
                                            album = meta.album,
                                            artist = meta.artist,
                                            color = commonColor,
                                            lastModified = lastModified,
                                            createdAt = ZonedDateTime.now(),
                                            updatedAt = ZonedDateTime.now(),
                                        )

                                        musicViewModel.insert(music) { success ->
                                            Log.d(TAG, "Success: $success")
                                        }
                                    }
                                },
                            )
                        }
                        Column(modifier = Modifier.height(140.dp)) {
                            MusicList(
                                musics = musics,
                                onClick = { music ->
                                    Log.d(TAG, "Clicked: ${music.title}")

                                    musicPlayer.stopAndRelease()
                                    musicPlayer.setListenerAsWeakRef(musicPlayerListener)
                                    musicPlayer.setMusic(music)
                                },
                            )
                        }
                    }
                }
            }
        }
    }
}
