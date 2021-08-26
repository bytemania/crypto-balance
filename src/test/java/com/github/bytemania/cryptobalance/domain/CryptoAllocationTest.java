package com.github.bytemania.cryptobalance.domain;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class CryptoAllocationTest {

    private final CryptoAllocation BTC = CryptoAllocation.of("BTC", 0 , BigDecimal.valueOf(50));
    private final CryptoAllocation ETH = CryptoAllocation.of("ETH", 0, BigDecimal.valueOf(40));
    private final CryptoAllocation WBTC = CryptoAllocation.of("WBTC", 0, BigDecimal.valueOf(1));
    private final CryptoAllocation ABTC = CryptoAllocation.of("ABTC", 0, BigDecimal.valueOf(1));

    @Test
    @DisplayName("Should be ordered by percentage (reversed) after by symbol")
    void shouldBeOrderedByPercentageReversedAfterBySymbol() {
        assertThat(BTC.compareTo(ETH)).isNegative();
        assertThat(WBTC.compareTo(ETH)).isPositive();
        assertThat(WBTC.compareTo(ABTC)).isPositive();
        assertThat(ABTC.compareTo(WBTC)).isNegative();
        assertThat(BTC.compareTo(BTC)).isZero();
    }

}