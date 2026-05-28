package de.fabiexe.kcrypto

import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.SecretKey
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
        val salt = ByteArray(saltLength)
        random.nextBytes(salt)

        val iv = ByteArray(ivLength)
        random.nextBytes(iv)

        val secretKey = deriveKey(key, salt)

        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, GCMParameterSpec(tagLength, iv))

        val encryptedData = cipher.doFinal(data)

        return salt + iv + encryptedData
    }

    actual override suspend fun decrypt(data: ByteArray, key: ByteArray): ByteArray {
        val salt = data.copyOfRange(0, saltLength)
        val iv = data.copyOfRange(saltLength, saltLength + ivLength)
        val encryptedData = data.copyOfRange(saltLength + ivLength, data.size)

        val secretKey = deriveKey(key, salt)

        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.DECRYPT_MODE, secretKey, GCMParameterSpec(tagLength, iv))

        return cipher.doFinal(encryptedData)
    }

    fun deriveKey(password: ByteArray, salt: ByteArray): SecretKey {
        val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
        val spec = PBEKeySpec(password.decodeToString().toCharArray(), salt, iterationCount, keyLength)
        return SecretKeySpec(factory.generateSecret(spec).encoded, "AES")
    }
}