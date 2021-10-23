package com.github.bytemania.adapter.out.web.client;

import com.github.bytemania.adapter.out.web.client.dto.Listing;
import com.github.bytemania.adapter.out.web.client.exception.CoinMarketCapClientException;

public interface CoinMarketCapWebClient {

    void create();

    Listing doGet() throws CoinMarketCapClientException;
}
