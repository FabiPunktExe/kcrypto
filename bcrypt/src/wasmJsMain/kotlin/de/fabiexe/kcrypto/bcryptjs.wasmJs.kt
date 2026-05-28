@file:OptIn(ExperimentalWasmJsInterop::class)
@file:JsModule("bcryptjs")

package de.fabiexe.kcrypto

internal actual external fun hashSync(password: String, rounds: Int): String
internal actual external fun compareSync(password: String, hash: String): Boolean