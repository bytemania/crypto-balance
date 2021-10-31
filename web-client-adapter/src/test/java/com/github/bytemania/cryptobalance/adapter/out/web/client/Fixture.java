package com.github.bytemania.cryptobalance.adapter.out.web.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.bytemania.cryptobalance.adapter.out.web.client.dto.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.fail;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Fixture {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public static final Listing A_VALID_LISTING = Listing
                    .builder()
                    .status(Status
                            .builder()
                            .timestamp(new Date(Instant.parse("2021-10-21T20:47:22.583Z").getEpochSecond()))
                            .errorCode(0)
                            .errorMessage(null)
                            .creditCount(1)
                            .build()
                    )
                    .data(List.of(
                                    CryptoCurrency
                                            .builder()
                                            .symbol("BTC")
                                            .tags(List.of(
                                                    "mineable",
                                                    "pow",
                                                    "sha-256",
                                                    "store-of-value",
                                                    "state-channels",
                                                    "coinbase-ventures-portfolio",
                                                    "three-arrows-capital-portfolio",
                                                    "polychain-capital-portfolio",
                                                    "binance-labs-portfolio",
                                                    "arrington-xrp-capital",
                                                    "blockchain-capital-portfolio",
                                                    "boostvc-portfolio",
                                                    "cms-holdings-portfolio",
                                                    "dcg-portfolio",
                                                    "dragonfly-capital-portfolio",
                                                    "electric-capital-portfolio",
                                                    "fabric-ventures-portfolio",
                                                    "framework-ventures",
                                                    "galaxy-digital-portfolio",
                                                    "huobi-capital",
                                                    "alameda-research-portfolio",
                                                    "a16z-portfolio",
                                                    "1confirmation-portfolio",
                                                    "winklevoss-capital",
                                                    "usv-portfolio",
                                                    "placeholder-ventures-portfolio",
                                                    "pantera-capital-portfolio",
                                                    "multicoin-capital-portfolio",
                                                    "paradigm-xzy-screener"
                                            ))
                                            .quote(Quote
                                                    .builder()
                                                    .quoteDetail(QuoteDetail
                                                            .builder()
                                                            .marketCapDominance(46.2667)
                                                            .price(100.4)
                                                            .build())
                                                    .build())
                                            .build(),
                                    CryptoCurrency
                                            .builder()
                                            .symbol("USDT")
                                            .tags(List.of(
                                                    "payments",
                                                    "stablecoin",
                                                    "stablecoin-asset-backed",
                                                    "binance-smart-chain",
                                                    "avalanche-ecosystem",
                                                    "solana-ecosystem"
                                            ))
                                            .quote(Quote
                                                    .builder()
                                                    .quoteDetail(QuoteDetail
                                                            .builder()
                                                            .marketCapDominance(2.7144)
                                                            .price(20.34563)
                                                            .build())
                                                    .build())
                                            .build()
                            )
                    )
                    .build();

    public static String readFileResource(String filename) {
        InputStream jsonStream = Fixture.class.getClassLoader().getResourceAsStream(filename);
        String jsonString = "";
        if (jsonStream == null) {
            fail("Can't read the file");
        } else {
            try {
                jsonString = new String(jsonStream.readAllBytes(), StandardCharsets.UTF_8);
            } catch (IOException e) {
                fail("Can't read the file");
            }
        }
        return jsonString;
    }

    public static Listing readFileResourceAndParse(String filename) throws JsonProcessingException {
        String jsonString = readFileResource("ok-usd-10.json");
        return OBJECT_MAPPER.readValue(jsonString, Listing.class);
    }



}
