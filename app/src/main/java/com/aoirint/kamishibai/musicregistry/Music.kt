package com.aoirint.kamishibai.musicregistry

import android.net.Uri
import android.os.Parcelable
import androidx.room.*
import kotlinx.parcelize.Parcelize
import java.time.ZonedDateTime

@Entity(
    tableName = "music_table",
    indices = [
        Index(value = ["uri"], unique = true)
    ],
)
@Parcelize
data class Music(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val uri: Uri,
    val title: String?,
    val album: String?,
    val artist: String?,
    val color: Int?,
    val fileLastModified: ZonedDateTime,
    val createdAt: ZonedDateTime,
    val updatedAt: ZonedDateTime,
) : Parcelable
