package com.github.bytemania.cryptobalance.domain.dto;

import com.github.bytemania.cryptobalance.domain.util.Util;
import lombok.Value;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.Objects;

@Value(staticConstructor = "of")
public class CryptoAllocation implements Comparable<CryptoAllocation> {

    public enum Operation {
        BUY,
        SELL,
        KEEP
    }

    String symbol;
    double marketCapPercentage;
    boolean stableCoin;
    BigDecimal amountToInvest;
    Operation rebalanceOperation;
    BigDecimal rebalanceInvestment;
    BigDecimal currentInvested;


    @Override
    public int compareTo(CryptoAllocation that) {
        return Comparator.comparing(CryptoAllocation::getAmountToInvest, (b1,b2) -> {
                    var v1 = Util.normalize(b1);
                    var v2 = Util.normalize(b2);
                    return v1.compareTo(v2);
                }).reversed()
                .thenComparing(CryptoAllocation::getMarketCapPercentage, (d1, d2) -> {
                    var v1 = Util.normalize(BigDecimal.valueOf(d1));
                    var v2 = Util.normalize(BigDecimal.valueOf(d2));
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

        var mcpThis = Util.normalize(BigDecimal.valueOf(this.marketCapPercentage)).doubleValue();
        var mcpThat = Util.normalize(BigDecimal.valueOf(that.marketCapPercentage)).doubleValue();

        var atiThis = Util.normalize(this.amountToInvest);
        var atiThat = Util.normalize(that.amountToInvest);

        return Double.compare(mcpThis, mcpThat) == 0
                && stableCoin == that.stableCoin
                && Objects.equals(symbol, that.symbol)
                && atiThis.compareTo(atiThat) == 0;
    }

    @Override
    public int hashCode() {
        var mcp = Util.normalize(BigDecimal.valueOf(marketCapPercentage)).doubleValue();
        var ati = Util.normalize(amountToInvest);

        return Objects.hash(symbol, mcp, stableCoin, ati);
    }
}
