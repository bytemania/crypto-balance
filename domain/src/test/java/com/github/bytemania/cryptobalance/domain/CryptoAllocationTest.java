package com.github.bytemania.cryptobalance.domain;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class CryptoAllocationTest {

    private static final CryptoAllocation BTC = CryptoAllocation.of("BTC", 51 , false,
            BigDecimal.valueOf(50), CryptoAllocation.Operation.BUY, BigDecimal.TEN, BigDecimal.valueOf(40));
    private static final CryptoAllocation ETH = CryptoAllocation.of("ETH", 40 , false,
            BigDecimal.valueOf(40), CryptoAllocation.Operation.SELL, BigDecimal.valueOf(20), BigDecimal.valueOf(60));
    private static final CryptoAllocation WBTC = CryptoAllocation.of("WBTC", 1, false,
            BigDecimal.ONE, CryptoAllocation.Operation.KEEP, BigDecimal.ZERO, BigDecimal.ONE);
    private static final CryptoAllocation ABTC = CryptoAllocation.of("ABTC", 1, true,
            BigDecimal.ONE, CryptoAllocation.Operation.KEEP, BigDecimal.ZERO, BigDecimal.ONE);

    private static class BtcBuilder {
        static BtcBuilder of() {
            return new BtcBuilder();
        }

        private double marketCapPercentage = 1;
        private double amountToInvest = 10;

        BtcBuilder withMarketCapPercentage(double marketCapPercentage) {
            this.marketCapPercentage = marketCapPercentage;
            return this;
        }

        BtcBuilder withAmountToInvest(double amountToInvest) {
            this.amountToInvest = amountToInvest;
            return this;
        }

        CryptoAllocation build() {
            return CryptoAllocation.of("BTC",
                    marketCapPercentage,
                    true,
                    BigDecimal.valueOf(amountToInvest),
                    CryptoAllocation.Operation.KEEP,
                    BigDecimal.ZERO,
                    BigDecimal.valueOf(amountToInvest));
        }
    }

    private static CryptoAllocation generateBtcWithAmountToInvest(double amountToInvest) {
        return BtcBuilder.of()
                .withAmountToInvest(amountToInvest)
                .build();
    }

    private static CryptoAllocation generateBtcWithMarketCapPercentage(double marketCapPercentage) {
        return BtcBuilder.of()
                .withMarketCapPercentage(marketCapPercentage)
                .build();
    }

    @Test
    @DisplayName("Should be ordered by amount (reversed)")
    void shouldBeOrderedByAmountReversed() {
        assertThat(BTC.compareTo(ETH)).isNegative();
        assertThat(WBTC.compareTo(ETH)).isPositive();
        assertThat(WBTC.compareTo(ABTC)).isPositive();
        assertThat(ABTC.compareTo(WBTC)).isNegative();
        assertThat(BTC.compareTo(BTC)).isZero();
        assertThat(ABTC.compareTo(CryptoAllocation.of("ABTC", 0, false,
                BigDecimal.valueOf(10), CryptoAllocation.Operation.KEEP, BigDecimal.ZERO, BigDecimal.valueOf(10))))
                .isPositive();
        assertThat(ABTC.compareTo(CryptoAllocation.of("ABTC", 0, true,
                BigDecimal.valueOf(10), CryptoAllocation.Operation.KEEP, BigDecimal.ZERO, BigDecimal.valueOf(10))))
                .isPositive();
    }

    @Test
    @DisplayName("Should compare amount to invest with precision 2 in reverse order")
    void shouldCompareAmountToInvestWithPrecision2InReverseOrder() {
        assertThat(generateBtcWithAmountToInvest(50.00).compareTo(generateBtcWithAmountToInvest(50.001))).isZero();
        assertThat(generateBtcWithAmountToInvest(50.01).compareTo(generateBtcWithAmountToInvest(50.005))).isZero();
        assertThat(generateBtcWithAmountToInvest(50.00).compareTo(generateBtcWithAmountToInvest(50.004))).isZero();
        assertThat(generateBtcWithAmountToInvest(50.02).compareTo(generateBtcWithAmountToInvest(50.005))).isNegative();
        assertThat(generateBtcWithAmountToInvest(50.00).compareTo(generateBtcWithAmountToInvest(50.005))).isPositive();
    }

    @Test
    @DisplayName("Should compare market cap percentage with precision 2")
    void shouldCompareMarketCapPercentageWithPrecision2() {
        assertThat(generateBtcWithMarketCapPercentage(50.00).compareTo(generateBtcWithMarketCapPercentage(50.001))).isZero();
        assertThat(generateBtcWithMarketCapPercentage(50.01).compareTo(generateBtcWithMarketCapPercentage(50.005))).isZero();
        assertThat(generateBtcWithMarketCapPercentage(50.00).compareTo(generateBtcWithMarketCapPercentage(50.004))).isZero();
    }

    @Test
    @DisplayName("Should be equals with precision 2 for Amount To Invest")
    void shouldBeEqualsWithPrecision2ForAmountToInvest() {
        assertThat(generateBtcWithAmountToInvest(50.00).equals(generateBtcWithAmountToInvest(50.001))).isTrue();
        assertThat(generateBtcWithAmountToInvest(50.01).equals(generateBtcWithAmountToInvest(50.005))).isTrue();
        assertThat(generateBtcWithAmountToInvest(50.00).equals(generateBtcWithAmountToInvest(50.004))).isTrue();
        assertThat(generateBtcWithAmountToInvest(50.01).equals(generateBtcWithAmountToInvest(50.004))).isFalse();
    }

    @Test
    @DisplayName("Should be equals for same object and same class ")
    void shouldBeEquals() {
        assertThat(BTC.equals(null)).isFalse();
        assertThat(BTC.equals(CryptoAllocation.of("BTC", 51, true, BigDecimal.ONE,
                CryptoAllocation.Operation.KEEP, BigDecimal.ZERO, BigDecimal.ONE))).isFalse();
        assertThat(BTC.equals(CryptoAllocation.of("ETC", 51 , true,
                BigDecimal.valueOf(50), CryptoAllocation.Operation.KEEP, BigDecimal.ZERO, BigDecimal.valueOf(50))))
                .isFalse();
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
    @DisplayName("Should calculate hashCode with precision 2 for Amount To Invest")
    void shouldCalculateHashCodeWithPrecision2ForAmountToInvest() {
        assertThat(generateBtcWithAmountToInvest(50.00).hashCode()).isEqualTo(generateBtcWithAmountToInvest(50.001).hashCode());
        assertThat(generateBtcWithAmountToInvest(50.01).hashCode()).isEqualTo(generateBtcWithAmountToInvest(50.005).hashCode());
        assertThat(generateBtcWithAmountToInvest(50.00).hashCode()).isEqualTo(generateBtcWithAmountToInvest(50.004).hashCode());
        assertThat(generateBtcWithAmountToInvest(50.01).hashCode()).isNotEqualTo(generateBtcWithAmountToInvest(50.004).hashCode());
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
