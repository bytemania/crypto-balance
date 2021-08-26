package com.github.bytemania.cryptobalance.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CryptoTest {

    private final Crypto BTC = Crypto.of("BTC", 50);
    private final Crypto ETH = Crypto.of("ETH", 40);
    private final Crypto WBTC = Crypto.of("WBTC", 1);
    private final Crypto ABTC = Crypto.of("ABTC", 1);

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
