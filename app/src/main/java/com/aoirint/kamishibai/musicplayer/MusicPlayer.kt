package com.aoirint.kamishibai.musicplayer

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import java.lang.ref.WeakReference

class MusicPlayer private constructor(
    private val context: Context
) {
    companion object {
        private var INSTANCE: MusicPlayer? = null

        fun getInstance(context: Context) = INSTANCE ?: synchronized(this) {
            INSTANCE ?: MusicPlayer(context).also { INSTANCE = it }
        }
    }

    private var mediaPlayer: MediaPlayer? = null
    private var musicUri: Uri? = null
    private var listener: WeakReference<MusicPlayerListener> = WeakReference(null)

    fun setMusicUri(uri: Uri) {
        musicUri = uri
        mediaPlayer = MediaPlayer.create(this.context, uri)
    }

    fun releaseMusic() {
        listener.get()?.onMusicUriChanged(musicUri, null)
        musicUri = null
        mediaPlayer = null
    }

    fun start() {
        mediaPlayer?.start()
    }

    fun stopAndRelease() {
        mediaPlayer?.stop()
        releaseMusic()
    }

    fun pause() {
        mediaPlayer?.pause()
    }

    fun setListenerAsWeakRef(listener: MusicPlayerListener?) {
        this.listener = WeakReference(listener)
    }

    fun removeListener() {
        this.listener = WeakReference(null)
    }
}