package com.aoirint.kamishibai.musicplayer

import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.net.Uri
import java.lang.ref.WeakReference

import android.media.MediaMetadataRetriever.*;
import com.aoirint.kamishibai.KamishibaiApp
import com.aoirint.kamishibai.musicregistry.Music
import com.aoirint.kamishibai.utility.ArtworkCacheManager

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
    private var music: Music? = null
    private var artwork: Bitmap? = null
    private var listener: WeakReference<MusicPlayerListener> = WeakReference(null)

    val artworkCacheManager: ArtworkCacheManager
        get() = (context.applicationContext as KamishibaiApp).artworkCacheManager

    val isPlaying: Boolean
        get() = mediaPlayer?.isPlaying ?: false

    val currentMusic: Music?
        get() = music

    val currentArtwork: Bitmap?
        get() = artwork

    fun setMusic(music: Music) {
        val oldContext = currentMusic

        this.music = music
        this.artwork = artworkCacheManager.loadOrCreate(music.uri)

        mediaPlayer = MediaPlayer.create(context, music.uri)

        listener.get()?.onMusicChanged(oldContext, music)
    }

    fun releaseMusic() {
        val oldContext = currentMusic
        music = null
        mediaPlayer = null

        listener.get()?.onMusicChanged(oldContext, null)
    }

    fun start() {
        mediaPlayer?.start()

        (context.applicationContext as KamishibaiApp).sendUpdateNotification()
    }

    fun stopAndRelease() {
        mediaPlayer?.stop()
        releaseMusic()
        (context.applicationContext as KamishibaiApp).sendUpdateNotification()
    }

    fun pause() {
        mediaPlayer?.pause()
        (context.applicationContext as KamishibaiApp).sendUpdateNotification()
    }

    fun setListenerAsWeakRef(listener: MusicPlayerListener?) {
        this.listener = WeakReference(listener)
    }

    fun removeListener() {
        this.listener = WeakReference(null)
    }
}
