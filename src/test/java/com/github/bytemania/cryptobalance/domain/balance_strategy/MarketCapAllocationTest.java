package com.github.bytemania.cryptobalance.domain.balance_strategy;

import com.github.bytemania.cryptobalance.domain.Crypto;
import com.github.bytemania.cryptobalance.domain.CryptoAllocation;
import org.assertj.core.data.Offset;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class MarketCapAllocationTest {

    private static MarketCapAllocation createMarketCapAllocation(double stableCoinPercentage, double minValueToAllocate) {
        Crypto btc = Crypto.of("BTC", 55.57, false);
        Crypto eth = Crypto.of("ETH", 28.03, false);
        Crypto ada = Crypto.of("ADA", 8.38, false);
        Crypto bnb = Crypto.of("BNB", 4.02, false);
        Crypto tether = Crypto.of("USDT", 2.00, true);
        Crypto ripple = Crypto.of("XRP", 1.00, false);

        Crypto stable = createStableCrypto(stableCoinPercentage);

        return MarketCapAllocation.of(
                List.of(btc, eth, ada, bnb, tether, ripple),
                stable,
                new BigDecimal("100"),
                BigDecimal.valueOf(minValueToAllocate));
    }

    private static Crypto createStableCrypto(double stableCoinPercentage) {
        return Crypto.of("BUSD", stableCoinPercentage, true);
    }

    @Test
    @DisplayName("Should allocate all to stableCoin if amount to invest is lower than minimum")
    void shouldAllocateAllToStableCoinIfAmountToInvestIsLowerThanMinimum() {
        var moreMinThanAmountToInvest = createMarketCapAllocation(20, 1000);
        assertThat(moreMinThanAmountToInvest.allocate()).isEqualTo(List.of(
                CryptoAllocation.of("BUSD", 100, true, BigDecimal.valueOf(100))));

        var sameAmountToInvestThanMin = createMarketCapAllocation(20, 100);
        assertThat(sameAmountToInvestThanMin.allocate()).isEqualTo(List.of(
                CryptoAllocation.of("BUSD", 100, true, BigDecimal.valueOf(100))));
    }

    @Test
    @DisplayName("Should full allocate if the stable and the list get 100% allocation and money is left")
    void shouldAllocateAllCoinsAndMoneyIsLeft() {
        var fullyAllocation = createMarketCapAllocation(2, 1);
        assertThat(fullyAllocation.allocate()).containsExactly(
                CryptoAllocation.of("BTC", 55.57, false, BigDecimal.valueOf(55.5700)),
                CryptoAllocation.of("ETH", 28.03, false, BigDecimal.valueOf(28.0300)),
                CryptoAllocation.of("ADA", 8.38, false, BigDecimal.valueOf(8.38)),
                CryptoAllocation.of("BNB", 4.02, false, BigDecimal.valueOf(4.02)),
                CryptoAllocation.of("BUSD", 2.0, true, BigDecimal.valueOf(2.00)),
                CryptoAllocation.of("XRP", 1.0, false, BigDecimal.valueOf(1.00))
        );

        var allocatedPercentage = fullyAllocation.allocate().stream()
                .map(CryptoAllocation::getMarketCapPercentage)
                .reduce(Double::sum)
                .get();

        var amountInvested = fullyAllocation.allocate().stream()
                .map(CryptoAllocation::getAmountToInvest)
                .reduce(BigDecimal::add)
                .get();

        assertThat(allocatedPercentage).isCloseTo(99, Offset.offset(0.001));
        assertThat(amountInvested).isCloseTo(BigDecimal.valueOf(99), Offset.offset(BigDecimal.valueOf(0.001)));
        assertThat(fullyAllocation.getRest()).isCloseTo(BigDecimal.ONE, Offset.offset(BigDecimal.valueOf(0.001)));
        assertThat(fullyAllocation.allocate().stream().filter(CryptoAllocation::isStableCoin).count()).isEqualTo(1);
    }

    @Test
    @DisplayName("Should full allocate Part of Portfolio")
    void shouldAllocatePartOfPortfolio() {
        var partialAllocation = createMarketCapAllocation(20, 10);
        assertThat(partialAllocation.allocate()).containsExactly(
                CryptoAllocation.of("BTC", 55.57, false, BigDecimal.valueOf(55.57)),
                CryptoAllocation.of("ETH", 24.43, false, BigDecimal.valueOf(24.43)),
                CryptoAllocation.of("BUSD", 20.0, true, BigDecimal.valueOf(20.00))
        );

        var allocatedPercentage = partialAllocation.allocate().stream()
                .map(CryptoAllocation::getMarketCapPercentage)
                .reduce(Double::sum)
                .get();

        var amountInvested = partialAllocation.allocate().stream()
                .map(CryptoAllocation::getAmountToInvest)
                .reduce(BigDecimal::add)
                .get();

        assertThat(allocatedPercentage).isEqualTo(100);
        assertThat(amountInvested).isCloseTo(BigDecimal.valueOf(100), Offset.offset(BigDecimal.valueOf(0.001)));
        assertThat(partialAllocation.getRest()).isEqualTo(BigDecimal.ZERO);
        assertThat(partialAllocation.allocate().stream().filter(CryptoAllocation::isStableCoin).count()).isEqualTo(1);
    }

}