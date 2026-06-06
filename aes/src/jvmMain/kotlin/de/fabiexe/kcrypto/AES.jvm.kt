package de.fabiexe.kcrypto

import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

actual open class AES_GCM actual constructor(
    val keyLength: Int,
    val saltLength: Int,
    val ivLength: Int,
    val iterationCount: Int,
    val tagLength: Int
) : AES {
    actual override suspend fun encrypt(data: ByteArray, key: ByteArray): ByteArray {
        val random = SecureRandom()
        val salt = ByteArray(saltLength).also(SecureRandom()::nextBytes)
        val iv = ByteArray(ivLength).also(random::nextBytes)
        return encrypt(data, key, iv, salt)
    }

    actual override suspend fun encryptWithPassword(data: ByteArray, password: ByteArray): ByteArray {
        val random = SecureRandom()
        val salt = ByteArray(saltLength).also(random::nextBytes)
        val iv = ByteArray(ivLength).also(random::nextBytes)
        val key = deriveKey(password, salt)
        return encrypt(data, key, iv, salt)
    }

    private fun encrypt(data: ByteArray, key: ByteArray, iv: ByteArray, salt: ByteArray): ByteArray {
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        val keySpec = SecretKeySpec(key, "AES")
        val parameterSpec = GCMParameterSpec(tagLength, iv)
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, parameterSpec)
        return salt + iv + cipher.doFinal(data)
    }

    actual override suspend fun decrypt(data: ByteArray, key: ByteArray): ByteArray {
        val salt = data.copyOfRange(0, saltLength)
        val iv = data.copyOfRange(saltLength, saltLength + ivLength)
        val encryptedData = data.copyOfRange(saltLength + ivLength, data.size)
        return decrypt(encryptedData, key, iv, salt)
    }

    actual override suspend fun decryptWithPassword(data: ByteArray, password: ByteArray): ByteArray {
        val salt = data.copyOfRange(0, saltLength)
        val iv = data.copyOfRange(saltLength, saltLength + ivLength)
        val encryptedData = data.copyOfRange(saltLength + ivLength, data.size)
        val key = deriveKey(password, salt)
        return decrypt(encryptedData, key, iv, salt)
    }

    private fun decrypt(data: ByteArray, key: ByteArray, iv: ByteArray, salt: ByteArray): ByteArray {
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        val keySpec = SecretKeySpec(key, "AES")
        val parameterSpec = GCMParameterSpec(tagLength, iv)
        cipher.init(Cipher.DECRYPT_MODE, keySpec, parameterSpec)
        return cipher.doFinal(data)
    }

    fun deriveKey(password: ByteArray, salt: ByteArray): ByteArray {
        val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
        val spec = PBEKeySpec(password.decodeToString().toCharArray(), salt, iterationCount, keyLength)
        return factory.generateSecret(spec).encoded
    }

    actual override suspend fun generateKey(): ByteArray {
        val keyGenerator = KeyGenerator.getInstance("AES")
        keyGenerator.init(keyLength)
        return keyGenerator.generateKey().encoded
    }
}