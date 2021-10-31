package com.github.bytemania.cryptobalance.domain.dto;

import lombok.EqualsAndHashCode;
import lombok.Value;

import java.math.BigDecimal;

@Value
public class Crypto {
    String symbol;
    @EqualsAndHashCode.Exclude
    double marketCapPercentage;
    @EqualsAndHashCode.Exclude
    BigDecimal price;
    @EqualsAndHashCode.Exclude
    boolean stableCoin;
}
