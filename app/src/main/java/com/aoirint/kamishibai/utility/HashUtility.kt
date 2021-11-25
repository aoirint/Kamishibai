package com.aoirint.kamishibai.utility

import android.content.Context
import android.net.Uri
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.security.DigestInputStream
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

class HashUtility {
    companion object {
        fun calcUriStringHash(uri: Uri): String {
            val messageDigest: MessageDigest
            try {
                messageDigest = MessageDigest.getInstance("md5") // fast cals
            } catch (error: NoSuchAlgorithmException) {
                throw RuntimeException(error)
            }

            messageDigest.update(uri.toString().toByteArray(StandardCharsets.UTF_16))

            return String(messageDigest.digest(), StandardCharsets.US_ASCII)
        }

    }
}