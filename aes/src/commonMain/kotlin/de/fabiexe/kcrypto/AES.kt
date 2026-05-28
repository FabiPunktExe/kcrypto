package de.fabiexe.kcrypto

/**
 * AES (Advanced Encryption Standard) is a widely used symmetric encryption algorithm.
 *
 * @see AES.GCM256
 * @see AES.encrypt
 * @see AES.decrypt
 */
interface AES : SymmetricEncryptionAlgorithm {
    companion object {
        val GCM256 = AES_GCM(256)
    }
}

/**
 * AES encryption algorithm in Galois/Counter Mode (GCM).
 *
 * @param keyLength The length of the key in bits (e.g., 128, 192, 256).
 * @param saltLength The length of the salt in bytes (default is 16).
 * @param ivLength The length of the initialization vector (IV) in bytes (default is 12).
 * @param iterationCount The number of iterations for key derivation (default is 65536).
 * @param tagLength The length of the authentication tag in bits (default is 128).
 */
expect open class AES_GCM(
    keyLength: Int,
    saltLength: Int = 16,
    ivLength: Int = 12,
    iterationCount: Int = 65536,
    tagLength: Int = 128
) : AES {
    override suspend fun encrypt(data: ByteArray, key: ByteArray): ByteArray
    override suspend fun decrypt(data: ByteArray, key: ByteArray): ByteArray
}