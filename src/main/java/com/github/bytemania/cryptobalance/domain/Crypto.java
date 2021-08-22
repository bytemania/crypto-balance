package com.github.bytemania.cryptobalance.domain;

import lombok.Value;

import java.util.Comparator;

@Value(staticConstructor = "of")
public class Crypto implements Comparable<Crypto>{
    String symbol;
    double marketCapPercentage;

    @Override
    public int compareTo(Crypto that) {
        return Comparator
                .comparing(Crypto::getMarketCapPercentage).reversed()
                .thenComparing(Crypto::getSymbol)
                .compare(this, that);
    }
}
