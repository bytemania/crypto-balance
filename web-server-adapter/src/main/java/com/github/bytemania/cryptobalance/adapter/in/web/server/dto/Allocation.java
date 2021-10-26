package com.github.bytemania.cryptobalance.adapter.in.web.server.dto;

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
    double marketCapPercentage;

    double amountToInvest;

    @EqualsAndHashCode.Exclude
    String operation;

    @EqualsAndHashCode.Exclude
    double rebalanceToInvest;

    @EqualsAndHashCode.Exclude
    double currentInvested;

    @Override
    public int compareTo(Allocation that) {
        return Comparator
                .comparing(Allocation::getAmountToInvest).reversed()
                .thenComparing(Allocation::getSymbol)
                .compare(this, that);
    }

}
