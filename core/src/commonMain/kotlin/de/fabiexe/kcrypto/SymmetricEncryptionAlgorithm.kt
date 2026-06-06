package de.fabiexe.kcrypto

interface SymmetricEncryptionAlgorithm {
    /**
     * Encrypt the given data using the provided key and initialization vector
     *
     * @param data The data to be encrypted
     * @param key The key to be used for encryption
     * @return The encrypted data
     */
    suspend fun encrypt(data: ByteArray, key: ByteArray): ByteArray

    /**
     * Encrypt the given data using the provided password
     *
     * @param data The data to be encrypted
     * @param password The password to be used for encryption
     * @return The encrypted data
     */
    suspend fun encryptWithPassword(data: ByteArray, password: ByteArray): ByteArray

    /**
     * Encrypt the given data using the provided password
     *
     * @param data The data to be encrypted
     * @param password The password to be used for encryption
     * @return The encrypted data
     */
    suspend fun encryptWithPassword(data: ByteArray, password: String): ByteArray {
        return encryptWithPassword(data, password.encodeToByteArray())
    }

    /**
     * Decrypt the given data using the provided key and initialization vector
     *
     * @param data The data to be decrypted
     * @param key The key to be used for decryption
     * @return The decrypted data
     */
    suspend fun decrypt(data: ByteArray, key: ByteArray): ByteArray

    /**
     * Decrypt the given data using the provided password
     *
     * @param data The data to be decrypted
     * @param password The password to be used for decryption
     * @return The decrypted data
     */
    suspend fun decryptWithPassword(data: ByteArray, password: ByteArray): ByteArray

    /**
     * Decrypt the given data using the provided password
     *
     * @param data The data to be decrypted
     * @param password The key to be used for decryption
     * @return The decrypted data
     */
    suspend fun decryptWithPassword(data: ByteArray, password: String): ByteArray {
        return decryptWithPassword(data, password.encodeToByteArray())
    }

    /**
     * Generate a random key for encryption
     *
     * @return The generated key
     */
    suspend fun generateKey(): ByteArray
}