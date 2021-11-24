package com.aoirint.kamishibai.musicplayer

import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri

class MusicMetadataUtility {
    companion object {
        fun loadMusicMetaDataFromUri(context: Context, uri: Uri): MusicMetadata {
            val mmr = MediaMetadataRetriever()
            mmr.setDataSource(context, uri)

            val title = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE)
            val album = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM)
            val artist = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST)

            return MusicMetadata(title, album, artist)
        }
    }
}