package de.fabiexe.kcrypto

import at.favre.lib.crypto.bcrypt.BCrypt

actual fun Bcrypt.hash(data: ByteArray, rounds: Int): ByteArray {
    return BCrypt.withDefaults().hash(12, data)
}

actual fun Bcrypt.verify(data: ByteArray, hash: ByteArray): Boolean {
    return BCrypt.verifyer().verify(data, hash).verified
}