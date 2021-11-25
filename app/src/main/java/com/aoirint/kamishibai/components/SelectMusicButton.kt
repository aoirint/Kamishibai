package com.aoirint.kamishibai.components

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import com.aoirint.kamishibai.MainActivity

@Composable
fun SelectMusicButton(
    title: String,
    multiple: Boolean = false,
    onSelected: (fileUris: List<Uri>) -> Unit
) {
    val selectMusic = if (multiple) {
        rememberLauncherForActivityResult(
            ActivityResultContracts.OpenMultipleDocuments()
        ) { fileUris: List<Uri> ->
            onSelected(fileUris)
        }
    } else {
        rememberLauncherForActivityResult(
            ActivityResultContracts.OpenDocument()
        ) { fileUri: Uri? ->
            if (fileUri != null) {
                onSelected(listOf(fileUri))
            }
        }
    }

    Button(
        onClick = {
            Log.d(MainActivity.TAG, "Select Music")
            selectMusic.launch(arrayOf("audio/*"))
        },
    ) {
        Text(title)
    }
}
