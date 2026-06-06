package de.fabiexe.kcrypto

internal actual inline fun runBlocking(crossinline block: suspend () -> ByteArray): ByteArray {
    return kotlinx.coroutines.runBlocking { block() }
}