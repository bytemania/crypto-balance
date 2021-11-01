package com.github.bytemania.cryptobalance.adapter.in.web.server.dto.allocation;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.github.bytemania.cryptobalance.adapter.in.web.server.impl.MoneySerializer;
import lombok.EqualsAndHashCode;
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
public class Allocation implements Comparable<Allocation> {

    String symbol;

    @EqualsAndHashCode.Exclude
    @JsonSerialize(using = MoneySerializer.class)
    Double price;

    @EqualsAndHashCode.Exclude
    double holding;

    @EqualsAndHashCode.Exclude
    @JsonSerialize(using = MoneySerializer.class)
    Double invested;

    double marketCapPercentage;

    @EqualsAndHashCode.Exclude
    @JsonSerialize(using = MoneySerializer.class)
    Double rebalance;

    @Override
    public int compareTo(Allocation that) {
        return Comparator
                .comparing(Allocation::getMarketCapPercentage).reversed()
                .thenComparing(Allocation::getSymbol)
                .compare(this, that);
    }

}
