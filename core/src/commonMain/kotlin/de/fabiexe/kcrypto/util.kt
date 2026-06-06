package de.fabiexe.kcrypto

internal expect inline fun runBlocking(crossinline block: suspend () -> ByteArray): ByteArray