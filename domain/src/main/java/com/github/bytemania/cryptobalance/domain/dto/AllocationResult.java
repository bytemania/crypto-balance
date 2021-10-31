package com.github.bytemania.cryptobalance.domain.dto;

import lombok.Value;

import java.math.BigDecimal;
import java.util.Set;

@Value
public class AllocationResult {
    BigDecimal amountToInvest;
    BigDecimal holdings;
    BigDecimal totalInvested;
    BigDecimal rest;
    BigDecimal minValueToAllocate;
    CryptoAllocation stableCrypto;
    Set<CryptoAllocation> cryptos;
}
