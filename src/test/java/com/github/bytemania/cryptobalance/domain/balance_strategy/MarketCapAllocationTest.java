package com.github.bytemania.cryptobalance.domain.balance_strategy;

import com.github.bytemania.cryptobalance.domain.Crypto;
import com.github.bytemania.cryptobalance.domain.CryptoAllocation;
import com.github.bytemania.cryptobalance.domain.CryptoState;
import org.assertj.core.data.Offset;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class MarketCapAllocationTest {

    private static MarketCapAllocation createMarketCapAllocation(
            double stableCoinPercentage,
            double minValueToAllocate) {

        return createMarketCapAllocation(
                stableCoinPercentage,
                100,
                minValueToAllocate,
                createMarketCap(),
                List.of());
    }

    private static MarketCapAllocation createMarketCapAllocation(
            double stableCoinPercentage,
            double amountToInvest,
            double minValueToAllocate,
            List<Crypto> marketCap,
            List<CryptoState> currentState) {
        Crypto stable = createStableCrypto(stableCoinPercentage);
        return MarketCapAllocation.of(
                marketCap,
                stable,
                BigDecimal.valueOf(amountToInvest),
                BigDecimal.valueOf(minValueToAllocate),
                currentState);
    }

    private static List<Crypto> createMarketCap() {
        Crypto btc = Crypto.of("BTC", 55.57, false);
        Crypto eth = Crypto.of("ETH", 28.03, false);
        Crypto ada = Crypto.of("ADA", 8.38, false);
        Crypto bnb = Crypto.of("BNB", 4.02, false);
        Crypto tether = Crypto.of("USDT", 2.00, true);
        Crypto ripple = Crypto.of("XRP", 1.00, false);

        return List.of(btc, eth, ada, bnb, tether, ripple);
    }

    private static Crypto createStableCrypto(double stableCoinPercentage) {
        return Crypto.of("BUSD", stableCoinPercentage, true);
    }

    private static CryptoAllocation createAllocation(String symbol,
                                                     double marketCapPercentage,
                                                     boolean isStableCoin,
                                                     double amountToInvest) {
        return CryptoAllocation.of(symbol,
                marketCapPercentage,
                isStableCoin,
                BigDecimal.valueOf(amountToInvest),
                CryptoAllocation.Operation.KEEP,
                BigDecimal.ZERO,
                BigDecimal.ZERO);
    }

    private static void assertRebalance(List<CryptoAllocation> allocations, List<CryptoState> state, double minToInvest) {

        Map<String, BigDecimal> currentState = state.stream()
                .collect(Collectors.toMap(CryptoState::getSymbol, CryptoState::getInvested));

        var totalInvested = 100 + currentState.values().stream()
                .map(BigDecimal::doubleValue)
                .reduce(Double::sum).get();

        for (var allocation: allocations) {
            String symbol = allocation.getSymbol();
            double amountToInvest = allocation.getAmountToInvest().doubleValue();
            double alreadyInvested = currentState.getOrDefault(symbol, BigDecimal.ZERO).doubleValue();
            double cap = allocation.getMarketCapPercentage();

            var isAmountExpected = Math.abs(amountToInvest - cap / 100 * totalInvested) < 0.01
                    || amountToInvest == minToInvest;

            CryptoAllocation.Operation expectedOperation;
            if (Math.abs(amountToInvest - alreadyInvested) <= 0.001) expectedOperation = CryptoAllocation.Operation.KEEP;
            else if (amountToInvest - alreadyInvested < 0) expectedOperation = CryptoAllocation.Operation.SELL;
            else expectedOperation = CryptoAllocation.Operation.BUY;

            assertThat(allocation.getCurrentInvested().doubleValue()).isCloseTo(alreadyInvested, Offset.offset(0.001));
            assertThat(isAmountExpected).isTrue();
            assertThat(allocation.getRebalanceInvestment().doubleValue())
                    .isCloseTo(Math.abs(amountToInvest - alreadyInvested), Offset.offset(0.01));
            assertThat(allocation.getRebalanceOperation()).isEqualTo(expectedOperation);
        }
    }

    @Test
    @DisplayName("Should allocate all to stableCoin if amount to invest is lower than minimum")
    void shouldAllocateAllToStableCoinIfAmountToInvestIsLowerThanMinimum() {
        var moreMinThanAmountToInvest = createMarketCapAllocation(20, 1000);
        assertThat(moreMinThanAmountToInvest.allocate()).isEqualTo(List.of(
                createAllocation("BUSD", 100, true,100)));

        var sameAmountToInvestThanMin = createMarketCapAllocation(20, 100);
        assertThat(sameAmountToInvestThanMin.allocate()).isEqualTo(List.of(
                createAllocation("BUSD", 100, true, 100)));
    }

    @Test
    @DisplayName("Should full allocate if the stable and the list get 100% allocation and money is left")
    void shouldAllocateAllCoinsAndMoneyIsLeft() {
        var fullyAllocation = createMarketCapAllocation(2, 1);
        assertThat(fullyAllocation.allocate()).containsExactly(
                createAllocation("BTC", 55.57, false, 55.57),
                createAllocation("ETH", 28.03, false, 28.03),
                createAllocation("ADA", 8.38, false, 8.38),
                createAllocation("BNB", 4.02, false, 4.02),
                createAllocation("BUSD", 2.0, true, 2.00),
                createAllocation("XRP", 1.0, false, 1.00)
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
                createAllocation("BTC", 55.57, false, 55.57),
                createAllocation("ETH", 24.43, false, 24.43),
                createAllocation("BUSD", 20.0, true, 20.00)
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

    @Test
    @DisplayName("Should fully allocate and rebalance")
    void shouldFullyAllocateAndRebalance() {
        var cap = createMarketCap();
        var state = List.of(
                CryptoState.of("BTC", BigDecimal.valueOf(100)),
                CryptoState.of("ETH", BigDecimal.valueOf(100)),
                CryptoState.of("ADA", BigDecimal.valueOf(100)),
                CryptoState.of("BUSD", BigDecimal.valueOf(10))
            );

        var marketCapAllocation = createMarketCapAllocation(2, 100, 1, cap, state);
        var allocations =  marketCapAllocation.allocate();
        assertRebalance(allocations, state, 1);
    }

    @Test
    @DisplayName("Should partial allocate and rebalance")
    void shouldPartialAllocateAndRebalance() {
        var cap = createMarketCap();
        var state = List.of(
                CryptoState.of("BTC", BigDecimal.valueOf(100)),
                CryptoState.of("ETH", BigDecimal.valueOf(100)),
                CryptoState.of("ADA", BigDecimal.valueOf(10)),
                CryptoState.of("BUSD", BigDecimal.valueOf(10)),
                CryptoState.of("XRP", BigDecimal.valueOf(90))
        );

        var marketCapAllocation = createMarketCapAllocation(20, 100, 10, cap, state);
        var allocations =  marketCapAllocation.allocate();
        assertRebalance(allocations, state, 1);
    }

    @Test
    @DisplayName("Only buy stable if theres no cap")
    void shouldOnlyByStableIfNoCap() {
        var cap = List.<Crypto>of();
        var state = List.of(
                CryptoState.of("BTC", BigDecimal.valueOf(100)),
                CryptoState.of("ETH", BigDecimal.valueOf(100)),
                CryptoState.of("ADA", BigDecimal.valueOf(10)),
                CryptoState.of("BUSD", BigDecimal.valueOf(10)),
                CryptoState.of("XRP", BigDecimal.valueOf(90))
        );

        var marketCapAllocation = createMarketCapAllocation(20, 100, 10, cap, state);
        var allocations =  marketCapAllocation.allocate();
        assertRebalance(allocations, state, 1);
        assertThat(allocations.get(0)).isEqualTo(CryptoAllocation.of("BUSD", 20.0, true,
                BigDecimal.valueOf(82.00), CryptoAllocation.Operation.BUY, BigDecimal.valueOf(72.00),
                BigDecimal.valueOf(10.00)));
        assertThat(allocations.subList(1, allocations.size() - 1).stream()
                .map(CryptoAllocation::getRebalanceOperation)
                .collect(Collectors.toSet())).isEqualTo(Set.of(CryptoAllocation.Operation.SELL));
    }

    @Test
    @DisplayName("Keep the money if is already there")
    void shouldKeepTheInvestmentIfIsInTheState() {
        var cap = List.of(Crypto.of("BUSD", 100, false));
        var state = List.of(CryptoState.of("BUSD", BigDecimal.valueOf(100)));

        var marketCapAllocation = createMarketCapAllocation(100, 0, 10, cap, state);
        var allocations =  marketCapAllocation.allocate();
        assertThat(allocations.size()).isEqualTo(1);
        var allocation = allocations.get(0);
        assertThat(allocation.getSymbol()).isEqualTo("BUSD");
        assertThat(allocation.getMarketCapPercentage()).isEqualTo(100);
        assertThat(allocation.isStableCoin()).isTrue();
        assertThat(allocation.getAmountToInvest().doubleValue()).isEqualTo(100);
        assertThat(allocation.getRebalanceOperation()).isEqualTo(CryptoAllocation.Operation.KEEP);
        assertThat(allocation.getRebalanceInvestment().doubleValue()).isEqualTo(0);
        assertThat(allocation.getCurrentInvested().doubleValue()).isEqualTo(100);
    }
}
