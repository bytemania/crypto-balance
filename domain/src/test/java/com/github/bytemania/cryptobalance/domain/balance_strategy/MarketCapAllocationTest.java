package com.github.bytemania.cryptobalance.domain.balance_strategy;

import com.github.bytemania.cryptobalance.domain.dto.Crypto;
import com.github.bytemania.cryptobalance.domain.dto.CryptoAllocation;
import com.github.bytemania.cryptobalance.domain.dto.CryptoState;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class MarketCapAllocationTest {

    private static MarketCapAllocation createMarketCapAllocation(
            double stableCoinPercentage,
            double amountToInvest,
            double minValueToAllocate,
            Set<Crypto> marketCap,
            Set<CryptoState> currentState) {
        Crypto stable = createStableCrypto(stableCoinPercentage);
        return new MarketCapAllocation(
                marketCap,
                stable,
                BigDecimal.valueOf(amountToInvest),
                BigDecimal.valueOf(minValueToAllocate),
                currentState);
    }

    private static Set<Crypto> createMarketCap() {
        Crypto btc = new Crypto("BTC", 55.57, BigDecimal.valueOf(60500), false);
        Crypto eth = new Crypto("ETH", 28.03, BigDecimal.valueOf(4200), false);
        Crypto ada = new Crypto("ADA", 8.38, BigDecimal.valueOf(1.93), false);
        Crypto bnb = new Crypto("BNB", 4.02, BigDecimal.valueOf(520), false);
        Crypto tether = new Crypto("USDT", 1.00, BigDecimal.ONE, true);
        Crypto ripple = new Crypto("XRP", 1.00, BigDecimal.valueOf(1.10), false);
        Crypto busd = new Crypto("BUSD", 1.00, BigDecimal.valueOf(1.01), true);

        return Set.of(btc, eth, ada, bnb, tether, ripple, busd);
    }

    private static Crypto createStableCrypto(double stableCoinPercentage) {
        return new Crypto("BUSD", stableCoinPercentage, BigDecimal.ONE, true);
    }

    @Test
    @DisplayName("no state and no cap")
    void noStateAndNoCap() {
        var allocator = createMarketCapAllocation(20, 200, 25, Set.of(), Set.of());

        var result = allocator.allocate();

        assertThat(result.getAmountToInvest().doubleValue()).isEqualTo(200.0);
        assertThat(result.getHoldings().doubleValue()).isEqualTo(0);
        assertThat(result.getTotalInvested().doubleValue()).isEqualTo(0);
        assertThat(result.getRest().doubleValue()).isEqualTo(160.0);
        assertThat(result.getStableCrypto().toString())
                .isEqualTo(new CryptoAllocation("BUSD", BigDecimal.ONE, BigDecimal.ZERO, BigDecimal.ZERO, 20.0, new BigDecimal("40.00")).toString());
        assertThat(result.getCryptos()).isEmpty();
    }

    @Test
    @DisplayName("State and No Cap")
    void stateAndNoCap() {
        var allocator = createMarketCapAllocation(20, 200, 25, Set.of(),
                Set.of(
                        new CryptoState("BTC", BigDecimal.valueOf(0.002), BigDecimal.valueOf(1000)),
                        new CryptoState("ETH", BigDecimal.valueOf(0.2), BigDecimal.valueOf(700)),
                        new CryptoState("BUSD", BigDecimal.valueOf(700), BigDecimal.valueOf(700))
                ));

        var result = allocator.allocate();

        assertThat(result.getAmountToInvest().doubleValue()).isEqualTo(200.0);
        assertThat(result.getHoldings().doubleValue()).isEqualTo(2400.0);
        assertThat(result.getTotalInvested().doubleValue()).isEqualTo(2400.0);
        assertThat(result.getRest().doubleValue()).isEqualTo(2080.0);
        assertThat(result.getMinValueToAllocate().doubleValue()).isEqualTo(25.0);
        assertThat(result.getStableCrypto()).isEqualTo(new CryptoAllocation("BUSD", new BigDecimal("1.01"), BigDecimal.valueOf(700), BigDecimal.valueOf(700), 20.0, BigDecimal.valueOf(-180.0)));
        assertThat(result.getCryptos().stream().map(CryptoAllocation::toString).collect(Collectors.toSet()))
                .hasSize(2)
                .containsExactlyInAnyOrder(
                        new CryptoAllocation("BTC", BigDecimal.ZERO, new BigDecimal("0.002"), new BigDecimal("1000"), 0, new BigDecimal("-1000")).toString(),
                        new CryptoAllocation("ETH", BigDecimal.ZERO, new BigDecimal("0.2"), new BigDecimal("700"), 0, new BigDecimal("-700")).toString());
    }

    @Test
    @DisplayName("MinValueToAllocate Bigger Than Amount To Invest Without Cap or State")
    void minValueToAllocateBiggerThanAmountToInvestWithoutCapOrState() {
        var allocator = createMarketCapAllocation(20, 200, 250, Set.of(), Set.of());

        var result = allocator.allocate();

        assertThat(result.getAmountToInvest().doubleValue()).isEqualTo(200.0);
        assertThat(result.getHoldings().doubleValue()).isEqualTo(0);
        assertThat(result.getTotalInvested().doubleValue()).isEqualTo(0.0);
        assertThat(result.getRest().doubleValue()).isEqualTo(200.0);
        assertThat(result.getMinValueToAllocate().doubleValue()).isEqualTo(250.0);
        assertThat(result.getStableCrypto().toString())
                .isEqualTo(new CryptoAllocation("BUSD", BigDecimal.ONE, BigDecimal.ZERO, BigDecimal.ZERO, 20.0, BigDecimal.ZERO).toString());
        assertThat(result.getCryptos()).isEmpty();
    }

    @Test
    @DisplayName("MinValueToAllocate Bigger Than Amount To Invest Without Cap")
    void minValueToAllocateBiggerThanAmountToInvest() {
        var allocator = createMarketCapAllocation(20, 200, 250,
                Set.of(new Crypto("BUSD", 1.03, BigDecimal.ONE, true)),
                Set.of(new CryptoState("BUSD", BigDecimal.TEN, BigDecimal.TEN)));

        var result = allocator.allocate();

        assertThat(result.getAmountToInvest().doubleValue()).isEqualTo(200.0);
        assertThat(result.getHoldings().doubleValue()).isEqualTo(10.0);
        assertThat(result.getTotalInvested().doubleValue()).isEqualTo(10.0);
        assertThat(result.getRest().doubleValue()).isEqualTo(210.0);
        assertThat(result.getMinValueToAllocate().doubleValue()).isEqualTo(250.0);
        assertThat(result.getStableCrypto().toString())
                .isEqualTo(new CryptoAllocation("BUSD", BigDecimal.ONE, BigDecimal.TEN, BigDecimal.TEN, 20.0, BigDecimal.ZERO).toString());
        assertThat(result.getCryptos()).isEmpty();
    }

    @Test
    @DisplayName("partial allocation with no state")
    void partialAllocationWithNoState() {
        var allocator = createMarketCapAllocation(20, 200, 25,
                createMarketCap(),
                Set.of());

        var result = allocator.allocate();

        assertThat(result.getAmountToInvest().doubleValue()).isEqualTo(200.0);
        assertThat(result.getHoldings().doubleValue()).isEqualTo(0.0);
        assertThat(result.getTotalInvested().doubleValue()).isEqualTo(0.0);
        assertThat(result.getRest().doubleValue()).isEqualTo(0.0);
        assertThat(result.getMinValueToAllocate().doubleValue()).isEqualTo(25.0);
        assertThat(result.getStableCrypto().toString())
                .isEqualTo(new CryptoAllocation("BUSD", new BigDecimal("1.01"), BigDecimal.ZERO, BigDecimal.ZERO, 20.0, new BigDecimal("40.00")).toString());
        assertThat(result.getCryptos().stream().map(CryptoAllocation::toString).collect(Collectors.toSet()))
                .hasSize(2)
                .contains(new CryptoAllocation("BTC", BigDecimal.valueOf(60500), BigDecimal.ZERO, BigDecimal.ZERO, 55.57, new BigDecimal("111.14000")).toString())
                .contains(new CryptoAllocation("ETH", BigDecimal.valueOf(4200), BigDecimal.ZERO, BigDecimal.ZERO, 28.03, new BigDecimal("48.86000")).toString());
    }

    @Test
    @DisplayName("partial allocation")
    void partialAllocation() {
        var allocator = createMarketCapAllocation(20, 200, 25,
                createMarketCap(),
                Set.of(
                        new CryptoState("ETH", BigDecimal.valueOf(0.2), BigDecimal.valueOf(700)),
                        new CryptoState("BUSD", BigDecimal.valueOf(20), BigDecimal.valueOf(20))
                ));

        var result = allocator.allocate();

        assertThat(result.getAmountToInvest().doubleValue()).isEqualTo(200.0);
        assertThat(result.getHoldings().doubleValue()).isEqualTo(860.2);
        assertThat(result.getTotalInvested().doubleValue()).isEqualTo(720.0);
        assertThat(result.getRest().doubleValue()).isEqualTo(0.0);
        assertThat(result.getMinValueToAllocate().doubleValue()).isEqualTo(25.0);
        assertThat(result.getStableCrypto().toString())
                .isEqualTo(new CryptoAllocation("BUSD", BigDecimal.valueOf(1.01), BigDecimal.valueOf(20), BigDecimal.valueOf(20), 20.0, new BigDecimal("191.840")).toString());
        assertThat(result.getCryptos().stream().map(CryptoAllocation::toString).collect(Collectors.toSet()))
                .hasSize(2)
                .containsExactlyInAnyOrder(
                        new CryptoAllocation("BTC", BigDecimal.valueOf(60500), BigDecimal.ZERO, BigDecimal.ZERO, 55.57, new BigDecimal("589.153140")).toString(),
                        new CryptoAllocation("ETH", BigDecimal.valueOf(4200), BigDecimal.valueOf(0.2), BigDecimal.valueOf(700), 28.03, new BigDecimal("-580.993140")).toString());
    }

    @Test
    @DisplayName("fully allocation")
    void fullAllocation() {
        var allocator = createMarketCapAllocation(3, 200, 1,
                createMarketCap(),
                Set.of(
                        new CryptoState("ETH", BigDecimal.valueOf(0.02), BigDecimal.valueOf(750)),
                        new CryptoState("BUSD", BigDecimal.valueOf(50), BigDecimal.valueOf(50))
                ));

        var result = allocator.allocate();

        assertThat(result.getAmountToInvest().doubleValue()).isEqualTo(200.0);
        assertThat(result.getHoldings().doubleValue()).isEqualTo(134.50);
        assertThat(result.getTotalInvested().doubleValue()).isEqualTo(800.0);
        assertThat(result.getRest().doubleValue()).isEqualTo(0.0);
        assertThat(result.getMinValueToAllocate().doubleValue()).isEqualTo(1.0);
        assertThat(result.getStableCrypto().toString())
                .isEqualTo(new CryptoAllocation("BUSD", BigDecimal.valueOf(1.01), BigDecimal.valueOf(50), BigDecimal.valueOf(50), 3.0, new BigDecimal("-40.4650")).toString());
        assertThat(result.getCryptos())
                .hasSize(5)
                .containsExactlyInAnyOrder(
                        new CryptoAllocation("BNB", BigDecimal.valueOf(520), BigDecimal.ZERO, BigDecimal.ZERO, 4.02, new BigDecimal("13.4468999999999966550")),
                        new CryptoAllocation("XRP", BigDecimal.valueOf(1.1), BigDecimal.ZERO, BigDecimal.ZERO, 1.0, new BigDecimal("3.3450")),
                        new CryptoAllocation("ETH", BigDecimal.valueOf(4200), new BigDecimal("0.02"), new BigDecimal("750"), 28.03, new BigDecimal("9.760350")),
                        new CryptoAllocation("ADA", BigDecimal.valueOf(1.93), BigDecimal.ZERO, BigDecimal.ZERO, 8.38, new BigDecimal("28.0311000000000033450")),
                        new CryptoAllocation("BTC", BigDecimal.valueOf(60500), BigDecimal.ZERO, BigDecimal.ZERO, 55.57, new BigDecimal("185.881650"))
                );
    }

}
