package com.unipass.smartAccount

enum class ChainID(val iD: Int) {
    ETHEREUM_MAINNET(1), ETHEREUM_GOERLI(5), BNBCHAIN_MAINNET(56), BNBCHAIN_TESTNET(97), POLYGON_MAINNET(
        137
    ),
    POLYGON_MUMBAI(80001), ARBITRIUM_ONE(42161), ARBITRUM_GOERLI(421613);

    companion object {
        fun from(findID: Int): ChainID = ChainID.values().first { it.iD == findID }
    }
}