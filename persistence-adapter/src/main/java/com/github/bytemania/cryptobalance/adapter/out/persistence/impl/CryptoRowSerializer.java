package com.github.bytemania.cryptobalance.adapter.out.persistence.impl;

import com.github.bytemania.cryptobalance.adapter.out.persistence.dto.CryptoRow;
import org.jetbrains.annotations.NotNull;
import org.mapdb.DataInput2;
import org.mapdb.DataOutput2;
import org.mapdb.Serializer;

import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;

class CryptoRowSerializer implements Serializer<CryptoRow>, Serializable {

    @Override
    public void serialize(@NotNull DataOutput2 out, @NotNull CryptoRow value) throws IOException {
        out.writeUTF(value.getSymbol());
        out.writeDouble(value.getHolding().doubleValue());
        out.writeDouble(value.getInvested().doubleValue());
    }

    @Override
    public CryptoRow deserialize(@NotNull DataInput2 input, int available) throws IOException {
        String symbol = input.readUTF();
        BigDecimal holding = BigDecimal.valueOf(input.readDouble());
        BigDecimal invested = BigDecimal.valueOf(input.readDouble());

        return new CryptoRow(symbol, holding, invested);
    }
}
