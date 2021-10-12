package com.github.bytemania.cryptobalance.domain;

import lombok.Value;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.Objects;

@Value(staticConstructor = "of")
public class Crypto implements Comparable<Crypto>{
    String symbol;
    double marketCapPercentage;
    boolean stableCoin;

    @Override
    public int compareTo(Crypto that) {
        return Comparator
                .comparing(Crypto::getMarketCapPercentage, (d1, d2) -> {
                    var v1 = BigDecimal.valueOf(d1).setScale(2, RoundingMode.HALF_UP);
                    var v2 = BigDecimal.valueOf(d2).setScale(2, RoundingMode.HALF_UP);
                    return v1.compareTo(v2);
                }).reversed()
                .thenComparing(Crypto::getSymbol)
                .thenComparing(Crypto::isStableCoin)
                .compare(this, that);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Crypto that = (Crypto) o;

        var mcpThis = BigDecimal.valueOf(this.marketCapPercentage)
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();
        var mcpThat = BigDecimal.valueOf(that.marketCapPercentage)
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();

        return Double.compare(mcpThis, mcpThat) == 0
                && stableCoin == that.stableCoin
                && Objects.equals(symbol, that.symbol);
    }

    @Override
    public int hashCode() {
        var mcp = BigDecimal.valueOf(marketCapPercentage)
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();

        return Objects.hash(symbol, mcp, stableCoin);
    }
}
