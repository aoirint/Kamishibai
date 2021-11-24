package com.aoirint.kamishibai.musicregistry

import androidx.annotation.WorkerThread
import kotlinx.coroutines.flow.Flow

class MusicRepository(
    private val musicDao: MusicDao
) {
    val allMusics: Flow<List<Music>> = musicDao.getMusicsOrderByCreatedAt()

    @WorkerThread
    suspend fun insert(music: Music) {
        musicDao.insert(music)
    }
}
