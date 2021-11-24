package com.aoirint.kamishibai.musicplayer

import android.content.Context
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.net.Uri
import java.lang.ref.WeakReference

import android.media.MediaMetadataRetriever.*;

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
    private var musicMetadata: MusicMetadata? = null
    private var listener: WeakReference<MusicPlayerListener> = WeakReference(null)

    fun setMusicUri(uri: Uri) {
        val oldContext = OnMusicChangedContext(musicUri, musicMetadata)
        musicUri = uri
        mediaPlayer = MediaPlayer.create(context, uri)

        musicMetadata = MusicMetadataUtility.loadMusicMetaDataFromUri(context, uri)

        val newContext = OnMusicChangedContext(musicUri, musicMetadata)
        listener.get()?.onMusicChanged(oldContext, newContext)
    }

    fun releaseMusic() {
        val oldContext = OnMusicChangedContext(musicUri, musicMetadata)
        musicUri = null
        mediaPlayer = null

        listener.get()?.onMusicChanged(oldContext, null)
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
