package de.fabiexe.kcrypto

internal expect fun hashSync(password: String, rounds: Int): String
internal expect fun compareSync(password: String, hash: String): Boolean