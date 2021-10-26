package com.github.bytemania.cryptobalance.adapter.out.web.client.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.bytemania.cryptobalance.adapter.out.web.client.CoinMarketCapWebClient;
import com.github.bytemania.cryptobalance.adapter.out.web.client.CoinMarketCapWebClientConfig;
import com.github.bytemania.cryptobalance.adapter.out.web.client.dto.Listing;
import com.github.bytemania.cryptobalance.adapter.out.web.client.exception.CoinMarketCapClientException;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.netty.http.client.HttpClient;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class CoinMarketCapWebClientImpl implements CoinMarketCapWebClient {
    private final static String RESOURCE_URI = "/cryptocurrency/listings/latest";
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private final CoinMarketCapWebClientConfig coinMarketCapWebClientConfig;
    private WebClient webClient;

    @Autowired
    public CoinMarketCapWebClientImpl(CoinMarketCapWebClientConfig coinMarketCapWebClientConfig) {
        this.coinMarketCapWebClientConfig = coinMarketCapWebClientConfig;
    }

    @Override
    public void create() {
        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, coinMarketCapWebClientConfig.getTimeoutMs())
                .responseTimeout(Duration.ofMillis(coinMarketCapWebClientConfig.getTimeoutMs()))
                .doOnConnected(connection ->
                        connection
                                .addHandlerLast(new ReadTimeoutHandler(coinMarketCapWebClientConfig.getTimeoutMs(), TimeUnit.MILLISECONDS))
                                .addHandlerLast(new WriteTimeoutHandler(coinMarketCapWebClientConfig.getTimeoutMs(), TimeUnit.MILLISECONDS)));

        ReactorClientHttpConnector clientConnector = new ReactorClientHttpConnector(httpClient);

        webClient = WebClient.builder()
                .clientConnector(clientConnector)
                .baseUrl(coinMarketCapWebClientConfig.getBaseUrl())
                .defaultHeader("X-CMC_PRO_API_KEY", coinMarketCapWebClientConfig.getAuthenticationKey())
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .build();

        log.info("Web client Started For Coin Market Cap");
    }

    @Override
    public Listing doGet() throws CoinMarketCapClientException {
        try {
            if (webClient == null) {
                String errorMessage = "Error getting message from CoinMarketCap client must be created";
                log.warn(errorMessage);
                throw new CoinMarketCapClientException(errorMessage);
            }

            String raw =  webClient
                    .get()
                    .uri(uriBuilder ->
                            uriBuilder
                                    .path(RESOURCE_URI)
                                    .queryParam("start", 1)
                                    .queryParam("limit", coinMarketCapWebClientConfig.getNumberOfCryptos())
                                    .queryParam("convert", coinMarketCapWebClientConfig.getCurrency())
                                    .build())
                    .retrieve()
                    .bodyToMono(String.class)
                    .block(Duration.ofMillis(coinMarketCapWebClientConfig.getTimeoutMs()));

            Listing listing = OBJECT_MAPPER.readValue(raw, Listing.class);

            if (listing.getStatus().getErrorCode() != 0) {
                String errorMessage =  String.format("Error getting message from CoinMarketCap error:%d message:%s",
                        listing.getStatus().getErrorCode(),
                        listing.getStatus().getErrorMessage() == null ? "" :
                                listing.getStatus().getErrorMessage());
                log.warn(errorMessage);
                throw new CoinMarketCapClientException(errorMessage);
            }

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ssZ");
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

            String dateFormatted = simpleDateFormat.format(listing.getStatus().getTimestamp());

            log.info("Coin Market Cap called at={}, currency={}, numberOfCryptos={}, creditSpent={}",
                    dateFormatted,
                    coinMarketCapWebClientConfig.getCurrency(),
                    coinMarketCapWebClientConfig.getNumberOfCryptos(),
                    listing.getStatus().getCreditCount());

            return listing;
        } catch (JsonProcessingException e) {
            log.warn("Error getting message from CoinMarketCap: Parse Error", e);
            throw new CoinMarketCapClientException("Error getting message from CoinMarketCap: Parse Error", e);
        } catch (WebClientResponseException e) {
            log.warn("Connection to Coin MarketCap Error", e);
            throw new CoinMarketCapClientException("Connection to Coin MarketCap Error", e);
        } catch (IllegalStateException e) {
            if (e.getMessage().startsWith("Timeout on blocking read for")) {
                log.warn("Connection Timed out after {}ms", coinMarketCapWebClientConfig.getTimeoutMs());
                throw new CoinMarketCapClientException("Connection timeout after "
                        + coinMarketCapWebClientConfig.getTimeoutMs() + "ms", e);
            }
            else {
                throw e;
            }
        }
    }

}
