package com.github.bytemania.cryptobalance.domain.dto;

import lombok.EqualsAndHashCode;
import lombok.Value;

import java.math.BigDecimal;

@Value
public class CryptoState {
    String symbol;

    @EqualsAndHashCode.Exclude
    BigDecimal holding;

    @EqualsAndHashCode.Exclude
    BigDecimal invested;
}
