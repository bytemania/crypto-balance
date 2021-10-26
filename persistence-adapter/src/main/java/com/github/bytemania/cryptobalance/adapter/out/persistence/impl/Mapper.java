package com.github.bytemania.cryptobalance.adapter.out.persistence.impl;

import com.github.bytemania.cryptobalance.domain.dto.CryptoState;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Mapper {
    public static CryptoState fromEntry(Map.Entry<String, BigDecimal> entry) {
        return CryptoState.of(entry.getKey(), entry.getValue());
    }
}
