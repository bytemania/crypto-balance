package com.github.bytemania.cryptobalance.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CryptoTest {

    private static final Crypto BTC = Crypto.of("BTC", 50, true);
    private static final Crypto ETH = Crypto.of("ETH", 40, true);
    private static final Crypto WBTC = Crypto.of("WBTC", 1, true);
    private static final Crypto ABTC = Crypto.of("ABTC", 1, false);

    private static Crypto generateBtcWithMarketCapPercentage(double marketCapPercentage) {
        return Crypto.of("BTC", marketCapPercentage, false);
    }

    @Test
    @DisplayName("Should be ordered by percentage (reversed) after by symbol then by isStable")
    void shouldBeOrderedByPercentageReversedAfterBySymbol() {
        assertThat(BTC.compareTo(ETH)).isNegative();
        assertThat(WBTC.compareTo(ETH)).isPositive();
        assertThat(WBTC.compareTo(ABTC)).isPositive();
        assertThat(ABTC.compareTo(WBTC)).isNegative();
        assertThat(BTC.compareTo(BTC)).isZero();
        assertThat(ABTC.compareTo(Crypto.of("ABTC", 1, true))).isNegative();
    }

    @Test
    @DisplayName("Should compare market cap percentage with precision 2")
    void shouldCompareMarketCapPercentageWithPrecision2() {
        assertThat(generateBtcWithMarketCapPercentage(50.00).compareTo(generateBtcWithMarketCapPercentage(50.001))).isZero();
        assertThat(generateBtcWithMarketCapPercentage(50.01).compareTo(generateBtcWithMarketCapPercentage(50.005))).isZero();
        assertThat(generateBtcWithMarketCapPercentage(50.00).compareTo(generateBtcWithMarketCapPercentage(50.004))).isZero();
    }

    @Test
    @DisplayName("Should be equals for same object and same class ")
    void shouldBeEquals() {
        assertThat(BTC.equals(null)).isFalse();
        assertThat(BTC.equals(Crypto.of("WBTC", 50, true))).isFalse();
        assertThat(BTC.equals("OTHER")).isFalse();
        assertThat(BTC.equals(BTC)).isTrue();
    }

    @Test
    @DisplayName("Should be equals with precision 2 for market cap percentage")
    void shouldBeEqualsWithPrecision2ForMarketCapPercentage() {
        assertThat(generateBtcWithMarketCapPercentage(50.00).equals(generateBtcWithMarketCapPercentage(50.001))).isTrue();
        assertThat(generateBtcWithMarketCapPercentage(50.01).equals(generateBtcWithMarketCapPercentage(50.005))).isTrue();
        assertThat(generateBtcWithMarketCapPercentage(50.00).equals(generateBtcWithMarketCapPercentage(50.004))).isTrue();
        assertThat(generateBtcWithMarketCapPercentage(50.01).equals(generateBtcWithMarketCapPercentage(50.004))).isFalse();
    }

    @Test
    @DisplayName("Should calculate hashCode with precision 2 for MarketCapPercentage")
    void shouldCalculateHashCodeWithPrecision2ForMarketCapPercentage() {
        assertThat(generateBtcWithMarketCapPercentage(50.00).hashCode()).isEqualTo(generateBtcWithMarketCapPercentage(50.001).hashCode());
        assertThat(generateBtcWithMarketCapPercentage(50.01).hashCode()).isEqualTo(generateBtcWithMarketCapPercentage(50.005).hashCode());
        assertThat(generateBtcWithMarketCapPercentage(50.00).hashCode()).isEqualTo(generateBtcWithMarketCapPercentage(50.004).hashCode());
        assertThat(generateBtcWithMarketCapPercentage(50.01).hashCode()).isNotEqualTo(generateBtcWithMarketCapPercentage(50.004).hashCode());
    }

}
