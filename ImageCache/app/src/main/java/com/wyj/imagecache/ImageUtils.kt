package com.wyj.imagecache

import java.security.MessageDigest

object ImageUtils {
    public fun hashKeyForCache(key: String): String {
        val messageDigest = MessageDigest.getInstance("MD5")
        messageDigest.update(key.toByteArray())
        return bytesToHexString(messageDigest.digest())
    }

    private fun bytesToHexString(bytes: ByteArray): String {
        val sb = StringBuffer()
        for (byte in bytes) {
            sb.append(String.format("%02X", byte))
        }
        return sb.toString()
    }
}