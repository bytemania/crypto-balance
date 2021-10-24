package com.github.bytemania.adapter.out.web.client.impl;

import com.github.bytemania.adapter.out.web.client.dto.Listing;
import com.github.bytemania.cryptobalance.domain.dto.Crypto;
import com.github.bytemania.cryptobalance.domain.util.Util;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Mapper {

    public static List<Crypto> fromListing(Listing listing) {
        return listing.getData().stream()
                .map(cryptoCurrency -> {
                    String symbol = cryptoCurrency.getSymbol();
                    double marketCapPercentage = Util.normalize(cryptoCurrency.getQuote().getQuoteDetail()
                            .getMarketCapDominance());
                    boolean stableCoin = cryptoCurrency.getTags().contains("stablecoin");

                    return Crypto.of(symbol, marketCapPercentage, stableCoin);
                })
                .collect(Collectors.toList());
    }
}
