package com.github.bytemania.adapter.in.web.server.impl;

import com.github.bytemania.adapter.in.web.server.Fixture;
import com.github.bytemania.adapter.in.web.server.dto.Allocation;
import com.github.bytemania.adapter.in.web.server.dto.Response;
import com.github.bytemania.cryptobalance.domain.dto.AllocationResult;
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


}