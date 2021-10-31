package com.github.bytemania.cryptobalance.adapter.out.persistence.impl;

import com.github.bytemania.cryptobalance.adapter.out.persistence.dto.CryptoRow;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapdb.DataInput2;
import org.mapdb.DataOutput2;

import java.io.IOException;
import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class CryptoRowSerializerTest {

    private static final CryptoRow CRYPTO_ROW = new CryptoRow(
            "ETH",
            BigDecimal.valueOf(1.0),
            BigDecimal.valueOf(10.0));
    private static final CryptoRowSerializer cryptoRowSerializer = new CryptoRowSerializer();

    @Test
    @DisplayName("Should Serialize")
    void shouldSerialize() throws IOException {
        DataOutput2 expected = new DataOutput2();
        expected.writeUTF(CRYPTO_ROW.getSymbol());
        expected.writeDouble(CRYPTO_ROW.getHolding().doubleValue());
        expected.writeDouble(CRYPTO_ROW.getInvested().doubleValue());

        DataOutput2 dataOutput2 = new DataOutput2();
        cryptoRowSerializer.serialize(dataOutput2, CRYPTO_ROW);

        assertThat(dataOutput2.buf).isEqualTo(expected.buf);
        assertThat(dataOutput2.pos).isEqualTo(dataOutput2.pos);
    }

    @Test
    @DisplayName("Should Deserialize")
    void shouldDeserialize() throws IOException {

        DataOutput2 dataOutput2 = new DataOutput2();
        cryptoRowSerializer.serialize(dataOutput2, CRYPTO_ROW);

        DataInput2 dataInput2 = new DataInput2.ByteArray(dataOutput2.buf);
        CryptoRow cryptoRow = cryptoRowSerializer.deserialize(dataInput2, 0);

        assertThat(cryptoRow).isEqualTo(CRYPTO_ROW);
    }
}