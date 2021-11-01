package com.github.bytemania.cryptobalance.adapter.in.web.server.dto.allocation;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.github.bytemania.cryptobalance.adapter.in.web.server.impl.MoneySerializer;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.experimental.NonFinal;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import java.util.SortedSet;

@Value
@NonFinal
@RequiredArgsConstructor
@Jacksonized
@SuperBuilder
public class Result {
    String currency;

    @JsonSerialize(using = MoneySerializer.class)
    Double amountToInvest;

    double holdings;

    @JsonSerialize(using = MoneySerializer.class)
    Double totalInvested;

    @JsonSerialize(using = MoneySerializer.class)
    Double rest;

    @JsonSerialize(using = MoneySerializer.class)
    Double minValueToAllocate;

    Allocation stableCrypto;

    SortedSet<Allocation> cryptos;
}
