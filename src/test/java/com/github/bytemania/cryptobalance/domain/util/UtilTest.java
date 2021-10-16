package com.github.bytemania.cryptobalance.domain.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class UtilTest {

    @Test
    @DisplayName("Normalize should round numbers to two decimals halfup")
    void shouldNormalize() {
        assertThat(Util.normalize(BigDecimal.ZERO)).isEqualTo(new BigDecimal("0.00"));
        assertThat(Util.normalize(BigDecimal.valueOf(1.234))).isEqualTo(BigDecimal.valueOf(1.23));
        assertThat(Util.normalize(BigDecimal.valueOf(1.235))).isEqualTo(BigDecimal.valueOf(1.24));
        assertThat(Util.normalize(BigDecimal.valueOf(1))).isEqualTo(new BigDecimal("1.00"));
    }


}