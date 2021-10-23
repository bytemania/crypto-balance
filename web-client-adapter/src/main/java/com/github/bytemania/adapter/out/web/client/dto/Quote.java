package com.github.bytemania.adapter.out.web.client.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.experimental.NonFinal;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

@Value
@NonFinal
@RequiredArgsConstructor
@Jacksonized @SuperBuilder
public class Quote {
    @JsonProperty("USD")
    @JsonAlias("EUR")
    QuoteDetail quoteDetail;
}
