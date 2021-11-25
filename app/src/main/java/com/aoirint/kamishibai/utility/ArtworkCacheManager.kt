package com.aoirint.kamishibai.utility

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.net.Uri
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException

class ArtworkCacheManager private constructor(
    private val context: Context
) {
    companion object {
        const val ARTWORK_DIR = "artworks"

        private var INSTANCE: ArtworkCacheManager? = null

        fun getInstance(context: Context) = INSTANCE ?: synchronized(this) {
            INSTANCE ?: ArtworkCacheManager(context).also { INSTANCE = it }
        }
    }

    fun getArtworkCachePath(musicUri: Uri): File {
        val artworkCacheDir = File(context.cacheDir, ARTWORK_DIR)
        val uriHash = HashUtility.calcUriStringHash(musicUri)

        return File(artworkCacheDir, File("$uriHash.jpg").name)
    }

    @Throws(IOException::class)
    fun loadOrCreate(musicUri: Uri): Bitmap? {
        val file = getArtworkCachePath(musicUri)
        if (file.exists()) return loadArtworkCache(file)

        return loadAndCacheArtwork(musicUri)
    }

    @Throws(IOException::class)
    fun loadArtworkCache(file: File): Bitmap? {
        FileInputStream(file).use {
            return BitmapFactory.decodeStream(it)
        }
    }

    fun deleteArtworkCachePath(musicUri: Uri) {
        val file = getArtworkCachePath(musicUri)
        file.delete()
    }

    @Throws(IOException::class)
    private fun loadAndCacheArtwork(musicUri: Uri): Bitmap? {
        val mmr = MediaMetadataRetriever()
        mmr.setDataSource(context, musicUri)

        mmr.embeddedPicture?.let { artworkBytes ->
            val artwork = BitmapFactory.decodeByteArray(artworkBytes, 0, artworkBytes.size ?: 0)

            val file = getArtworkCachePath(musicUri)
            file.parentFile.mkdirs()

            FileOutputStream(file).use {
                artwork.compress(Bitmap.CompressFormat.JPEG, 100, it)
            }

            return artwork
        }

        return null
    }


}