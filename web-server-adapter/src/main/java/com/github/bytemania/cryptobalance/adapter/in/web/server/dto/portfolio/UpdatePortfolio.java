package com.github.bytemania.cryptobalance.adapter.in.web.server.dto.portfolio;

import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.experimental.NonFinal;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import java.util.Set;

@Value
@NonFinal
@RequiredArgsConstructor
@Jacksonized
@SuperBuilder
public class UpdatePortfolio {
    String currency;
    Set<Crypto> cryptosToUpdate;
    Set<String> cryptosToRemove;
}
