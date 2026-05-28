package de.fabiexe.kcrypto

interface HashingAlgorithm {
    /**
     * Hash the given data
     *
     * @param data The input data to be hashed
     * @return The hash of the input
     */
    suspend fun hash(data: ByteArray): ByteArray

    /**
     * Hash the given data
     *
     * @param data The input data to be hashed
     * @return The hash of the input
     */
    suspend fun hash(data: String): ByteArray = hash(data.encodeToByteArray())

    /**
     * Hash the given data
     *
     * @param data The input data to be hashed
     * @return The hash of the input as string
     */
    suspend fun hashToString(data: ByteArray): String = hash(data).decodeToString()

    /**
     * Hash the given data
     *
     * @param data The input data to be hashed
     * @return The hash of the input as string
     */
    suspend fun hashToString(data: String): String = hash(data).decodeToString()
}