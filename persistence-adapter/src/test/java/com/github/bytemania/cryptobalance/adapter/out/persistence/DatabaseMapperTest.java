package com.github.bytemania.cryptobalance.adapter.out.persistence;

import com.github.bytemania.cryptobalance.adapter.out.persistence.dto.CryptoRow;
import com.github.bytemania.cryptobalance.domain.dto.CryptoState;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;


class DatabaseMapperTest {

    @Test
    @DisplayName("Should Translate Fom CryptoRow to CryptoState")
    void shouldTranslateFromCryptoRowToCryptoState() {
        assertThat(DatabaseMapper.INSTANCE.cryptoRowToCryptoState(new CryptoRow("BUSD", BigDecimal.ONE, BigDecimal.TEN)))
                .isEqualTo(new CryptoState("BUSD", BigDecimal.ONE, BigDecimal.TEN));

        assertThat(DatabaseMapper.INSTANCE.cryptoRowToCryptoState(null)).isNull();
    }

    @Test
    @DisplayName("Should Translate Fom CryptoState to CryptoRow")
    void shouldTranslateFromCryptoStateToCryptoState() {
        assertThat(DatabaseMapper.INSTANCE.cryptoStateToCryptoRow(new CryptoState ("BUSD", BigDecimal.ONE, BigDecimal.TEN)))
                .isEqualTo(new CryptoRow("BUSD", BigDecimal.ONE, BigDecimal.TEN));

        assertThat(DatabaseMapper.INSTANCE.cryptoStateToCryptoRow(null)).isNull();
    }


}
