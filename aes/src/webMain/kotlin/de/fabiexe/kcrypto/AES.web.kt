package de.fabiexe.kcrypto

import js.buffer.ArrayBuffer
import js.buffer.BufferSource
import js.buffer.toByteArray
import js.typedarrays.Uint8Array
import js.typedarrays.toByteArray
import js.typedarrays.toUint8Array
import web.crypto.*
import web.encoding.TextEncoder

@Suppress("unused")
@OptIn(ExperimentalWasmJsInterop::class)
private fun newPbkdf2Params(name: String, hash: HashAlgorithmIdentifier, iterations: Int, salt: BufferSource): Pbkdf2Params =
    js("({ name: name, hash: hash, iterations: iterations, salt: salt })")

@Suppress("unused")
@OptIn(ExperimentalWasmJsInterop::class)
private fun newAesDerivedKeyParams(name: String, length: Int): AesDerivedKeyParams =
    js("({ name: name, length: length })")

@Suppress("unused")
@OptIn(ExperimentalWasmJsInterop::class)
fun newAesGcmParams(name: String, iv: BufferSource, tagLength: Int): AesGcmParams =
    js("({ name: name, iv: iv, tagLength: tagLength })")

@Suppress("unused")
@OptIn(ExperimentalWasmJsInterop::class)
private fun newAesKeyGenParams(name: String, length: Int): AesKeyGenParams =
    js("({ name: name, length: length })")

actual open class AES_GCM actual constructor(
    val keyLength: Int,
    val saltLength: Int,
    val ivLength: Int,
    val iterationCount: Int,
    val tagLength: Int
) : AES {
    @OptIn(ExperimentalWasmJsInterop::class)
    actual override suspend fun encrypt(data: ByteArray, key: ByteArray): ByteArray {
        val salt = crypto.getRandomValues(Uint8Array(saltLength))
        val iv = crypto.getRandomValues(Uint8Array(ivLength))
        val cryptoKey = crypto.subtle.importKey(
            KeyFormat.raw,
            key.toUint8Array(),
            "AES-GCM",
            false,
            listOf(KeyUsage.encrypt).toJsArray()
        )
        return encrypt(data, cryptoKey, salt, iv.toUint8Array())
    }

    actual override suspend fun encryptWithPassword(data: ByteArray, password: ByteArray): ByteArray {
        val salt = crypto.getRandomValues(Uint8Array(saltLength))
        val iv = crypto.getRandomValues(Uint8Array(ivLength))
        val cryptoKey = deriveKey(password, salt)
        return encrypt(data, cryptoKey, salt, iv)
    }

    private suspend fun encrypt(data: ByteArray, key: CryptoKey, salt: Uint8Array<ArrayBuffer>, iv: Uint8Array<ArrayBuffer>): ByteArray {
        val algorithm = newAesGcmParams("AES-GCM", iv, tagLength)
        val encodedData = TextEncoder().encode(data.decodeToString())
        val encryptedData = crypto.subtle.encrypt(algorithm, key, encodedData)
        return salt.toByteArray() + iv.toByteArray() + encryptedData.toByteArray()
    }

    @OptIn(ExperimentalWasmJsInterop::class)
    actual override suspend fun decrypt(data: ByteArray, key: ByteArray): ByteArray {
        val salt = data.copyOfRange(0, saltLength).toUint8Array()
        val iv = data.copyOfRange(saltLength, saltLength + ivLength).toUint8Array()
        val encryptedData = data.copyOfRange(saltLength + ivLength, data.size)
        val cryptoKey = crypto.subtle.importKey(
            KeyFormat.raw,
            key.toUint8Array(),
            "AES-GCM",
            false,
            listOf(KeyUsage.decrypt).toJsArray()
        )
        return decrypt(encryptedData, cryptoKey, iv, salt)
    }

    actual override suspend fun decryptWithPassword(data: ByteArray, password: ByteArray): ByteArray {
        val salt = data.copyOfRange(0, saltLength).toUint8Array()
        val iv = data.copyOfRange(saltLength, saltLength + ivLength).toUint8Array()
        val encryptedData = data.copyOfRange(saltLength + ivLength, data.size)
        val cryptoKey = deriveKey(password, salt)
        return decrypt(encryptedData, cryptoKey, iv, salt)
    }

    private suspend fun decrypt(data: ByteArray, key: CryptoKey, iv: Uint8Array<ArrayBuffer>, salt: Uint8Array<ArrayBuffer>): ByteArray {
        val algorithm = newAesGcmParams("AES-GCM", iv, tagLength)
        return crypto.subtle.decrypt(algorithm, key, data.toUint8Array()).toByteArray()
    }

    @OptIn(ExperimentalWasmJsInterop::class)
    suspend fun deriveKey(password: ByteArray, salt: BufferSource): CryptoKey {
        val baseKey = crypto.subtle.importKey(
            KeyFormat.raw,
            TextEncoder().encode(password.decodeToString()),
            "PBKDF2",
            false,
            listOf(KeyUsage.deriveKey).toJsArray()
        )
        return crypto.subtle.deriveKey(
            newPbkdf2Params("PBKDF2", AlgorithmIdentifier("SHA-256"), iterationCount, salt),
            baseKey,
            newAesDerivedKeyParams("AES-GCM", keyLength),
            true,
            listOf(KeyUsage.encrypt, KeyUsage.decrypt).toJsArray()
        )
    }

    @OptIn(ExperimentalWasmJsInterop::class)
    actual override suspend fun generateKey(): ByteArray {
        val algorithm = newAesKeyGenParams("AES-GCM", keyLength)
        val keyUsages = listOf(KeyUsage.encrypt, KeyUsage.decrypt).toJsArray()
        val key = crypto.subtle.generateKey(algorithm, true, keyUsages)
        return crypto.subtle.exportKey(KeyFormat.raw, key).toByteArray()
    }
}