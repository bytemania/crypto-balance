package com.github.bytemania.cryptobalance.domain.dto;

import lombok.EqualsAndHashCode;
import lombok.Value;

import java.math.BigDecimal;

@Value(staticConstructor = "of")
public class CryptoState {
    String symbol;
    @EqualsAndHashCode.Exclude
    BigDecimal invested;
}
