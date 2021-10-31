package com.github.bytemania.cryptobalance.domain.dto;

import lombok.EqualsAndHashCode;
import lombok.Value;

import java.math.BigDecimal;

@Value
public class CryptoAllocation {
    String symbol;

    @EqualsAndHashCode.Exclude
    BigDecimal price;

    @EqualsAndHashCode.Exclude
    BigDecimal holding;

    @EqualsAndHashCode.Exclude
    BigDecimal invested;

    @EqualsAndHashCode.Exclude
    double marketCapPercentage;

    @EqualsAndHashCode.Exclude
    BigDecimal rebalance;
}
