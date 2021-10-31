package com.github.bytemania.cryptobalance.adapter.in.web.server.dto.portfolio;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CryptoTest {

    @Test
    @DisplayName("compareTo")
    void compareTo() {
        final Crypto crypto = new Crypto("BTC", 0.01, 1000.0);

        assertThat(crypto.compareTo(new Crypto("BTC", 0.01, 1000.1))).isPositive();
        assertThat(crypto.compareTo(new Crypto("BTC", 0.01, 1000.0))).isZero();
        assertThat(crypto.compareTo(crypto)).isZero();
        assertThat(crypto.compareTo(new Crypto("BTC", 0.01, 999.9))).isNegative();
        assertThat(crypto.compareTo(new Crypto("BTC", 0.011, 1000.0))).isNegative();
        assertThat(crypto.compareTo(new Crypto("BTC", 0.009, 1000.0))).isPositive();
        assertThat(crypto.compareTo(new Crypto("ETH", 0.01, 1000.0))).isNegative();
        assertThat(crypto.compareTo(new Crypto("ADA", 0.01, 1000.0))).isPositive();
    }
}
