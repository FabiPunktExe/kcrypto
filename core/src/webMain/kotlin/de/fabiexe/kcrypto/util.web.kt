package de.fabiexe.kcrypto

internal actual inline fun runBlocking(crossinline block: suspend () -> ByteArray): ByteArray {
    throw UnsupportedOperationException("runBlocking is not supported in JavaScript and WebAssembly. Please use suspend functions.")
}