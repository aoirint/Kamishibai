package com.aoirint.kamishibai.musicregistry

import android.net.Uri
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(
    tableName = "music_table",
    indices = [
        Index(value = ["uri"], unique = true)
    ],
)
data class Music(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val uri: Uri,
    val title: String?,
    val album: String?,
    val artist: String?,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
)