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
import com.aoirint.kamishibai.views.DebugMusicPlayerView
import java.time.ZonedDateTime

class MainActivity : ComponentActivity() {
    companion object {
        const val TAG = "MainActivity"
    }

    private val musicViewModel: MusicViewModel by viewModels {
        MusicViewModelFactory((application as KamishibaiApp).repository)
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
                        DebugMusicPlayerView()
                    }
                }
            }
        }
    }
}
