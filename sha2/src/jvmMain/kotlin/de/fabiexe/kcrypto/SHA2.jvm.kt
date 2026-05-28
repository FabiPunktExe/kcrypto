package de.fabiexe.kcrypto

import java.security.MessageDigest

internal actual fun sha2(length: Int): SHA2 = object : SHA2 {
    override suspend fun hash(data: ByteArray): ByteArray {
        return MessageDigest.getInstance("SHA-$length").digest(data)
    }
}