package com.aoirint.kamishibai.musicregistry

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MusicDao {
    @Query("SELECT * FROM music_table ORDER BY createdAt DESC")
    fun getMusicsOrderByCreatedAt(): Flow<List<Music>>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(music: Music)

    @Query("DELETE FROM music_table")
    suspend fun deleteAll()
}
