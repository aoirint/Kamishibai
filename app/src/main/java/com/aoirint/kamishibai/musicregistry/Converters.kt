package com.aoirint.kamishibai.musicregistry

import android.net.Uri
import androidx.room.TypeConverter
import java.time.LocalDateTime
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

class Converters {
    @TypeConverter
    fun uriFromString(value: String?): Uri? {
        return value?.let { Uri.parse(it) }
    }

    @TypeConverter
    fun stringFromUri(uri: Uri?): String? {
        return uri?.toString()
    }

    @TypeConverter
    fun dateTimeFromString(value: String?): ZonedDateTime? {
        return value?.let {
            try {
                ZonedDateTime.parse(it, DateTimeFormatter.ISO_OFFSET_DATE_TIME)
            } catch (error: DateTimeParseException) {
                error.printStackTrace()
                return ZonedDateTime.now()
            }
        }
    }

    @TypeConverter
    fun stringFromDateTime(date: ZonedDateTime?): String? {
        return date?.let { DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(it) }
    }
}