package de.fabiexe.kcrypto

/**
 * SHA2 (Secure Hashing Algorithm 2) is a secure and fast hashing algorithm.
 *
 * @see SHA2.hash
 */
interface SHA2 : HashingAlgorithm {
    companion object {
        val SHA256 = sha2(256)
        val SHA384 = sha2(384)
        val SHA512 = sha2(512)
    }
}

internal expect fun sha2(length: Int): SHA2