package de.fabiexe.kcrypto

import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertTrue

class BcryptTest {
    @Test
    fun test() = runTest {
        val password = "password"
        val hash = Bcrypt.hash(password)
        assertTrue(Bcrypt.verify(password, hash))
    }
}