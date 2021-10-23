package com.github.bytemania.adapter.out.web.client;

import com.github.bytemania.adapter.out.web.client.exception.CoinMarketCapClientException;
import com.github.bytemania.adapter.out.web.client.impl.Mapper;
import com.github.bytemania.cryptobalance.domain.Crypto;
import com.github.bytemania.cryptobalance.port.out.LoadCoinMarketCapPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class WebClientAdapter implements InitializingBean, LoadCoinMarketCapPort {

    private final CoinMarketCapWebClient coinMarketCapWebClient;

    @Autowired
    public WebClientAdapter(CoinMarketCapWebClient coinMarketCapWebClient) {
        this.coinMarketCapWebClient = coinMarketCapWebClient;
    }

    @Override
    public void afterPropertiesSet() {
        coinMarketCapWebClient.create();
        log.info("Coin Market Cap Web Client created");
    }

    @Override
    public List<Crypto> load() {
        try {
            log.info("Fetching crypto cap from Coin Market Cap Web Client");
            return Mapper.fromListing(coinMarketCapWebClient.doGet());
        } catch (CoinMarketCapClientException e) {
            log.warn("Error getting crypto cap from Coin Market Cap Web Client");
            throw new IllegalStateException(e);
        }
    }
}
