package com.github.bytemania.cryptobalance.adapter.out.web.client.exception;

public class CoinMarketCapClientException extends Exception {

    public CoinMarketCapClientException(String message) {
        super(message);
    }

    public CoinMarketCapClientException(String message, Throwable cause) {
        super(message, cause);
    }
}
