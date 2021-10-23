package com.github.bytemania.adapter.out.web.client;

import com.github.bytemania.adapter.out.web.client.impl.CoinMarketCapWebClientConfigImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(classes = {CoinMarketCapWebClientConfig.class, CoinMarketCapWebClientConfigImpl.class})
public class CoinMarketCapWebClientConfigTest {

    static {
        System.clearProperty("WEB_CLIENT_BASE_URL");
        System.clearProperty("WEB_CLIENT_AUTH_KEY");
        System.clearProperty("WEB_CLIENT_TIMEOUT_MS");
        System.clearProperty("WEB_CLIENT_NUMBER_OF_CRYPTOS");
        System.clearProperty("WEB_CLIENT_CURRENCY");
    }

    @SuppressWarnings({"UnusedDeclaration"})
    @Autowired
    private CoinMarketCapWebClientConfig coinMarketCapWebClientConfig;

    @Test
    @DisplayName("Should override the default if the ENV is settle")
    void shouldOverrideTheDefaultValues() {
        assertThat(coinMarketCapWebClientConfig.getBaseUrl()).isEqualTo("https://pro-api.coinmarketcap.com/v1");
        assertThat(coinMarketCapWebClientConfig.getAuthenticationKey()).isEqualTo("UNKNOWN_KEY");
        assertThat(coinMarketCapWebClientConfig.getTimeoutMs()).isEqualTo(90000);
        assertThat(coinMarketCapWebClientConfig.getNumberOfCryptos()).isEqualTo(100);
        assertThat(coinMarketCapWebClientConfig.getCurrency()).isEqualTo("USD");
    }
}
