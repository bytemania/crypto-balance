package com.github.bytemania.cryptobalance.adapter.out.web.client.dto;

import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.experimental.NonFinal;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

@Value
@NonFinal
@RequiredArgsConstructor
@Jacksonized @SuperBuilder
public class Listing {
    Status status;
    List<CryptoCurrency> data;
}
