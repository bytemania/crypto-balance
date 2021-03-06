package com.github.bytemania.adapter.in.web.server.dto;

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
public class Response {
    String currency;
    double amountToInvest;
    double rest;
    double amountInvested;
    Allocation stableCrypto;
    List<Allocation> cryptos;
}
