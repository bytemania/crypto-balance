package com.github.bytemania.cryptobalance.domain;

import java.util.List;

public interface BalanceStrategy {
    List<CryptoAllocation> allocate();
}
