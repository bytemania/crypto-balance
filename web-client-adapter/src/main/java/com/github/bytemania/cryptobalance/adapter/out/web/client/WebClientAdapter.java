package com.github.bytemania.cryptobalance.adapter.out.web.client;

import com.github.bytemania.cryptobalance.adapter.out.web.client.exception.CoinMarketCapClientException;
import com.github.bytemania.cryptobalance.domain.dto.Crypto;
import com.github.bytemania.cryptobalance.port.out.LoadCoinMarketCapPortOut;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Set;

@Slf4j
@Component
public class WebClientAdapter implements InitializingBean, LoadCoinMarketCapPortOut {

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
    public Set<Crypto> load() {
        try {
            log.info("Fetching crypto cap from Coin Market Cap Web Client");
            return CoinMarketCapMapper.INSTANCE.listingToCryptos(coinMarketCapWebClient.doGet());
        } catch (CoinMarketCapClientException e) {
            log.warn("Error getting crypto cap from Coin Market Cap Web Client");
            throw new IllegalStateException(e);
        }
    }
}
