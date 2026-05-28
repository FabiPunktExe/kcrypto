package de.fabiexe.kcrypto

import org.kotlincrypto.hash.sha2.SHA256
import org.kotlincrypto.hash.sha2.SHA384
import org.kotlincrypto.hash.sha2.SHA512

internal actual fun sha2(length: Int): SHA2 = object : SHA2 {
    override suspend fun hash(data: ByteArray): ByteArray {
        val digest = when (length) {
            256 -> SHA256()
            384 -> SHA384()
            512 -> SHA512()
            else -> throw IllegalArgumentException("Unsupported SHA2 length: $length")
        }
        return digest.digest(data)
    }
}