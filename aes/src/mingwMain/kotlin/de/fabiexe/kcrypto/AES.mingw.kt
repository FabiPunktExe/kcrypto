package de.fabiexe.kcrypto

import kotlinx.cinterop.*
import platform.posix.memset
import platform.windows.*

private const val BCRYPT_SHA256_ALGORITHM = "SHA256"
private const val BCRYPT_ALG_HMAC_FLAG = 0x00000008u
private const val BCRYPT_AES_ALGORITHM = "AES"
private const val BCRYPT_CHAINING_MODE = "ChainingMode"
private const val BCRYPT_CHAINING_MODE_GCM = "ChainingModeGCM"
private const val BCRYPT_AUTHENTICATED_CIPHER_MODE_INFO_VERSION = 1u

actual open class AES_GCM actual constructor(
    val keyLength: Int,
    val saltLength: Int,
    val ivLength: Int,
    val iterationCount: Int,
    val tagLength: Int
) : AES {
    actual override suspend fun encrypt(data: ByteArray, key: ByteArray): ByteArray {
        val salt = ByteArray(saltLength).apply(SecureRandom::nextBytes)
        val iv = ByteArray(ivLength).apply(SecureRandom::nextBytes)
        return encrypt(data, key, salt, iv)
    }

    actual override suspend fun encryptWithPassword(data: ByteArray, password: ByteArray): ByteArray {
        val salt = ByteArray(saltLength).apply(SecureRandom::nextBytes)
        val iv = ByteArray(ivLength).apply(SecureRandom::nextBytes)
        val key = deriveKey(password, salt)
        return encrypt(data, key, salt, iv)
    }

    @OptIn(ExperimentalForeignApi::class)
    private fun encrypt(data: ByteArray, key: ByteArray, salt: ByteArray, iv: ByteArray): ByteArray = memScoped {
        useAlgorithm(BCRYPT_AES_ALGORITHM, 0u) { aesAlgHandle ->
            var status = BCryptSetProperty(
                aesAlgHandle.value,
                BCRYPT_CHAINING_MODE,
                BCRYPT_CHAINING_MODE_GCM.wcstr.ptr.reinterpret(),
                ((BCRYPT_CHAINING_MODE_GCM.wcstr.size) * sizeOf<WCHARVar>()).toUInt(),
                0u
            )
            check(status == 0) { "BCryptSetProperty failed: 0x${status.toHexString()}" }

            val keyHandle = alloc<BCRYPT_KEY_HANDLEVar>()
            key.usePinned { pinnedKey ->
                status = BCryptGenerateSymmetricKey(
                    aesAlgHandle.value,
                    keyHandle.ptr,
                    null,
                    0u,
                    pinnedKey.addressOf(0).reinterpret(),
                    key.size.toUInt(),
                    0u
                )
            }
            check(status == 0) { "BCryptGenerateSymmetricKey failed: 0x${status.toHexString()}" }

            try {
                iv.usePinned { pinnedIv ->
                    val tag = ByteArray(tagLength / 8)
                    tag.usePinned { pinnedTag ->
                        val authInfo = alloc<BCRYPT_AUTHENTICATED_CIPHER_MODE_INFO>()
                        BCRYPT_INIT_AUTH_MODE_INFO(authInfo)
                        authInfo.pbNonce = pinnedIv.addressOf(0).reinterpret()
                        authInfo.cbNonce = iv.size.toUInt()
                        authInfo.pbTag = pinnedTag.addressOf(0).reinterpret()
                        authInfo.cbTag = tag.size.toUInt()
                        authInfo.pbAuthData = null
                        authInfo.cbAuthData = 0u

                        val result = ByteArray(data.size)
                        val resultLength = alloc<DWORDVar>()
                        data.usePinned { pinnedData ->
                            result.usePinned { pinnedResult ->
                                status = BCryptEncrypt(
                                    keyHandle.value,
                                    pinnedData.addressOf(0).reinterpret(),
                                    data.size.toUInt(),
                                    authInfo.ptr,
                                    null,
                                    0u,
                                    pinnedResult.addressOf(0).reinterpret(),
                                    result.size.toUInt(),
                                    resultLength.ptr,
                                    0u
                                )
                            }
                        }
                        check(status == 0) { "BCryptEncrypt failed: 0x${status.toHexString()}" }

                        return salt + iv + result + tag
                    }
                }
            } finally {
                BCryptDestroyKey(keyHandle.value)
            }
        }
    }

    actual override suspend fun decrypt(data: ByteArray, key: ByteArray): ByteArray {
        val salt = data.copyOfRange(0, saltLength)
        val iv = data.copyOfRange(saltLength, saltLength + ivLength)
        val encryptedData = data.copyOfRange(saltLength + ivLength, data.size - (tagLength / 8))
        val tag = data.copyOfRange(data.size - (tagLength / 8), data.size)
        return decrypt(encryptedData, key, salt, iv, tag)
    }

    actual override suspend fun decryptWithPassword(data: ByteArray, password: ByteArray): ByteArray {
        val salt = data.copyOfRange(0, saltLength)
        val iv = data.copyOfRange(saltLength, saltLength + ivLength)
        val encryptedData = data.copyOfRange(saltLength + ivLength, data.size - (tagLength / 8))
        val tag = data.copyOfRange(data.size - (tagLength / 8), data.size)
        val key = deriveKey(password, salt)
        return decrypt(encryptedData, key, salt, iv, tag)
    }

    @OptIn(ExperimentalForeignApi::class)
    private fun decrypt(
        encryptedData: ByteArray,
        derivedKey: ByteArray,
        salt: ByteArray,
        iv: ByteArray,
        tag: ByteArray
    ): ByteArray = memScoped {
        useAlgorithm(BCRYPT_AES_ALGORITHM, 0u) { aesAlgHandle ->
            var status = BCryptSetProperty(
                aesAlgHandle.value,
                BCRYPT_CHAINING_MODE,
                BCRYPT_CHAINING_MODE_GCM.wcstr.ptr.reinterpret(),
                ((BCRYPT_CHAINING_MODE_GCM.wcstr.size) * sizeOf<WCHARVar>()).toUInt(),
                0u
            )
            check(status == 0) { "BCryptSetProperty failed: 0x${status.toHexString()}" }

            val keyHandle = alloc<BCRYPT_KEY_HANDLEVar>()
            status = derivedKey.usePinned { pinnedDerivedKey ->
                BCryptGenerateSymmetricKey(
                    aesAlgHandle.value,
                    keyHandle.ptr,
                    null,
                    0u,
                    pinnedDerivedKey.addressOf(0).reinterpret(),
                    derivedKey.size.toUInt(),
                    0u
                )
            }
            check(status == 0) { "BCryptGenerateSymmetricKey failed: 0x${status.toHexString()}" }

            try {
                iv.usePinned { pinnedIv ->
                    tag.usePinned { pinnedTag ->
                        val authInfo = alloc<BCRYPT_AUTHENTICATED_CIPHER_MODE_INFO>()
                        BCRYPT_INIT_AUTH_MODE_INFO(authInfo)
                        authInfo.pbNonce = pinnedIv.addressOf(0).reinterpret()
                        authInfo.cbNonce = iv.size.toUInt()
                        authInfo.pbTag = pinnedTag.addressOf(0).reinterpret()
                        authInfo.cbTag = tag.size.toUInt()
                        authInfo.pbAuthData = null
                        authInfo.cbAuthData = 0u

                        val result = ByteArray(encryptedData.size)
                        val resultLength = alloc<DWORDVar>()
                        status = encryptedData.usePinned { pinnedEncryptedData ->
                            result.usePinned { pinnedResult ->
                                BCryptDecrypt(
                                    keyHandle.value,
                                    pinnedEncryptedData.addressOf(0).reinterpret(),
                                    encryptedData.size.toUInt(),
                                    authInfo.ptr,
                                    null,
                                    0u,
                                    pinnedResult.addressOf(0).reinterpret(),
                                    result.size.toUInt(),
                                    resultLength.ptr,
                                    0u
                                )
                            }
                        }
                        check(status == 0) { "BCryptDecrypt failed: 0x${status.toHexString()}" }

                        return result.copyOfRange(0, resultLength.value.toInt())
                    }
                }
            } finally {
                BCryptDestroyKey(keyHandle.value)
            }
        }
    }

    @OptIn(ExperimentalForeignApi::class)
    fun deriveKey(password: ByteArray, salt: ByteArray): ByteArray = memScoped {
        useAlgorithm(BCRYPT_SHA256_ALGORITHM, BCRYPT_ALG_HMAC_FLAG) { sha256AlgHandle ->
            val output = ByteArray(keyLength / 8)
            val status = password.usePinned { pinnedPassword ->
                salt.usePinned { pinnedSalt ->
                    output.usePinned { pinnedOutput ->
                        BCryptDeriveKeyPBKDF2(
                            sha256AlgHandle.value,
                            pinnedPassword.addressOf(0).reinterpret(),
                            password.size.toUInt(),
                            pinnedSalt.addressOf(0).reinterpret(),
                            salt.size.toUInt(),
                            iterationCount.toULong(),
                            pinnedOutput.addressOf(0).reinterpret(),
                            output.size.toUInt(),
                            0u
                        )
                    }
                }
            }
            check(status == 0) { "BCryptDeriveKeyPBKDF2 failed: 0x${status.toHexString()}" }
            return output
        }
    }

    @OptIn(ExperimentalForeignApi::class)
    private fun BCRYPT_INIT_AUTH_MODE_INFO(_AUTH_INFO_STRUCT_: BCRYPT_AUTHENTICATED_CIPHER_MODE_INFO) {
        memset(_AUTH_INFO_STRUCT_.ptr, 0, sizeOf<BCRYPT_AUTHENTICATED_CIPHER_MODE_INFO>().convert())
        _AUTH_INFO_STRUCT_.cbSize = sizeOf<BCRYPT_AUTHENTICATED_CIPHER_MODE_INFO>().convert()
        _AUTH_INFO_STRUCT_.dwInfoVersion = BCRYPT_AUTHENTICATED_CIPHER_MODE_INFO_VERSION
    }

    actual override suspend fun generateKey(): ByteArray {
        return ByteArray(keyLength / 8).apply(SecureRandom::nextBytes)
    }

    @OptIn(ExperimentalForeignApi::class)
    private inline fun <T> useAlgorithm(
        algorithm: String,
        flags: UInt,
        block: (BCRYPT_ALG_HANDLEVar) -> T
    ): T = memScoped {
        val algHandle = alloc<BCRYPT_ALG_HANDLEVar>()
        val status = BCryptOpenAlgorithmProvider(
            algHandle.ptr,
            algorithm,
            null,
            flags
        )
        check(status == 0) { "BCryptOpenAlgorithmProvider failed: 0x${status.toHexString()}" }

        try {
            return block(algHandle)
        } finally {
            BCryptCloseAlgorithmProvider(algHandle.value, 0u)
        }
    }
}