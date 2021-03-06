package com.github.bytemania.adapter.out.web.client.impl;

import com.github.bytemania.adapter.out.web.client.CoinMarketCapWebClientConfig;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
public class CoinMarketCapWebClientConfigImpl implements CoinMarketCapWebClientConfig {

    @Value("#{systemProperties['WEB_CLIENT_BASE_URL'] ?: 'https://pro-api.coinmarketcap.com/v1'}")
    private String baseUrl;

    @Value("#{systemProperties['WEB_CLIENT_AUTH_KEY'] ?: 'UNKNOWN_KEY'}")
    private String authenticationKey;

    @Value("#{systemProperties['WEB_CLIENT_TIMEOUT_MS'] ?: '90000'}")
    private int timeoutMs;

    @Value("#{systemProperties['WEB_CLIENT_NUMBER_OF_CRYPTOS'] ?: '100'}")
    int numberOfCryptos;

    @Value("#{systemProperties['APP_CURRENCY'] ?: 'USD'}")
    String currency;
}
