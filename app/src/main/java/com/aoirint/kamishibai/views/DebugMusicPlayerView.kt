package com.aoirint.kamishibai.views

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aoirint.kamishibai.KamishibaiApp
import com.aoirint.kamishibai.MainActivity
import com.aoirint.kamishibai.components.MusicList
import com.aoirint.kamishibai.components.SelectMusicButton
import com.aoirint.kamishibai.musicplayer.MusicMetadataUtility
import com.aoirint.kamishibai.musicplayer.MusicPlayer
import com.aoirint.kamishibai.musicplayer.MusicPlayerListener
import com.aoirint.kamishibai.musicregistry.Music
import com.aoirint.kamishibai.musicregistry.MusicViewModel
import com.aoirint.kamishibai.musicregistry.MusicViewModelFactory
import com.aoirint.kamishibai.utility.BitmapUtility
import com.aoirint.kamishibai.utility.UriUtility
import java.time.ZonedDateTime

@Composable
fun DebugMusicPlayerView(
    context: Context = LocalContext.current,
    musicViewModel: MusicViewModel = viewModel(
        factory=MusicViewModelFactory((context.applicationContext as KamishibaiApp).repository)
    ),
    musicPlayer: MusicPlayer = (context.applicationContext as KamishibaiApp).musicPlayer,
) {
    val musics = musicViewModel.allMusics.observeAsState()
    val (music, setMusic) = rememberSaveable { mutableStateOf<Music?>(null) }

    val musicPlayerListener = object : MusicPlayerListener {
        override fun onMusicChanged(oldContext: Music?, newContext: Music?) {
            setMusic(newContext)
            (context.applicationContext as KamishibaiApp).sendUpdateNotification()
        }
    }


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
                Log.d(MainActivity.TAG, "Play Music")
                musicPlayer.start()
            },
        ) {
            Text("Play Music")
        }
        Spacer(Modifier.size(8.dp))
        Button(
            onClick = {
                Log.d(MainActivity.TAG, "Pause Music")
                musicPlayer.pause()
            },
        ) {
            Text("Pause Music")
        }
        Spacer(Modifier.size(8.dp))
        Button(
            onClick = {
                Log.d(MainActivity.TAG, "Stop Music")
                musicPlayer.stopAndRelease()
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
                        val contentResolver = context.contentResolver
                        val takeFlags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                        contentResolver.takePersistableUriPermission(fileUri, takeFlags)

                        // TODO: validate uri is music
                        val meta = MusicMetadataUtility.loadMusicMetaDataFromUri(context, fileUri)
                        val lastModified = UriUtility.queryLastModified(context, fileUri)

                        val artwork = (context.applicationContext as KamishibaiApp).artworkCacheManager.loadOrCreate(fileUri)
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
                            Log.d(MainActivity.TAG, "Success: $success")
                        }
                    }
                },
            )
        }
        Column(modifier = Modifier.height(140.dp)) {
            MusicList(
                musics = musics.value ?: emptyList(),
                onClick = { music ->
                    Log.d(MainActivity.TAG, "Clicked: ${music.title}")

                    musicPlayer.stopAndRelease()
                    musicPlayer.setListenerAsWeakRef(musicPlayerListener)
                    musicPlayer.setMusic(music)
                },
            )
        }
    }
}