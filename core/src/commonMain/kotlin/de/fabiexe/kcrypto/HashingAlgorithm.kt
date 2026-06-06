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
    fun hashBlocking(data: ByteArray): ByteArray = runBlocking { hash(data) }
}