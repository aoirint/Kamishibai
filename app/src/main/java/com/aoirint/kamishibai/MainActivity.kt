package com.aoirint.kamishibai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.aoirint.kamishibai.ui.theme.KamishibaiTheme
import com.aoirint.kamishibai.views.DebugMusicPlayerView

class MainActivity : ComponentActivity() {
    companion object {
        const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val navController = rememberNavController()
            KamishibaiTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    NavHost(
                        navController = navController,
                        startDestination = "debugMusicPlayer"
                    ) {
                        composable("debugMusicPlayer") {
                            DebugMusicPlayerView()
                        }
                    }
                }
            }
        }
    }
}
