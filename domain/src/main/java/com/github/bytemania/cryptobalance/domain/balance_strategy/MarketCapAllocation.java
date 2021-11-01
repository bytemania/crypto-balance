package com.github.bytemania.cryptobalance.domain.balance_strategy;

import com.github.bytemania.cryptobalance.domain.BalanceStrategy;
import com.github.bytemania.cryptobalance.domain.dto.AllocationResult;
import com.github.bytemania.cryptobalance.domain.dto.Crypto;
import com.github.bytemania.cryptobalance.domain.dto.CryptoAllocation;
import com.github.bytemania.cryptobalance.domain.dto.CryptoState;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@EqualsAndHashCode
@ToString
public class MarketCapAllocation implements BalanceStrategy {

    private static BigDecimal calculateAmountToInvest(BigDecimal rest,
                                                      BigDecimal minValueToInvest,
                                                      double marketCapPercentage,
                                                      BigDecimal totalToRebalance) {
        if (marketCapPercentage == 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal valueToInvest = totalToRebalance.multiply(BigDecimal.valueOf(marketCapPercentage / 100));

        if (valueToInvest.compareTo(rest) > 0 && rest.compareTo(minValueToInvest) > 0) {
            return rest;
        }

        if (valueToInvest.compareTo(rest) > 0 && rest.compareTo(minValueToInvest) < 0) {
            return BigDecimal.ZERO;
        }

        if (valueToInvest.compareTo(minValueToInvest) < 0) {
            return minValueToInvest;
        }

        return valueToInvest;
    }

    private final AllocationResult allocationResult;

    public MarketCapAllocation(Set<Crypto> cryptos,
                               Crypto stableCoin,
                               BigDecimal valueToAllocate,
                               BigDecimal minValueToAllocate,
                               Set<CryptoState> currentAllocation) {

        Map<String, Crypto> indexedCrypto = cryptos.stream()
                .collect(Collectors.toMap(Crypto::getSymbol, Function.identity()));

        Map<String, CryptoState> indexedAllocation = currentAllocation.stream()
                .collect(Collectors.toMap(CryptoState::getSymbol, Function.identity()));

        BigDecimal holdings = currentAllocation.stream()
                .map(cryptoState -> {
                    if (indexedCrypto.containsKey(cryptoState.getSymbol()) && !cryptoState.getHolding().equals(BigDecimal.ZERO)) {
                        var price = indexedCrypto.get(cryptoState.getSymbol()).getPrice();
                        return cryptoState.getHolding().multiply(price);
                    } else {
                        log.warn("Could not find price for coin {}. Please increment WEB_CLIENT_NUMBER_OF_CRYPTOS property",
                                cryptoState.getSymbol());
                        return cryptoState.getInvested();
                    }
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalInvested = currentAllocation.stream()
                .map(CryptoState::getInvested)
                .reduce(BigDecimal.ZERO, BigDecimal::add);


        BigDecimal totalToRebalance = holdings.add(valueToAllocate);
        BigDecimal rest = totalToRebalance;

        Optional<Crypto> stableCrypto = Optional.ofNullable(indexedCrypto.get(stableCoin.getSymbol()));
        Optional<CryptoState> stableCryptoState = Optional.ofNullable(indexedAllocation.get(stableCoin.getSymbol()));

        String stableCryptoSymbol = stableCoin.getSymbol();
        BigDecimal stableCryptoPrice = stableCrypto.map(Crypto::getPrice).orElse(BigDecimal.ONE);
        BigDecimal stableCryptoHolding = stableCryptoState.map(CryptoState::getHolding).orElse(BigDecimal.ZERO);
        BigDecimal stableCryptoInvested = stableCryptoState.map(CryptoState::getInvested).orElse(BigDecimal.ZERO);
        double stableCryptoMarketPercentage = stableCoin.getMarketCapPercentage();

        if (rest.compareTo(minValueToAllocate) >= 0) {
            BigDecimal stableCoinValueToInvest = calculateAmountToInvest(
                    rest,
                    minValueToAllocate,
                    stableCryptoMarketPercentage,
                    totalToRebalance);

            BigDecimal stableCryptoRebalance = stableCoinValueToInvest
                    .subtract(stableCryptoHolding.multiply(stableCryptoPrice));

            rest = rest.subtract(stableCoinValueToInvest);

            CryptoAllocation stableCoinAllocation = new CryptoAllocation(
                    stableCryptoSymbol,
                    stableCryptoPrice,
                    stableCryptoHolding,
                    stableCryptoInvested,
                    stableCryptoMarketPercentage,
                    stableCryptoRebalance);

            int i = 0;
            List<Crypto> cryptoList = cryptos.stream()
                    .filter(crypto -> !crypto.isStableCoin())
                    .sorted(Comparator.comparing(Crypto::getMarketCapPercentage).reversed())
                    .collect(Collectors.toList());
            Set<CryptoAllocation> allocations = new HashSet<>();

            while (rest.compareTo(minValueToAllocate) >= 0 && i < cryptoList.size()) {
                Crypto crypto = cryptoList.get(i);

                String symbol = crypto.getSymbol();
                BigDecimal price = crypto.getPrice();
                BigDecimal holding = indexedAllocation.containsKey(symbol) ? indexedAllocation.get(symbol).getHolding()
                        : BigDecimal.ZERO;
                BigDecimal invested = indexedAllocation.containsKey(symbol) ? indexedAllocation.get(symbol).getInvested()
                        : BigDecimal.ZERO;
                double marketCapPercentage = crypto.getMarketCapPercentage();
                BigDecimal newInvestment = calculateAmountToInvest(rest, minValueToAllocate, marketCapPercentage,
                        totalToRebalance);
                BigDecimal rebalance = newInvestment.subtract(holding.multiply(price));

                CryptoAllocation allocation = new CryptoAllocation(symbol, price, holding, invested,
                        marketCapPercentage, rebalance);

                allocations.add(allocation);

                rest = rest.subtract(newInvestment);
                i++;
            }

            Set<String> allocatedSymbols = allocations.stream()
                    .map(CryptoAllocation::getSymbol)
                    .collect(Collectors.toSet());

            for(String stateSymbol: indexedAllocation.keySet()) {
                if (!stateSymbol.equals(stableCryptoSymbol) && !allocatedSymbols.contains(stateSymbol)) {
                    BigDecimal price = indexedCrypto.containsKey(stateSymbol) ? indexedCrypto.get(stateSymbol).getPrice()
                            : BigDecimal.ZERO;
                    BigDecimal holding = indexedAllocation.get(stateSymbol).getHolding();
                    BigDecimal invested = indexedAllocation.get(stateSymbol).getInvested();
                    double marketCapPercentage = 0.0;
                    BigDecimal rebalance = BigDecimal.ZERO.subtract(invested);
                    CryptoAllocation allocation = new CryptoAllocation(stateSymbol, price, holding, invested,
                            marketCapPercentage, rebalance);
                    allocations.add(allocation);
                }
            }

            allocationResult = new AllocationResult(
                    valueToAllocate,
                    holdings,
                    totalInvested,
                    rest,
                    minValueToAllocate,
                    stableCoinAllocation,
                    allocations);
        } else {
            CryptoAllocation stableCoinAllocation = new CryptoAllocation(
                    stableCryptoSymbol,
                    stableCryptoPrice,
                    stableCryptoHolding,
                    stableCryptoInvested,
                    stableCryptoMarketPercentage,
                    BigDecimal.ZERO);

            allocationResult = new AllocationResult(
                    valueToAllocate,
                    holdings,
                    totalInvested,
                    rest,
                    minValueToAllocate,
                    stableCoinAllocation,
                    Set.of());
        }
    }

    @Override
    public AllocationResult allocate() {
        return allocationResult;
    }
}
