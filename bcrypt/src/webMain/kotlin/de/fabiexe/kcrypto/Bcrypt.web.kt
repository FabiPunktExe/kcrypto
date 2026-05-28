package de.fabiexe.kcrypto

actual fun Bcrypt.hash(data: ByteArray, rounds: Int): ByteArray {
    return hashSync(data.decodeToString(), rounds).encodeToByteArray()
}

actual fun Bcrypt.verify(data: ByteArray, hash: ByteArray): Boolean {
    return compareSync(data.decodeToString(), hash.decodeToString())
}