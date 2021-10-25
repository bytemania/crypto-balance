package com.github.bytemania.cryptobalance.port.in;

import com.github.bytemania.cryptobalance.domain.dto.AllocationResult;
import com.github.bytemania.cryptobalance.domain.dto.Crypto;

import java.math.BigDecimal;

public interface CreateAllocation {

    AllocationResult allocate(Crypto stableCoin, BigDecimal amountToInvest, BigDecimal minAmountToAllocate);

}
