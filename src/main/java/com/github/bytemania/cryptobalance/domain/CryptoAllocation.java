package com.github.bytemania.cryptobalance.domain;

import lombok.Value;

import java.math.BigDecimal;
import java.util.Comparator;

@Value(staticConstructor = "of")
public class CryptoAllocation implements Comparable<CryptoAllocation> {

    String symbol;
    double marketCapPercentage;
    BigDecimal amountToInvest;

    @Override
    public int compareTo(CryptoAllocation that) {
        return Comparator
                .comparing(CryptoAllocation::getAmountToInvest).reversed()
                .thenComparing(CryptoAllocation::getSymbol)
                .compare(this, that);
    }
}
