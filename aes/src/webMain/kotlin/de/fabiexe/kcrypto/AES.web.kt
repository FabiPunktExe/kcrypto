package de.fabiexe.kcrypto

import js.buffer.BufferSource
import js.buffer.toByteArray
import js.typedarrays.Uint8Array
import js.typedarrays.toByteArray
import js.typedarrays.toUint8Array
import web.crypto.*
import web.encoding.TextEncoder

@Suppress("unused")
@OptIn(ExperimentalWasmJsInterop::class)
fun newPbkdf2Params(name: String, hash: HashAlgorithmIdentifier, iterations: Int, salt: BufferSource): Pbkdf2Params =
    js("({ name: name, hash: hash, iterations: iterations, salt: salt })")

@Suppress("unused")
@OptIn(ExperimentalWasmJsInterop::class)
fun newAesDerivedKeyParams(name: String, length: Int): AesDerivedKeyParams =
    js("({ name: name, length: length })")

@Suppress("unused")
@OptIn(ExperimentalWasmJsInterop::class)
fun newAesGcmParams(name: String, iv: BufferSource, tagLength: Int): AesGcmParams =
    js("({ name: name, iv: iv, tagLength: tagLength })")

actual open class AES_GCM actual constructor(
    val keyLength: Int,
    val saltLength: Int,
    val ivLength: Int,
    val iterationCount: Int,
    val tagLength: Int
) : AES {
    actual override suspend fun encrypt(data: ByteArray, key: ByteArray): ByteArray {
        val salt = crypto.getRandomValues(Uint8Array(saltLength))
        val iv = crypto.getRandomValues(Uint8Array(ivLength))
        val secretKey = deriveKey(key, salt)
        val encryptedData = crypto.subtle.encrypt(
            newAesGcmParams("AES-GCM", iv, tagLength),
            secretKey,
            TextEncoder().encode(data.decodeToString())
        )
        return salt.toByteArray() + iv.toByteArray() + encryptedData.toByteArray()
    }

    actual override suspend fun decrypt(data: ByteArray, key: ByteArray): ByteArray {
        val salt = data.copyOfRange(0, saltLength).toUint8Array()
        val iv = data.copyOfRange(saltLength, saltLength + ivLength).toUint8Array()
        val encryptedData = data.copyOfRange(saltLength + ivLength, data.size).toUint8Array()
        val secretKey = deriveKey(key, salt)
        val decryptedData = crypto.subtle.decrypt(
            newAesGcmParams("AES-GCM", iv, tagLength),
            secretKey,
            encryptedData
        )
        return decryptedData.toByteArray()
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
}