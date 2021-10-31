package com.github.bytemania.cryptobalance.adapter.in.web.server.dto.allocation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AllocationTest {

    @Test
    @DisplayName("compareTo")
    void compareTo() {
        var allocation = new Allocation("BTC", 11D, 10D, 100D, 50, -5D);

        assertThat(allocation.compareTo(new Allocation("BTC", 11D, 10D, 100D, 51, -5D)))
                .isPositive();
        assertThat(allocation.compareTo(new Allocation("BTC", 11D, 10D, 100D, 49, -5D)))
                .isNegative();
        assertThat(allocation.compareTo(new Allocation("BTC", 11D, 10D, 100D, 50, -5D)))
                .isZero();
        assertThat(allocation.compareTo(allocation))
                .isZero();


    }

}