package com.github.bytemania.adapter.out.web.client.impl;

import com.github.bytemania.adapter.out.web.client.dto.Listing;
import com.github.bytemania.cryptobalance.domain.Crypto;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Mapper {

    public static List<Crypto> fromListing(Listing listing) {
        return listing.getData().stream()
                .map(cryptoCurrency -> {
                    String symbol = cryptoCurrency.getSymbol();
                    double marketCapPercentage = BigDecimal
                            .valueOf(cryptoCurrency.getQuote().getQuoteDetail().getMarketCapDominance())
                            .setScale(2, RoundingMode.HALF_UP)
                            .doubleValue();
                    boolean stableCoin = cryptoCurrency.getTags().contains("stablecoin");

                    return Crypto.of(symbol, marketCapPercentage, stableCoin);
                })
                .collect(Collectors.toList());
    }
}
