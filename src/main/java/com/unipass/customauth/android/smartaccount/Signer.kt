package com.unipass.smartAccount

/**
 * Partial Signer from ethers.js https://github.com/ethers-io/ethers.js/blob/main/src.ts/providers/signer.ts
 */
interface Signer {
    /**
     * get signer ethereum address
     * @return ethereum address
     */
    fun address(): String

    /**
     * sign string message using personal sign
     * @param message String message to be signed, it will be converted to UTF-8 bytes before signing
     * @return signature with hex format prefixed with '0x'
     */
    fun signMessage(message: String): String

    /**
     * sign bytes message using personal sign
     * @param message Bytes message to be signed
     * @return signature with hex format prefixed with '0x'
     */
    fun signMessage(message: ByteArray): String
}

class WrapSigner(private val signer: Signer) : uniffi.shared.Signer {
    override fun address(): String {
        return signer.address()
    }

    override fun signMessage(message: List<UByte>): String {
        return signer.signMessage(message.toUByteArray().toByteArray())
    }
}