package de.fabiexe.kcrypto

import js.buffer.toByteArray
import web.crypto.crypto
import web.crypto.digest
import web.encoding.TextEncoder

internal actual fun sha2(length: Int): SHA2 = object : SHA2 {
    override suspend fun hash(data: ByteArray): ByteArray {
        val buffer = TextEncoder().encode(data.decodeToString())
        val hash = crypto.subtle.digest("SHA-$length", buffer)
        return hash.toByteArray()
    }
}