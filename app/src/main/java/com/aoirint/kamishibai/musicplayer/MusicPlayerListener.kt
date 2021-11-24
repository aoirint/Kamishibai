package com.aoirint.kamishibai.musicplayer

import android.net.Uri

data class OnMusicChangedContext(
    val uri: Uri?,
    val metadata: MusicMetadata?,
)

interface MusicPlayerListener {
    fun onMusicChanged(oldContext: OnMusicChangedContext?, newContext: OnMusicChangedContext?)
}
