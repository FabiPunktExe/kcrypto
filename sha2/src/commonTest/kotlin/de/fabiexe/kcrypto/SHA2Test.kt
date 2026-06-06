package de.fabiexe.kcrypto

import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertContentEquals

class SHA2Test {
    val data = "foo bar"
    val sha256 = "fbc1a9f858ea9e177916964bd88c3d37b91a1e84412765e29950777f265c4b75"
    val sha384 = "6839312f3db343477070d3c0b2becd417b357154d48794d01d78cfb4617ed5ab819a77b6832f6542dd18bb738131ef7e"
    val sha512 = "65019286222ace418f742556366f9b9da5aaf6797527d2f0cba5bfe6b2f8ed24746542a0f2be1da8d63c2477f688b608eb53628993afa624f378b03f10090ce7"

    @Test
    fun testSHA256() = runTest {
        assertContentEquals(sha256.hexToByteArray(), SHA2.SHA256.hash(data.encodeToByteArray()))
    }

    @Test
    fun testSHA384() = runTest {
        assertContentEquals(sha384.hexToByteArray(), SHA2.SHA384.hash(data.encodeToByteArray()))
    }

    @Test
    fun testSHA512() = runTest {
        assertContentEquals(sha512.hexToByteArray(), SHA2.SHA512.hash(data.encodeToByteArray()))
    }
}