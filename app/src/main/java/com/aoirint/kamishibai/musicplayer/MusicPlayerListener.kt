package com.aoirint.kamishibai.musicplayer

import android.net.Uri

interface MusicPlayerListener {
    fun onMusicUriChanged(oldUri: Uri?, newUri: Uri?)
}
