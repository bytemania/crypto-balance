package com.github.bytemania.cryptobalance.adapter.out.persistence.impl;

import com.github.bytemania.cryptobalance.domain.dto.CryptoState;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;


class MapperTest {

    @Test
    @DisplayName("Should Translate from Database Format to the Internal Model")
    void shouldTranslateFromTheDatabaseToInternalModel() {
        assertThat(Mapper.fromEntry(Map.entry("BUSD", BigDecimal.ONE)))
                .isEqualTo(CryptoState.of("BUSD", BigDecimal.ONE));
    }
}
