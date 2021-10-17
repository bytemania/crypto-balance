package com.github.bytemania.cryptobalance.domain.balance_strategy;

import com.github.bytemania.cryptobalance.domain.BalanceStrategy;
import com.github.bytemania.cryptobalance.domain.Crypto;
import com.github.bytemania.cryptobalance.domain.CryptoAllocation;
import com.github.bytemania.cryptobalance.domain.CryptoState;
import com.github.bytemania.cryptobalance.domain.util.Util;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@EqualsAndHashCode
@ToString
public class MarketCapAllocation implements BalanceStrategy {

    public static MarketCapAllocation of(List<Crypto> cryptos,
                                         Crypto stableCoin,
                                         BigDecimal valueToAllocate,
                                         BigDecimal minValueToAllocate,
                                         List<CryptoState> currentState) {
        return new MarketCapAllocation(cryptos, stableCoin, valueToAllocate, minValueToAllocate, currentState);
    }

    private static BigDecimal calculateTotalInvested(List<CryptoState> allocation) {
        return allocation.stream()
                .map(CryptoState::getInvested)
                .reduce(BigDecimal::add)
                .orElse(BigDecimal.ZERO);
    }

    private final BigDecimal amountToInvest;
    private final Map<String, BigDecimal> currentState;
    @Getter
    private BigDecimal rest;
    private final List<CryptoAllocation> allocation;

    public MarketCapAllocation(List<Crypto> cryptos,
                               Crypto stableCoin,
                               BigDecimal valueToAllocate,
                               BigDecimal minValueToAllocate,
                               List<CryptoState> currentAllocation) {
        this.currentState = currentAllocation.stream()
                .collect(Collectors.toMap(CryptoState::getSymbol, CryptoState::getInvested));
        allocation = new ArrayList<>();
        rest = valueToAllocate.add(MarketCapAllocation.calculateTotalInvested(currentAllocation));
        amountToInvest = rest;
        allocateStableCoin(stableCoin, minValueToAllocate);
        allocateNonStableCoin(cryptos, minValueToAllocate);
        allocateNonUsedCoinsToSell();
        Collections.sort(allocation);
    }

    @Override
    public List<CryptoAllocation> allocate() {
        return allocation;
    }

    private void allocateStableCoin(Crypto stableCoin, BigDecimal minValueToAllocate) {
        BigDecimal valueInvested = calculateValueToInvest(minValueToAllocate,
                stableCoin.getMarketCapPercentage());

        double percentageInvested = allocationPercentage(valueInvested);
        var allocatedCrypto = createAllocation(
                stableCoin.getSymbol(),
                percentageInvested,
                true,
                valueInvested);
        allocation.add(allocatedCrypto);
    }

    private void allocateNonStableCoin(List<Crypto> cryptos, BigDecimal minValueToAllocate) {
        List<Crypto> sortedCryptos = cryptos.stream().sorted().collect(Collectors.toList());
        Iterator<Crypto> it = sortedCryptos.iterator();
        while (rest.compareTo(BigDecimal.ZERO) > 0 && it.hasNext()) {
            Crypto crypto = it.next();
            if (!crypto.isStableCoin()) {
                BigDecimal valueInvested = calculateValueToInvest(minValueToAllocate, crypto.getMarketCapPercentage());
                double percentageInvested = allocationPercentage(valueInvested);
                var allocatedCrypto = createAllocation(
                        crypto.getSymbol(),
                        percentageInvested,
                        crypto.isStableCoin(),
                        valueInvested);
                allocation.add(allocatedCrypto);
            }
        }
    }

    private void allocateNonUsedCoinsToSell() {
        Set<String> cryptosUsed = allocation.stream().map(CryptoAllocation::getSymbol).collect(Collectors.toSet());
        for (var symbol : currentState.keySet()) {
            if (!cryptosUsed.contains(symbol)) {
                var sellAllocation =  CryptoAllocation.of(
                        symbol,
                        0,
                        false,
                        BigDecimal.ZERO,
                        CryptoAllocation.Operation.SELL,
                        currentState.get(symbol),
                        currentState.get(symbol)
                );
                allocation.add(sellAllocation);
            }
        }
    }

    private BigDecimal calculateValueToInvest(BigDecimal minValueToAllocate, double percentageToAllocate) {

        var amountToAllocate = amountToInvest.multiply(BigDecimal.valueOf(percentageToAllocate / 100));

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

    private double allocationPercentage(BigDecimal valueInvested) {
        return valueInvested.divide(amountToInvest, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100)).doubleValue();
    }

    private CryptoAllocation createAllocation(
            String symbol,
            double percentageInvested,
            boolean isStableCoin,
            BigDecimal valueToInvest) {

        var roundedCurrentAmount = Util.normalize(currentState.getOrDefault(symbol, BigDecimal.ZERO));
        var roundedValueToInvest = Util.normalize(valueToInvest);

        CryptoAllocation.Operation operation;

        if (roundedCurrentAmount.compareTo(roundedValueToInvest) == 0 ) {
            operation = CryptoAllocation.Operation.KEEP;
        } else if (roundedCurrentAmount.compareTo(roundedValueToInvest) < 0) {
            operation = CryptoAllocation.Operation.BUY;
        } else {
            operation = CryptoAllocation.Operation.SELL;
        }

        var rebalanceAmount = roundedValueToInvest.subtract(roundedCurrentAmount).abs();

        return CryptoAllocation.of(
                symbol,
                percentageInvested,
                isStableCoin,
                roundedValueToInvest,
                operation,
                rebalanceAmount,
                roundedCurrentAmount
        );
    }
}
