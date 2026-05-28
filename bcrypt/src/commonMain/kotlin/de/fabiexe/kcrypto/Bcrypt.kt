package de.fabiexe.kcrypto

/**
 * Bcrypt is a hashing algorithm designed for password hashing.
 * Bcrypt is intentionally slow to prevent brute-force attacks.
 * Bcrypt includes a random salt in the hash, so the same input will produce different hashes each time.
 *
 * @see [Bcrypt.hash] to hash data.
 * @see [Bcrypt.verify] to verify data against a hash.
 */
object Bcrypt : HashingAlgorithm {
    override suspend fun hash(data: ByteArray): ByteArray {
        return hash(data, 12)
    }

    /**
     * Verify the given data against the provided hash
     *
     * @param data The input data (password) to be verified
     * @param hash The hash to compare against
     * @return `true` if the data matches the hash, `false` otherwise
     */
    fun verify(data: String, hash: ByteArray): Boolean = verify(data.encodeToByteArray(), hash)

    /**
     * Verify the given data against the provided hash
     *
     * @param data The input data (password) to be verified
     * @param hash The hash to compare against
     * @return `true` if the data matches the hash, `false` otherwise
     */
    fun verify(data: ByteArray, hash: String): Boolean = verify(data, hash.encodeToByteArray())

    /**
     * Verify the given data against the provided hash
     *
     * @param data The input data (password) to be verified
     * @param hash The hash to compare against
     * @return `true` if the data matches the hash, `false` otherwise
     */
    fun verify(data: String, hash: String): Boolean = verify(data.encodeToByteArray(), hash.encodeToByteArray())
}

/**
 * Hash the given data
 *
 * @param data The input data (password) to be hashed
 * @param rounds The number of rounds to use for hashing; higher rounds increase security but also increase hashing time
 * @return The hash of the input
 */
expect fun Bcrypt.hash(data: ByteArray, rounds: Int): ByteArray

/**
 * Verify the given data against the provided hash
 *
 * @param data The input data (password) to be verified
 * @param hash The hash to compare against
 * @return `true` if the data matches the hash, `false` otherwise
 */
expect fun Bcrypt.verify(data: ByteArray, hash: ByteArray): Boolean