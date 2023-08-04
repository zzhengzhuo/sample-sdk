package com.unipass.smartAccount

import kotlin.jvm.JvmOverloads
import kotlinx.coroutines.coroutineScope
import org.web3j.utils.Numeric
import uniffi.shared.RoleWeight
import uniffi.shared.SendingTransactionOptions
import uniffi.shared.SimulateTransactionOptions
import uniffi.shared.SmartAccount
import uniffi.shared.SmartAccountBuilder
import uniffi.shared.Transaction
import uniffi.shared.SimulateResult
import uniffi.shared.TypedData

class SmartAccount(options: SmartAccountOptions) {
    var builder: SmartAccountBuilder?;
    var inner: SmartAccount? = null;
    var masterKeySigner: WrapSigner? = null;
    var masterKeyRoleWeight: RoleWeight? = null;

    init {
        builder = SmartAccountBuilder();
        if (options.masterKeySigner != null) {
            masterKeySigner = WrapSigner(options.masterKeySigner);
            masterKeyRoleWeight = options.masterKeyRoleWeight;
            builder!!.withMasterKeySigner(masterKeySigner!!, masterKeyRoleWeight);
        }
        if (options.unipassServerUrl != null) {
            builder!!.withUnipassServerUrl(options.unipassServerUrl)
        }
        builder!!.withAppId(options.appId);
        options.chainOptions.iterator().forEach { chainOptions ->
            builder!!.addChainOption(
                chainOptions.chainId.iD.toULong(),
                chainOptions.rpcUrl,
                chainOptions.relayerUrl
            )
        };
    }

    suspend fun init(options: SmartAccountInitOptions) {
        builder!!.withActiveChain(options.chainId.iD.toULong());
        return coroutineScope {
            inner = builder!!.build();
            builder!!.destroy();
            builder = null;
        }
    }

    /**
     * Init initialize by keys
     *
     * @param options
     */
    suspend fun init(options: SmartAccountInitByKeysOptions) {
        var keys = options.keys.toMutableList()
        if (masterKeySigner == null) {
            val masterKey = keys.removeFirst()
            builder = builder!!.withMasterKey(masterKey)
        }
        builder!!.addGuardianKeys(keys.toList()).withActiveChain(options.chainId.iD.toULong())
        return coroutineScope {
            inner = builder!!.build();
            builder!!.destroy();
            builder = null;
        }
    }

    /**
     * Init initialize by keyset json string.
     * Notice that the first key is the master key. If you pass master key in the
     * constructor function, the master key will replace the master key in the
     * keyset json in options.
     *
     * @param options
     */
    suspend fun init(options: SmartAccountInitByKeysetJsonOptions) {
        builder =
            builder!!.withActiveChain(options.chainId.iD.toULong())
                .withKeysetJson(options.keysetJson);
        if (masterKeySigner != null) {
            builder = builder!!.withMasterKeySigner(masterKeySigner!!, masterKeyRoleWeight);
        }
        return coroutineScope {
            inner = builder!!.build();
            builder!!.destroy();
            builder = null;
        }
    }

    /*********************** Account Status Functions  */
    /**
     * Address
     *
     * @return the contract address of the smart account.
     *          return `0x` prefixed hex string.
     */
    fun address(): String {
        this.requireInit()
        return Numeric.toHexString(inner!!.address().toUByteArray().toByteArray())
    }

    //        throw new Exception("not implemented");
    /**
     * Is deployed
     *
     * @return whether the smart account contract is deployed
     */
    suspend fun isDeployed(): Boolean {
        this.requireInit()
        return this.inner!!.isDeployed();
    }

    private fun requireInit() {
        if (inner == null) {
            throw SmartAccountNotInitException();
        }
    }

    /**
     * Chain id of the current active chain.
     *
     * @return
     */
    fun chainId(): ChainID {
        requireInit();
        return ChainID.from(this.inner!!.chain().toInt());
    }

    suspend fun nonce(): ULong {
        requireInit()
        return this.inner!!.nonce()
    }

    /**
     * Switch chain
     * Notice that the chain has to be included in the chain options
     * from the constructor.
     *
     * @param chainID
     */
    fun switchChain(chainID: ChainID) {
        requireInit()
        this.inner!!.switchChain(chainID.iD.toULong())
    }

    /*********************** Message Sign Functions  */
    suspend fun signMessage(message: String): String? {
        return Numeric.toHexString(
            inner?.signMessage(message.toByteArray().toUByteArray().toList())?.toUByteArray()
                ?.toByteArray()
        );
    }

    suspend fun signMessage(messageBytes: ByteArray?): String? {
        return if (messageBytes == null) {
            null
        } else {
            Numeric.toHexString(
                inner?.signMessage(messageBytes.toUByteArray().toList())?.toUByteArray()
                    ?.toByteArray()
            )
        }
    }

    suspend fun signTypedData(typedData: TypedData): String {
        this.requireInit()
        return Numeric.toHexString(
            this.inner!!.signTypedData(typedData).toUByteArray().toByteArray()
        )
    }

    /*********************** Transaction Functions  */
    @JvmOverloads
    suspend fun simulateTransaction(
        tx: Transaction,
        options: SimulateTransactionOptions? = null
    ): SimulateResult? {
        return this.simulateTransactionBatch(arrayOf(tx), options)
    }

    @JvmOverloads
    suspend fun simulateTransactionBatch(
        txs: Array<Transaction>,
        options: SimulateTransactionOptions? = null
    ): SimulateResult? {
        return inner?.simulateTransactions(txs.asList(), options)
    }

    @JvmOverloads
    suspend fun sendTransaction(
        tx: Transaction,
        options: SendingTransactionOptions? = null
    ): String? {
        return this.sendTransactionBatch(arrayOf(tx), options)
    }

    @JvmOverloads
    suspend fun sendTransactionBatch(
        txs: Array<Transaction>,
        options: SendingTransactionOptions? = null
    ): String? {
        return inner?.sendTransactions(txs.asList(), options)
    }

    @JvmOverloads
    fun signTransaction(
        txs: Array<Transaction?>?,
        options: SendingTransactionOptions? = null
    ): Transaction? {
        return null
    }

    // TODO:
    fun sendSignedTransaction(): String? {
        return null
    }

    suspend fun waitTransactionReceiptByHash(
        transactionHash: String,
        confirmations: Int,
        chainId: ChainID?,
        timeOut: Int
    ): uniffi.shared.TransactionReceipt? {
        return inner?.waitForTransaction(transactionHash);
    }

    fun getKeysetJson(): String {
        requireInit()
        return inner!!.keysetJson();
    }
}