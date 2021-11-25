package com.aoirint.kamishibai.utility

import android.graphics.Bitmap

class BitmapUtility {
    companion object {
        fun extractCommonColor(bitmap: Bitmap): Int {
            val scaledBitmap = Bitmap.createScaledBitmap(bitmap, 1, 1, false)
            return scaledBitmap.getPixel(0, 0)
        }
    }
}