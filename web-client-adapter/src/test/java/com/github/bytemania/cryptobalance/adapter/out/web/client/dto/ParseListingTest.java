package com.github.bytemania.cryptobalance.adapter.out.web.client.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.bytemania.cryptobalance.adapter.out.web.client.Fixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class ParseListingTest {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Test
    @DisplayName("Should Parse The Top 10 CryptoCurrencies in USD")
    void shouldParseTheTop10CryptoCurrenciesInUSD() throws JsonProcessingException {
        String jsonString = Fixture.readFileResource("ok-usd-10.json");
        Listing listing = OBJECT_MAPPER.readValue(jsonString, Listing.class);
        assertThat(listing).isNotNull();
        assertThat(listing.getStatus().getTimestamp()).isEqualTo(Instant.parse("2021-10-21T20:00:11.716Z"));
        assertThat(listing.getStatus().getErrorCode()).isEqualTo(0);
        assertThat(listing.getStatus().getErrorMessage()).isNull();
        assertThat(listing.getStatus().getCreditCount()).isEqualTo(1);
        assertThat(listing.getData().stream().map(CryptoCurrency::getSymbol).collect(Collectors.toList()))
                .isEqualTo(List.of("BTC", "ETH", "BNB", "ADA", "USDT", "SOL", "XRP", "DOT", "USDC", "DOGE"));
        assertThat(listing.getData().stream().map(c -> c.getQuote().getQuoteDetail().getMarketCapDominance())
                .collect(Collectors.toList()))
                .isEqualTo(List.of(46.1577, 18.765, 3.0848, 2.7677, 2.7068, 2.1652, 2.0092, 1.6501, 1.2593, 1.255));
        assertThat(listing.getData().stream().filter(c -> c.getTags().contains("stablecoin")).count()).isEqualTo(2);
    }

    @Test
    @DisplayName("Should Parse The Top 10 CryptoCurrencies in EUR")
    void shouldParseTheTop10CryptoCurrenciesInEUR() throws JsonProcessingException {
        String jsonString = Fixture.readFileResource("ok-eur-10.json");
        Listing listing = OBJECT_MAPPER.readValue(jsonString, Listing.class);
        assertThat(listing).isNotNull();
        assertThat(listing.getStatus().getTimestamp()).isEqualTo(Instant.parse("2021-10-21T20:47:22.583Z"));
        assertThat(listing.getStatus().getErrorCode()).isEqualTo(0);
        assertThat(listing.getStatus().getErrorMessage()).isNull();
        assertThat(listing.getStatus().getCreditCount()).isEqualTo(1);
        assertThat(listing.getData().stream().map(CryptoCurrency::getSymbol).collect(Collectors.toList()))
                .isEqualTo(List.of("BTC", "ETH", "BNB", "ADA", "USDT", "SOL", "XRP", "DOT", "USDC", "DOGE"));
        assertThat(listing.getData().stream().map(c -> c.getQuote().getQuoteDetail().getMarketCapDominance())
                .collect(Collectors.toList()))
                .isEqualTo(List.of(46.2667, 18.8906, 3.0963, 2.7752, 2.7164, 2.1814, 2.0191, 1.6551, 1.2649, 1.263));
        assertThat(listing.getData().stream().filter(c -> c.getTags().contains("stablecoin")).count()).isEqualTo(2);
    }
}
