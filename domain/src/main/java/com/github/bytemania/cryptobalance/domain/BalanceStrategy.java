package com.github.bytemania.cryptobalance.domain;

import com.github.bytemania.cryptobalance.domain.dto.AllocationResult;

public interface BalanceStrategy {
    AllocationResult allocate();
}
