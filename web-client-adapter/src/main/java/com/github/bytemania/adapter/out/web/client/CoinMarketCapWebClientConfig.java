package com.github.bytemania.adapter.out.web.client;

public interface CoinMarketCapWebClientConfig {

    String getBaseUrl();

    String getAuthenticationKey();

    int getTimeoutMs();

    int getNumberOfCryptos();

    String getCurrency();

}
