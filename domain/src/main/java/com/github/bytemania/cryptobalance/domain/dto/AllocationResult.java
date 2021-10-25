package com.github.bytemania.cryptobalance.domain.dto;

import lombok.Value;

import java.math.BigDecimal;
import java.util.List;

@Value(staticConstructor = "of")
public class AllocationResult {
    BigDecimal amountToInvest;
    BigDecimal rest;
    List<CryptoAllocation> cryptos;
}
