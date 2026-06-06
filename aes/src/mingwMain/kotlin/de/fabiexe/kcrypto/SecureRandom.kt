package de.fabiexe.kcrypto

import kotlinx.cinterop.*
import platform.windows.BCRYPT_USE_SYSTEM_PREFERRED_RNG
import platform.windows.BCryptGenRandom

object SecureRandom {
    @OptIn(ExperimentalForeignApi::class)
    fun nextBytes(bytes: ByteArray) {
        bytes.usePinned { pinnedBytes ->
            BCryptGenRandom(
                null,
                pinnedBytes.addressOf(0).reinterpret(),
                bytes.size.convert(),
                BCRYPT_USE_SYSTEM_PREFERRED_RNG.convert()
            )
        }
    }
}