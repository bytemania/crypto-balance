package com.github.bytemania.cryptobalance.adapter.in.web.server;

import com.github.bytemania.cryptobalance.adapter.in.web.server.dto.allocation.Allocation;
import com.github.bytemania.cryptobalance.adapter.in.web.server.dto.portfolio.Crypto;
import com.github.bytemania.cryptobalance.adapter.in.web.server.dto.portfolio.GetPortfolio;
import com.github.bytemania.cryptobalance.domain.dto.CryptoAllocation;
import com.github.bytemania.cryptobalance.domain.dto.CryptoState;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class WebServerMapperTest {
    @Test
    @DisplayName("cryptoStateToCrypto")
    void cryptoStateToCrypto() {
        CryptoState cryptoState = new CryptoState("BTC", BigDecimal.ONE, BigDecimal.TEN);
        assertThat(WebServerMapper.INSTANCE.cryptoStateToCrypto(cryptoState))
                .isEqualTo(Crypto.builder()
                        .symbol(cryptoState.getSymbol())
                        .holding(cryptoState.getHolding().doubleValue())
                        .amountInvested(cryptoState.getInvested().doubleValue())
                        .build());

        assertThat(WebServerMapper.INSTANCE.cryptoStateToCrypto(null)).isNull();
    }

    @Test
    @DisplayName("cryptoToCryptoState")
    void cryptoToCryptoState() {
        Crypto crypto = new Crypto("BTC", 1.0, 10.0);
        assertThat(WebServerMapper.INSTANCE.cryptoToCryptoState(crypto))
                .isEqualTo(new CryptoState("BTC", BigDecimal.valueOf(1.0), BigDecimal.valueOf(10.0)));

        assertThat(WebServerMapper.INSTANCE.cryptoToCryptoState(null)).isNull();
    }

    @Test
    @DisplayName("createGetPorfolio")
    void createGetPortfolio() {
        assertThat(WebServerMapper.INSTANCE.createGetPorfolio("USD", Fixture.cryptoState))
                .isEqualTo(GetPortfolio.builder()
                        .currency("USD")
                        .totalAmountInvested(1200.0)
                        .cryptos(List.of(
                                new Crypto("BTC", 0.02, 1000.0),
                                new Crypto("BUSD", 200, 200.0)))
                        .build());
    }

    @Test
    @DisplayName("cryptoAllocationToAllocation")
    void cryptoAllocationToAllocation() {
        Allocation allocation = WebServerMapper.INSTANCE.cryptoAllocationToAllocation(
                new CryptoAllocation("BTC",
                        BigDecimal.valueOf(65000),
                        BigDecimal.valueOf(0.001),
                        BigDecimal.valueOf(650),
                        55.65,
                        BigDecimal.valueOf(20)));

        assertThat(allocation.toString())
                .isEqualTo("Allocation(symbol=BTC, price=65000.0, holding=0.001, invested=650.0, marketCapPercentage=55.65, rebalance=20.0)");

        assertThat(WebServerMapper.INSTANCE.cryptoAllocationToAllocation(null)).isNull();
    }

    @Test
    @DisplayName("allocationResultToResult")
    void allocationResultToResult() {
        var result = WebServerMapper.INSTANCE.allocationResultToResult("USD", Fixture.allocationResult);
        assertThat(result).isNotNull();
        assertThat(result.getCurrency()).isEqualTo("USD");
        assertThat(result.getAmountToInvest().doubleValue()).isEqualTo(200.0);
        assertThat(result.getHoldings()).isEqualTo(1201.0);
        assertThat(result.getTotalInvested().doubleValue()).isEqualTo(1200.0);
        assertThat(result.getRest().doubleValue()).isEqualTo(1.0);
        assertThat(result.getMinValueToAllocate()).isEqualTo(25.0);
        assertThat(result.getStableCrypto().toString()).isEqualTo("Allocation(symbol=BUSD, price=1.0, holding=121.0, invested=200.0, marketCapPercentage=20.0, rebalance=21.0)");
        assertThat(result.getCryptos().stream().map(Allocation::toString).collect(Collectors.toList()))
                .hasSize(3)
                .containsExactly("Allocation(symbol=BTC, price=65000.0, holding=0.001, invested=650.0, marketCapPercentage=55.0, rebalance=20.0)",
                        "Allocation(symbol=ADA, price=2.0, holding=220.0, invested=450.0, marketCapPercentage=28.0, rebalance=-40.0)",
                        "Allocation(symbol=DOGE, price=0.2, holding=10.0, invested=100.0, marketCapPercentage=15.0, rebalance=30.0)");

        var resultWithoutAllocation = WebServerMapper.INSTANCE.allocationResultToResult("USD", null);

        assertThat(resultWithoutAllocation).isNotNull();
        assertThat(resultWithoutAllocation.getCurrency()).isEqualTo("USD");
        assertThat(resultWithoutAllocation.getAmountToInvest()).isNull();
        assertThat(resultWithoutAllocation.getHoldings()).isZero();
        assertThat(resultWithoutAllocation.getTotalInvested()).isNull();
        assertThat(resultWithoutAllocation.getRest()).isNull();
        assertThat(resultWithoutAllocation.getMinValueToAllocate()).isNull();
        assertThat(resultWithoutAllocation.getStableCrypto()).isNull();
        assertThat(resultWithoutAllocation.getCryptos()).isNull();

        var resultWithoutAllocationAndCurrency = WebServerMapper.INSTANCE.allocationResultToResult(null, null);

        assertThat(resultWithoutAllocationAndCurrency).isNull();
    }

}