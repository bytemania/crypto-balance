package com.github.bytemania.cryptobalance.domain.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class UtilTest {

    @Test
    @DisplayName("Normalize should round bigDecimals to two decimals halfup")
    void shouldNormalizeBigDecimals() {
        assertThat(Util.normalize(BigDecimal.ZERO)).isEqualTo(new BigDecimal("0.00"));
        assertThat(Util.normalize(BigDecimal.valueOf(1.234))).isEqualTo(BigDecimal.valueOf(1.23));
        assertThat(Util.normalize(BigDecimal.valueOf(1.235))).isEqualTo(BigDecimal.valueOf(1.24));
        assertThat(Util.normalize(BigDecimal.valueOf(1))).isEqualTo(new BigDecimal("1.00"));
    }

    @Test
    @DisplayName("Normalize should round doubles to two decimals halfup")
    void shouldNormalizeDoubles() {
        assertThat(Util.normalize(0)).isEqualTo(0.00);
        assertThat(Util.normalize(1.234)).isEqualTo(1.23);
        assertThat(Util.normalize(1.235)).isEqualTo(1.24);
        assertThat(Util.normalize(1)).isEqualTo(1.00);
    }


}