package com.github.bytemania.cryptobalance.domain.dto;

import com.github.bytemania.cryptobalance.domain.util.Util;
import lombok.Value;

import java.math.BigDecimal;
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
                    var v1 = Util.normalize(BigDecimal.valueOf(d1));
                    var v2 = Util.normalize(BigDecimal.valueOf(d2));
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

        var mcpThis = Util.normalize(BigDecimal.valueOf(this.marketCapPercentage)).doubleValue();
        var mcpThat = Util.normalize(BigDecimal.valueOf(that.marketCapPercentage)).doubleValue();

        return Double.compare(mcpThis, mcpThat) == 0
                && stableCoin == that.stableCoin
                && Objects.equals(symbol, that.symbol);
    }

    @Override
    public int hashCode() {
        var mcp = Util.normalize(BigDecimal.valueOf(marketCapPercentage)).doubleValue();

        return Objects.hash(symbol, mcp, stableCoin);
    }
}
