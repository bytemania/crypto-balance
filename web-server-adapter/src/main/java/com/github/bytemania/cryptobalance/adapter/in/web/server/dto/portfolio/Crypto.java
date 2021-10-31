package com.github.bytemania.cryptobalance.adapter.in.web.server.dto.portfolio;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.github.bytemania.cryptobalance.adapter.in.web.server.impl.MoneySerializer;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.experimental.NonFinal;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import java.util.Comparator;

@Value
@NonFinal
@RequiredArgsConstructor
@Jacksonized
@SuperBuilder
public class Crypto implements Comparable<Crypto> {
    String symbol;

    double holding;

    @JsonSerialize(using = MoneySerializer.class)
    Double amountInvested;

    @Override
    public int compareTo(Crypto that) {
        return Comparator
                .comparing(Crypto::getAmountInvested).reversed()
                .thenComparing(Crypto::getHolding)
                .thenComparing(Crypto::getSymbol)
                .compare(this, that);
    }
}
