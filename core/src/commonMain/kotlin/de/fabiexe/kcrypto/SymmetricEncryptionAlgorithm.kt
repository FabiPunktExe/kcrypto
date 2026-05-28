package de.fabiexe.kcrypto

interface SymmetricEncryptionAlgorithm {
    /**
     * Encrypt the given data using the provided key
     *
     * @param data The data to be encrypted
     * @param key The key to be used for encryption
     * @return The encrypted data
     */
    suspend fun encrypt(data: ByteArray, key: ByteArray): ByteArray

    /**
     * Encrypt the given data using the provided key
     *
     * @param data The data to be encrypted
     * @param key The key to be used for encryption
     * @return The encrypted data
     */
    suspend fun encrypt(data: String, key: ByteArray): ByteArray = encrypt(data.encodeToByteArray(), key)

    /**
     * Encrypt the given data using the provided key
     *
     * @param data The data to be encrypted
     * @param key The key to be used for encryption
     * @return The encrypted data
     */
    suspend fun encrypt(data: ByteArray, key: String): ByteArray = encrypt(data, key.encodeToByteArray())

    /**
     * Encrypt the given data using the provided key
     *
     * @param data The data to be encrypted
     * @param key The key to be used for encryption
     * @return The encrypted data
     */
    suspend fun encrypt(data: String, key: String): ByteArray = encrypt(data.encodeToByteArray(), key.encodeToByteArray())

    /**
     * Decrypt the given data using the provided key
     *
     * @param data The data to be decrypted
     * @param key The key to be used for decryption
     * @return The decrypted data
     */
    suspend fun decrypt(data: ByteArray, key: ByteArray): ByteArray

    /**
     * Decrypt the given data using the provided key
     *
     * @param data The data to be decrypted
     * @param key The key to be used for decryption
     * @return The decrypted data
     */
    suspend fun decrypt(data: ByteArray, key: String): ByteArray = decrypt(data, key.encodeToByteArray())

    /**
     * Decrypt the given data using the provided key
     *
     * @param data The data to be decrypted
     * @param key The key to be used for decryption
     * @return The decrypted data as a string
     */
    suspend fun decryptToString(data: ByteArray, key: ByteArray): String = decrypt(data, key).decodeToString()

    /**
     * Decrypt the given data using the provided key
     *
     * @param data The data to be decrypted
     * @param key The key to be used for decryption
     * @return The decrypted data as a string
     */
    suspend fun decryptToString(data: ByteArray, key: String): String = decrypt(data, key).decodeToString()
}