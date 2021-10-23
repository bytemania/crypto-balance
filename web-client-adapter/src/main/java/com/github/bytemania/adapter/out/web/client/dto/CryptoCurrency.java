package com.github.bytemania.adapter.out.web.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
@JsonIgnoreProperties(ignoreUnknown = true)
public class CryptoCurrency {
    String symbol;
    List<String> tags;
    Quote quote;
}
