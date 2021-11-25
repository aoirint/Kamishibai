package com.aoirint.kamishibai

import android.app.Application
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.os.Message
import android.os.Messenger
import android.os.RemoteException
import com.aoirint.kamishibai.utility.ArtworkCacheManager
import com.aoirint.kamishibai.musicplayer.MusicPlayer
import com.aoirint.kamishibai.musicregistry.MusicRoomDatabase
import com.aoirint.kamishibai.musicregistry.MusicRepository
import com.aoirint.kamishibai.musicservice.MusicNotificationService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class KamishibaiApp : Application(), ServiceConnection {
    val applicationScope = CoroutineScope(SupervisorJob())

    val database by lazy { MusicRoomDatabase.getDatabase(this, applicationScope) }
    val repository by lazy { MusicRepository(database.musicDao()) }

    val musicPlayer: MusicPlayer
        get() = MusicPlayer.getInstance(this)

    val artworkCacheManager: ArtworkCacheManager
        get() = ArtworkCacheManager.getInstance(this)

    var messenger: Messenger? = null

    override fun onCreate() {
        super.onCreate()

        startNotificationService()
    }

    fun startNotificationService() {
        val intent = Intent(this, MusicNotificationService::class.java).setAction("start")
        startService(intent)
        bindService(intent, this, BIND_AUTO_CREATE)
    }

    fun sendUpdateNotification() {
        if (messenger == null) startNotificationService()

        try {
            messenger?.send(Message.obtain(null, 0))
        } catch (error: RemoteException) {
            error.printStackTrace()
        }
    }

    override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
        messenger = Messenger(binder)
    }

    override fun onServiceDisconnected(name: ComponentName?) {
        messenger = null
    }
}
