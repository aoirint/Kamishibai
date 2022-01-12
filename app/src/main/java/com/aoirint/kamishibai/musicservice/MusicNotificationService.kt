package com.aoirint.kamishibai.musicservice

import android.app.*
import android.content.Context
import android.content.Intent
import android.media.MediaMetadata
import android.media.session.MediaSession
import android.os.*
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.core.app.NotificationCompat
import com.aoirint.kamishibai.KamishibaiApp
import com.aoirint.kamishibai.R
import com.aoirint.kamishibai.musicplayer.MusicPlayer
import com.aoirint.kamishibai.musicservice.MusicNotificationService.MainActivity.Companion.TAG

class MusicNotificationService: Service() {
    class MainActivity : ComponentActivity() {
        companion object {
            const val TAG = "MusicNotificationService"
        }
    }

    var messenger: Messenger? = null
    var mediaSession: MediaSessionCompat? = null
    var mediaSessionToken: MediaSessionCompat.Token? = null

    class MediaSessionCallback: MediaSessionCompat.Callback() {
    }

    override fun onCreate() {
        super.onCreate()

        messenger = Messenger(IncomingHandler(this))
        mediaSession = MediaSessionCompat(this, TAG)

        mediaSession?.let { mediaSession ->
            mediaSessionToken = mediaSession.sessionToken

            mediaSession.setCallback(MediaSessionCallback())
        }
    }

    override fun onBind(p0: Intent?): IBinder? {
        return messenger?.binder
    }

    override fun onDestroy() {
        super.onDestroy()

        val musicPlayer = MusicPlayer.getInstance(this)
        musicPlayer.stopAndRelease()
    }

    fun createBuilder(): NotificationCompat.Builder {
        fun configureActions(builder: NotificationCompat.Builder): NotificationCompat.Builder {
            fun <T> createActivityPendingIntent(activityClass: Class<T>): PendingIntent {
                return PendingIntent.getActivity(
                    applicationContext,
                    0,
                    Intent(applicationContext, activityClass)
                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK),
                    0
                )
            }

            fun createActionPendingIntent(action: String): PendingIntent {
                return PendingIntent.getService(
                    this,
                    0,
                    Intent(this, MusicNotificationService::class.java)
                        .setAction(action),
                    0
                )
            }

            val musicPlayer = (application as KamishibaiApp).musicPlayer

            var playAction: NotificationCompat.Action
            if (! musicPlayer.isPlaying) {
                val playPendingIntent = createActionPendingIntent("play")
                playAction = NotificationCompat.Action.Builder(R.drawable.ic_play_arrow_7f7f7f_32dp, "Play", playPendingIntent).build()
            }
            else {
                val pausePendingIntent = createActionPendingIntent("pause")
                playAction = NotificationCompat.Action.Builder(R.drawable.ic_pause_7f7f7f_32dp, "Pause", pausePendingIntent).build()
            }

            return builder
                .setContentIntent(
                    createActivityPendingIntent(MainActivity::class.java)
                )
                .addAction(
                    NotificationCompat.Action.Builder(
                        R.drawable.ic_skip_previous_7f7f7f_32dp,
                        "Previous",
                        createActionPendingIntent("previous")
                    ).build()
                )
                .addAction(playAction)
                .addAction(
                    NotificationCompat.Action.Builder(
                        R.drawable.ic_skip_next_7f7f7f_32dp,
                        "Next",
                        createActionPendingIntent("next")
                    ).build()
                )
                .addAction(
                    NotificationCompat.Action.Builder(
                        R.drawable.ic_close_7f7f7f_32dp,
                        "Close",
                        createActionPendingIntent("close")
                    ).build()
                )
        }

        fun configurePreferences(builder: NotificationCompat.Builder): NotificationCompat.Builder {
            val musicPlayer = (application as KamishibaiApp).musicPlayer
            val music = musicPlayer.currentMusic

            val title = music?.title ?: ""
            val album = music?.album ?: ""
            val artist = music?.artist ?: ""

            var description = ""
            if (album != "") description += album
            if (album != "" && artist != "") description += " - "
            if (artist != "") description += artist

            val style = androidx.media.app.NotificationCompat.MediaStyle()

            builder
                .setContentTitle(title)
                .setContentText(description)
                .setSmallIcon(R.drawable.ic_music_note_7f7f7f_32dp)
                .setStyle(
                    music?.let {
                        style.setShowActionsInCompactView(0,1,2,3)
                    } ?: style.setShowActionsInCompactView(3)
                )
                .setTicker(null)
                .setOnlyAlertOnce(true)

            music?.color?.let {
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    builder.setColorized(true)
                }
                builder.setColor(it)
            }

            musicPlayer.currentArtwork?.let {
                builder.setLargeIcon(it)
            }

            return builder
        }

        val context = applicationContext

        val channelId = "default"
        val channelTitle = context.getString(R.string.app_name)

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val builder: NotificationCompat.Builder

        val channel: NotificationChannel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channel = NotificationChannel(channelId, channelTitle, NotificationManager.IMPORTANCE_LOW)
            channel.setSound(null, null)
            channel.enableVibration(false)

            notificationManager.createNotificationChannel(channel)

            builder = NotificationCompat.Builder(context, channelId)
        }
        else {
            builder = NotificationCompat.Builder(context)
            builder.priority = Notification.PRIORITY_MAX
        }

        configureActions(builder)
        configurePreferences(builder)

        return builder
    }

    fun updateNotification() {
        val notification = createBuilder().build()
        startForeground(1, notification)

        val musicPlayer = (application as KamishibaiApp).musicPlayer
        val music = musicPlayer.currentMusic
        val artworkCacheManager = (application as KamishibaiApp).artworkCacheManager
        val artwork = music?.uri?.let { artworkCacheManager.loadOrCreate(it) }

        val builder = MediaMetadataCompat.Builder()
        builder.putString(MediaMetadata.METADATA_KEY_TITLE, music?.title)

        artwork?.let { builder.putBitmap(MediaMetadata.METADATA_KEY_ALBUM_ART, it) }

        mediaSession?.setMetadata(builder.build())


        val stateBuilder = PlaybackStateCompat.Builder()
        stateBuilder.setActions(
            PlaybackStateCompat.ACTION_PLAY
            or PlaybackStateCompat.ACTION_PLAY_PAUSE
            or PlaybackStateCompat.ACTION_PAUSE
            or PlaybackStateCompat.ACTION_REWIND
            or PlaybackStateCompat.ACTION_FAST_FORWARD
        )

        mediaSession?.let { mediaSession ->
            mediaSession.setPlaybackState(stateBuilder.build())
            mediaSession.isActive = true
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val musicPlayer = (application as KamishibaiApp).musicPlayer

        when (intent?.action) {
            "start" -> {
                updateNotification()
            }
            "play" -> {
                musicPlayer.start()
                updateNotification()
            }
            "pause" -> {
                musicPlayer.pause()
                updateNotification()
            }
            "previous" -> {
                Log.d(TAG, "Not implemented: previous")
                updateNotification()
            }
            "next" -> {
                Log.d(TAG, "Not implemented: next")
                updateNotification()
            }
            "close" -> {
                musicPlayer.stopAndRelease()
                stopForeground(true)
                stopSelf()
            }
        }

        return START_STICKY
    }

    internal class IncomingHandler(
        val service: MusicNotificationService
    ): Handler(Looper.getMainLooper()) {
        override fun handleMessage(message: Message) {
            when (message.what) {
                0 -> {
                    service.updateNotification()
                }
            }
        }
    }

}