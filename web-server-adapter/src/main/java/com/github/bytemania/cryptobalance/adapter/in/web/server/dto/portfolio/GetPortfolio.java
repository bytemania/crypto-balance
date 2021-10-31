package com.github.bytemania.cryptobalance.adapter.in.web.server.dto.portfolio;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.github.bytemania.cryptobalance.adapter.in.web.server.impl.MoneySerializer;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.experimental.NonFinal;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

@Value
@NonFinal
@RequiredArgsConstructor
@Jacksonized
@SuperBuilder
public class GetPortfolio {
    String currency;

    @JsonSerialize(using = MoneySerializer.class)
    Double totalAmountInvested;

    List<Crypto> cryptos;
}
