package com.aoirint.kamishibai.musicplayer

import com.aoirint.kamishibai.musicregistry.Music

interface MusicPlayerListener {
    fun onMusicChanged(oldContext: Music?, newContext: Music?)
}
