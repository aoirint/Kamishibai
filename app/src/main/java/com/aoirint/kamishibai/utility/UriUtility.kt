package com.aoirint.kamishibai.utility

import android.content.Context
import android.net.Uri
import android.provider.DocumentsContract
import java.lang.IllegalArgumentException
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime

class UriUtility {
    companion object {
        fun queryLastModified(context: Context, uri: Uri): ZonedDateTime {
            val unixEpochMillis = queryLastModifiedUnixEpochMillis(context, uri)
            val instant = Instant.ofEpochMilli(unixEpochMillis)
            return ZonedDateTime.ofInstant(instant, ZoneId.systemDefault())
        }

        fun queryLastModifiedUnixEpochMillis(context: Context, uri: Uri): Long {
            return context.contentResolver.query(uri, null, null, null, null)?.use { cur ->
                try {
                    val columnLastModified = cur.getColumnIndexOrThrow(DocumentsContract.Document.COLUMN_LAST_MODIFIED)
                    cur.moveToFirst()
                    cur.getLong(columnLastModified)
                } catch (error: IllegalArgumentException) {
                    0
                }
            } ?: 0
        }
    }
}