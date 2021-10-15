package com.github.bytemania.cryptobalance.domain.balance_strategy;

import com.github.bytemania.cryptobalance.domain.BalanceStrategy;
import com.github.bytemania.cryptobalance.domain.Crypto;
import com.github.bytemania.cryptobalance.domain.CryptoAllocation;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

@EqualsAndHashCode
@ToString
public class MarketCapAllocation implements BalanceStrategy {

    //TODO AS Add the CurrentOperation State and do the merge with BUY/SELL/NO Operation
    public static MarketCapAllocation of(List<Crypto> cryptos,
                                          Crypto stableCoin,
                                          BigDecimal valueToAllocate,
                                          BigDecimal minValueToAllocate) {
        return new MarketCapAllocation(cryptos, stableCoin, valueToAllocate, minValueToAllocate);
    }

    @Getter
    private BigDecimal rest;
    private final List<CryptoAllocation> allocation;

    public MarketCapAllocation(List<Crypto> cryptos,
                               Crypto stableCoin,
                               BigDecimal valueToAllocate,
                               BigDecimal minValueToAllocate) {
        allocation = new ArrayList<>();
        rest = new BigDecimal(valueToAllocate.toString());
        allocateStableCoin(stableCoin, valueToAllocate, minValueToAllocate);
        allocateNonStableCoin(cryptos, valueToAllocate, minValueToAllocate);
        Collections.sort(allocation);
    }

    @Override
    public List<CryptoAllocation> allocate() {
        return allocation;
    }

    private void allocateStableCoin(Crypto stableCoin,
                                    BigDecimal valueToAllocate,
                                    BigDecimal minValueToAllocate) {
        BigDecimal valueInvested = calculateValueToInvest(valueToAllocate, minValueToAllocate, stableCoin.getMarketCapPercentage());

        double percentageInvested = allocationPercentage(valueToAllocate, valueInvested);
        allocation.add(
                CryptoAllocation.of(
                        stableCoin.getSymbol(),
                        percentageInvested,
                        true,
                        valueInvested));
    }

    private void allocateNonStableCoin(List<Crypto> cryptos,
                                       BigDecimal valueToAllocate,
                                       BigDecimal minValueToAllocate) {
        List<Crypto> sortedCryptos = cryptos.stream().sorted().collect(Collectors.toList());
        Iterator<Crypto> it = sortedCryptos.iterator();
        while (rest.compareTo(BigDecimal.ZERO) > 0 && it.hasNext()) {
            Crypto crypto = it.next();
            if (!crypto.isStableCoin()) {
                BigDecimal valueInvested = calculateValueToInvest(valueToAllocate, minValueToAllocate, crypto.getMarketCapPercentage());
                double percentageInvested = allocationPercentage(valueToAllocate, valueInvested);
                allocation.add(CryptoAllocation.of(
                        crypto.getSymbol(),
                        percentageInvested,
                        crypto.isStableCoin(),
                        valueInvested));
            }
        }
    }

    private BigDecimal calculateValueToInvest(BigDecimal valueToAllocate, BigDecimal minValueToAllocate, double percentageToAllocate) {

        var amountToAllocate = valueToAllocate.multiply(BigDecimal.valueOf(percentageToAllocate / 100));

        if (amountToAllocate.compareTo(minValueToAllocate) < 0) {
           amountToAllocate = minValueToAllocate;
        }

        BigDecimal valueToInvest;
        if (rest.compareTo(amountToAllocate) > 0) {
            valueToInvest = amountToAllocate;
            rest = rest.subtract(amountToAllocate);
        } else {
            valueToInvest = rest;
            rest = BigDecimal.ZERO;
        }

        return valueToInvest;
    }

    private double allocationPercentage(BigDecimal valueToAllocate, BigDecimal valueInvested) {
        return valueInvested.divide(valueToAllocate, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100)).doubleValue();
    }
}
