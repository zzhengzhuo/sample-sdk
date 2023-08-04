package com.unipass.smartAccount

import com.unipass.smartAccount.ChainID
import uniffi.shared.Key

class SmartAccountInitOptions(
    val chainId: ChainID
)

class SmartAccountInitByKeysOptions(
    val chainId: ChainID,
    val keys: Array<Key>
)

class SmartAccountInitByKeysetJsonOptions(
    val chainId: ChainID,
    val keysetJson: String
)