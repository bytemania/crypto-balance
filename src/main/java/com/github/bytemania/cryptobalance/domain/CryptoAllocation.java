package com.github.bytemania.cryptobalance.domain;

import lombok.Value;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.Objects;

@Value(staticConstructor = "of")
public class CryptoAllocation implements Comparable<CryptoAllocation> {

    String symbol;
    double marketCapPercentage;
    boolean stableCoin;
    BigDecimal amountToInvest;

    @Override
    public int compareTo(CryptoAllocation that) {
        return Comparator.comparing(CryptoAllocation::getAmountToInvest, (b1,b2) -> {
                    var v1 = b1.setScale(2, RoundingMode.HALF_UP);
                    var v2 = b2.setScale(2, RoundingMode.HALF_UP);
                    return v1.compareTo(v2);
                }).reversed()
                .thenComparing(CryptoAllocation::getMarketCapPercentage, (d1, d2) -> {
                    var v1 = BigDecimal.valueOf(d1).setScale(2, RoundingMode.HALF_UP);
                    var v2 = BigDecimal.valueOf(d2).setScale(2, RoundingMode.HALF_UP);
                    return v1.compareTo(v2);
                })
                .thenComparing(CryptoAllocation::getSymbol)
                .thenComparing(CryptoAllocation::isStableCoin)
                .compare(this, that);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CryptoAllocation that = (CryptoAllocation) o;

        var mcpThis = BigDecimal.valueOf(this.marketCapPercentage)
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();
        var mcpThat = BigDecimal.valueOf(that.marketCapPercentage)
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();

        var atiThis = this.amountToInvest.setScale(2, RoundingMode.HALF_UP);
        var atiThat = that.amountToInvest.setScale(2, RoundingMode.HALF_UP);

        return Double.compare(mcpThis, mcpThat) == 0
                && stableCoin == that.stableCoin
                && Objects.equals(symbol, that.symbol)
                && atiThis.compareTo(atiThat) == 0;
    }

    @Override
    public int hashCode() {
        var mcp = BigDecimal.valueOf(marketCapPercentage)
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();
        var ati = amountToInvest.setScale(2, RoundingMode.HALF_UP);

        return Objects.hash(symbol, mcp, stableCoin, ati);
    }
}
