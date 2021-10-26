package com.github.bytemania.cryptobalance.adapter.in.web.server.impl;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ValidationTest {

    @Test
    @DisplayName("Should validate the parameters")
    void shouldValidateTheParameters() throws ValidationException {
        Validation.allocation(0.0, 0, 0);
        Validation.allocation(100.0, 1, 0);
        Validation.allocation(20.0, 200, 25);

        assertThatThrownBy(() -> Validation.allocation(-1.0, 0, 0))
                .isInstanceOf(ValidationException.class)
                .hasMessage("stableCryptoPercentage must be [0..100] value: -1.0");

        assertThatThrownBy(() -> Validation.allocation(100.001, 0, 0))
                .isInstanceOf(ValidationException.class)
                .hasMessage("stableCryptoPercentage must be [0..100] value: 100.001");

        assertThatThrownBy(() -> Validation.allocation(50, -1.0, 0))
                .isInstanceOf(ValidationException.class)
                .hasMessage("valueToInvest must be >= 0 value: -1.0");

        assertThatThrownBy(() -> Validation.allocation(50, 1.0, -1.0))
                .isInstanceOf(ValidationException.class)
                .hasMessage("minValueToAllocate must be >= 0 value: -1.0");

        assertThatThrownBy(() -> Validation.allocation(50, 10.0, 100.0))
                .isInstanceOf(ValidationException.class)
                .hasMessage("minValueToAllocate must be <= valueToInvest value: 100.0 valueToInvest: 10.0");
    }
}