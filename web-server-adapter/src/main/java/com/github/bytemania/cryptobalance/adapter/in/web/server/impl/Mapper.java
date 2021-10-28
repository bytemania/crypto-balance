package com.github.bytemania.cryptobalance.adapter.in.web.server.impl;

import com.github.bytemania.cryptobalance.adapter.in.web.server.dto.allocation.Allocation;
import com.github.bytemania.cryptobalance.adapter.in.web.server.dto.allocation.Response;
import com.github.bytemania.cryptobalance.adapter.in.web.server.dto.portfolio.Crypto;
import com.github.bytemania.cryptobalance.adapter.in.web.server.dto.portfolio.GetPortfolio;
import com.github.bytemania.cryptobalance.domain.dto.AllocationResult;
import com.github.bytemania.cryptobalance.domain.dto.CryptoAllocation;
import com.github.bytemania.cryptobalance.domain.dto.CryptoState;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Mapper {

    public static Response fromAllocationResult(String currency, double valueToInvest, AllocationResult allocationResult) {
        double totalAmount = fromBigDecimal(allocationResult.getAmountToInvest());

        double rest = fromBigDecimal(allocationResult.getRest());

        double amountInvested = fromDouble(totalAmount - (valueToInvest - rest));

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
                .amountToInvest(valueToInvest)
                .rest(rest)
                .amountInvested(amountInvested)
                .stableCrypto(stableCrypto)
                .cryptos(cryptos)
                .build();
    }

    public static GetPortfolio fromCryptoStateList(String currency, List<CryptoState> cryptoStateList) {

        var total = fromDouble(cryptoStateList.stream()
                .map(CryptoState::getInvested)
                .reduce(BigDecimal::add).orElse(BigDecimal.ZERO).doubleValue());

        List<Crypto> cryptos = cryptoStateList.stream()
                .map(cryptoState -> Crypto.builder()
                        .symbol(cryptoState.getSymbol())
                        .amountInvested(fromDouble(cryptoState.getInvested().doubleValue()))
                        .build())
                .collect(Collectors.toList());

        return GetPortfolio.builder()
                .currency(currency)
                .totalAmountInvested(total)
                .cryptos(cryptos)
                .build();
    }

    public static CryptoState fromCrypto(Crypto crypto) {
        return CryptoState.of(
                crypto.getSymbol(),
                BigDecimal.valueOf(crypto.getAmountInvested()).setScale(2, RoundingMode.HALF_UP));
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
