package com.github.bytemania.cryptobalance.adapter.in.web.server.impl;

import com.github.bytemania.cryptobalance.adapter.in.web.server.Fixture;
import com.github.bytemania.cryptobalance.adapter.in.web.server.dto.allocation.Allocation;
import com.github.bytemania.cryptobalance.adapter.in.web.server.dto.allocation.Response;
import com.github.bytemania.cryptobalance.adapter.in.web.server.dto.portfolio.Crypto;
import com.github.bytemania.cryptobalance.domain.dto.AllocationResult;
import com.github.bytemania.cryptobalance.domain.dto.CryptoState;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class MapperTest {

    @Test
    @DisplayName("Should translate an Allocation Result")
    void shouldTranslateAnAllocationResult() {

        Response response = Mapper.fromAllocationResult("USD", Fixture.allocationResult);

        assertThat(response).isNotNull();
        assertThat(response.getCurrency()).isEqualTo("USD");
        assertThat(response.getAmountToInvest()).isEqualTo(21.0);
        assertThat(response.getRest()).isEqualTo(1.0);
        assertThat(response.getAmountInvested()).isEqualTo(80.0);
        assertThat(response.getStableCrypto()).isEqualTo(
                Allocation
                        .builder()
                        .symbol("USDT")
                        .marketCapPercentage(20.0)
                        .amountToInvest(20.0)
                        .operation("KEEP")
                        .rebalanceToInvest(0.0)
                        .currentInvested(20.0)
                        .build());
        assertThat(response.getCryptos()).containsExactly(
                Allocation
                        .builder()
                        .symbol("BTC")
                        .marketCapPercentage(60.0)
                        .amountToInvest(60.0)
                        .operation("BUY")
                        .rebalanceToInvest(20.0)
                        .currentInvested(40.0)
                        .build(),
                Allocation
                        .builder()
                        .symbol("ADA")
                        .marketCapPercentage(20.0)
                        .amountToInvest(20.0)
                        .operation("KEEP")
                        .rebalanceToInvest(0.0)
                        .currentInvested(20.0)
                        .build()
                );
    }

    @Test
    @DisplayName("Should translate a Empty Allocation Result")
    void shouldTranslateAEmptyAllocationResult() {

        var allocation = AllocationResult.of(BigDecimal.ONE, BigDecimal.ONE, List.of());

        Response response = Mapper.fromAllocationResult("USD", allocation);

        assertThat(response).isNotNull();
        assertThat(response.getCurrency()).isEqualTo("USD");
        assertThat(response.getAmountToInvest()).isEqualTo(1.0);
        assertThat(response.getRest()).isEqualTo(1.0);
        assertThat(response.getAmountInvested()).isEqualTo(0.0);
        assertThat(response.getStableCrypto()).isEqualTo(Allocation.builder().build());
        assertThat(response.getCryptos()).isEmpty();
    }

    @Test
    @DisplayName("Should translate a Portfolio")
    void shouldTranslateAPortfolio() {
        var portfolio = Mapper.fromCryptoStateList("USD", Fixture.cryptoState);
        assertThat(portfolio).isNotNull();
        assertThat(portfolio.getCurrency()).isEqualTo("USD");
        assertThat(portfolio.getTotalAmountInvested()).isEqualTo(1200.0);
        assertThat(portfolio.getCryptos()).containsExactly(
                Crypto.builder()
                        .symbol("USDT")
                        .amountInvested(200.0)
                        .build(),
                Crypto.builder()
                        .symbol("BTC")
                        .amountInvested(1000.0)
                        .build()
        );
    }

    @Test
    @DisplayName("Should translate a empty Portfolio")
    void shouldTranslateAEmptyPortfolio() {
        var portfolio = Mapper.fromCryptoStateList("USD", List.of());
        assertThat(portfolio).isNotNull();
        assertThat(portfolio.getCurrency()).isEqualTo("USD");
        assertThat(portfolio.getTotalAmountInvested()).isEqualTo(0.0);
        assertThat(portfolio.getCryptos()).isEmpty();
    }

    @Test
    @DisplayName("Should translate a Crypto")
    void shouldTranslateACrypto() {
        CryptoState cryptoState = Mapper.fromCrypto(Crypto.builder().symbol("ETH").amountInvested(13).build());
        assertThat(cryptoState.getSymbol()).isEqualTo("ETH");
        assertThat(cryptoState.getInvested().doubleValue()).isEqualTo(13.00);

        cryptoState = Mapper.fromCrypto(Crypto.builder().symbol("ETH").amountInvested(13.004).build());
        assertThat(cryptoState.getSymbol()).isEqualTo("ETH");
        assertThat(cryptoState.getInvested().doubleValue()).isEqualTo(13.00);

        cryptoState = Mapper.fromCrypto(Crypto.builder().symbol("ETH").amountInvested(13.005).build());
        assertThat(cryptoState.getSymbol()).isEqualTo("ETH");
        assertThat(cryptoState.getInvested().doubleValue()).isEqualTo(13.01);
    }

}