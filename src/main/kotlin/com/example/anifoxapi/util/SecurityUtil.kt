package com.example.anifoxapi.util

import com.google.common.hash.Hashing

class SecurityUtil {
    companion object {
        fun encryptSHA256(plainText: String): String {
            val hashFunction = Hashing.sha256()
            val hc = hashFunction
                .newHasher()
                .putString(plainText, Charsets.UTF_8)
                .hash()
            return hc.toString()
        }
    }
}