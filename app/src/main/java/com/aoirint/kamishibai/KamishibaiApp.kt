package com.aoirint.kamishibai

import android.app.Application
import com.aoirint.kamishibai.musicregistry.MusicRoomDatabase
import com.aoirint.kamishibai.musicregistry.MusicRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class KamishibaiApp : Application() {
    val applicationScope = CoroutineScope(SupervisorJob())

    val database by lazy { MusicRoomDatabase.getDatabase(this, applicationScope) }
    val repository by lazy { MusicRepository(database.musicDao()) }
}
