package de.fabiexe.kcrypto

import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class AESTest {
    @Test
    fun testGCM256WithPassword() = runTest {
        val data = "foo bar"
        val password = "password"

        var encrypted = AES.GCM256.encryptWithPassword(data.encodeToByteArray(), password)
        var decrypted = AES.GCM256.decryptWithPassword(encrypted, password).decodeToString()
        assertEquals(data, decrypted)

        // Test with fixed values ("foo bar" encrypted with "password")
        encrypted = "f34a54b1cb3ad1c109970a8b29160545405e4612eb307f70818afc55a64589d0b9e3cf0bc31bc97c8325d109694a2243a86480".hexToByteArray()
        decrypted = AES.GCM256.decryptWithPassword(encrypted, password).decodeToString()
        assertEquals(data, decrypted)
    }
}