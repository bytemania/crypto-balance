package com.github.bytemania.cryptobalance.adapter.in.web.server.impl;

import com.github.bytemania.cryptobalance.adapter.in.web.server.dto.Allocation;
import com.github.bytemania.cryptobalance.adapter.in.web.server.dto.Response;
import com.github.bytemania.cryptobalance.domain.dto.AllocationResult;
import com.github.bytemania.cryptobalance.domain.dto.CryptoAllocation;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Mapper {

    public static Response fromAllocationResult(String currency, AllocationResult allocationResult) {
        double amountToInvest = fromBigDecimal(allocationResult.getAmountToInvest());

        double rest = fromBigDecimal(allocationResult.getRest());

        double totalAmount = allocationResult.getCryptos().stream()
                .map(CryptoAllocation::getAmountToInvest)
                .reduce(BigDecimal::add)
                .map(Mapper::fromBigDecimal)
                .orElse(0.00);

        double amountInvested = fromDouble(totalAmount - (amountToInvest - rest));

        Allocation stableCrypto = allocationResult.getCryptos().stream()
                .filter(CryptoAllocation::isStableCoin)
                .map(Mapper::fromCryptoAllocation)
                .findFirst()
                .orElse(Allocation.builder().build());

        List<Allocation> cryptos = allocationResult.getCryptos().stream()
                .filter(cryptoAllocation -> !cryptoAllocation.isStableCoin())
                .map(Mapper::fromCryptoAllocation)
                .sorted()
                .collect(Collectors.toList());

        return Response.builder()
                .currency(currency)
                .amountToInvest(amountToInvest)
                .rest(rest)
                .amountInvested(amountInvested)
                .stableCrypto(stableCrypto)
                .cryptos(cryptos)
                .build();
    }

    private static Allocation fromCryptoAllocation(CryptoAllocation cryptoAllocation) {
        return Allocation
                .builder()
                .symbol(cryptoAllocation.getSymbol())
                .marketCapPercentage(fromDouble(cryptoAllocation.getMarketCapPercentage()))
                .amountToInvest(fromBigDecimal(cryptoAllocation.getAmountToInvest()))
                .operation(cryptoAllocation.getRebalanceOperation().toString())
                .rebalanceToInvest(fromBigDecimal(cryptoAllocation.getRebalanceInvestment()))
                .currentInvested(fromBigDecimal(cryptoAllocation.getCurrentInvested()))
                .build();
    }

    private static double fromDouble(double d) {
        return BigDecimal.valueOf(d).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    private static double fromBigDecimal(BigDecimal bigDecimal) {
        return bigDecimal.setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

}
