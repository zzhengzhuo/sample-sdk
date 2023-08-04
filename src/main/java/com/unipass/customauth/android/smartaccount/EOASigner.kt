package com.unipass.smartAccount

import org.web3j.crypto.Credentials
import org.web3j.crypto.Sign
import org.web3j.utils.Numeric
import java.nio.charset.StandardCharsets

class EOASigner(privateKey: String?) : Signer {
    private var credentials: Credentials? = null

    init {
        credentials = Credentials.create(privateKey)
    }

    override fun address(): String {
        return credentials!!.address
    }

    override fun signMessage(message: String): String {
        val messageBytes = message!!.toByteArray(StandardCharsets.UTF_8)
        return this.signMessage(messageBytes)
    }

    override fun signMessage(message: ByteArray): String {
        val signature = Sign.signPrefixedMessage(message, credentials!!.ecKeyPair)
        val value = ByteArray(65)
        System.arraycopy(signature.r, 0, value, 0, 32)
        System.arraycopy(signature.s, 0, value, 32, 32)
        System.arraycopy(signature.v, 0, value, 64, 1)
        return Numeric.toHexString(value)
    }
}