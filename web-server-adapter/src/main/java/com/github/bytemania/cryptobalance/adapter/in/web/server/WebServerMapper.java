package com.github.bytemania.cryptobalance.adapter.in.web.server;

import com.github.bytemania.cryptobalance.adapter.in.web.server.dto.allocation.Allocation;
import com.github.bytemania.cryptobalance.adapter.in.web.server.dto.allocation.Result;
import com.github.bytemania.cryptobalance.adapter.in.web.server.dto.portfolio.Crypto;
import com.github.bytemania.cryptobalance.adapter.in.web.server.dto.portfolio.GetPortfolio;
import com.github.bytemania.cryptobalance.domain.dto.AllocationResult;
import com.github.bytemania.cryptobalance.domain.dto.CryptoAllocation;
import com.github.bytemania.cryptobalance.domain.dto.CryptoState;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper
abstract class WebServerMapper {

    static final WebServerMapper INSTANCE = Mappers.getMapper(WebServerMapper.class);


    public GetPortfolio createGetPorfolio(String currency, Set<CryptoState> cryptoStateList) {

        var total = cryptoStateList.stream()
                        .map(CryptoState::getInvested)
                        .reduce(BigDecimal::add)
                        .orElse(BigDecimal.ZERO).doubleValue();

        List<Crypto> cryptos = cryptoStateList.stream()
                .map(this::cryptoStateToCrypto)
                .sorted()
                .collect(Collectors.toList());

        return GetPortfolio.builder()
                .currency(currency)
                .totalAmountInvested(total)
                .cryptos(cryptos)
                .build();
    }

    @Mapping(source = "invested", target = "amountInvested")
    abstract Crypto cryptoStateToCrypto(CryptoState cryptoState);

    @Mapping(source = "amountInvested", target = "invested")
    abstract CryptoState cryptoToCryptoState(Crypto crypto);

    abstract Allocation cryptoAllocationToAllocation(CryptoAllocation cryptoAllocation);

    @Mapping(source = "currency", target = "currency")
    @Mapping(source = "result.amountToInvest", target = "amountToInvest")
    @Mapping(source = "result.holdings", target = "holdings")
    @Mapping(source = "result.totalInvested", target = "totalInvested")
    @Mapping(source = "result.rest", target = "rest")
    @Mapping(source = "result.minValueToAllocate", target = "minValueToAllocate")
    @Mapping(source = "result.stableCrypto", target = "stableCrypto")
    @Mapping(source = "result.cryptos", target = "cryptos")
    abstract Result allocationResultToResult(String currency, AllocationResult result);

}
