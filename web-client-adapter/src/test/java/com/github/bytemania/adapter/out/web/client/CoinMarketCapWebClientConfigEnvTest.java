package com.github.bytemania.adapter.out.web.client;

import com.github.bytemania.adapter.out.web.client.impl.CoinMarketCapWebClientConfigImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(classes = {CoinMarketCapWebClientConfig.class, CoinMarketCapWebClientConfigImpl.class})
@TestPropertySource(properties = {
        "WEB_CLIENT_BASE_URL=https://anotherUrl:9090",
        "WEB_CLIENT_AUTH_KEY=A_KEY",
        "WEB_CLIENT_TIMEOUT_MS=10",
        "WEB_CLIENT_NUMBER_OF_CRYPTOS=50",
        "WEB_CLIENT_CURRENCY=EUR"
})
public class CoinMarketCapWebClientConfigEnvTest {

    static {
        System.setProperty("WEB_CLIENT_BASE_URL", "https://anotherUrl:9090");
        System.setProperty("WEB_CLIENT_AUTH_KEY", "A_KEY");
        System.setProperty("WEB_CLIENT_TIMEOUT_MS", "10");
        System.setProperty("WEB_CLIENT_NUMBER_OF_CRYPTOS", "50");
        System.setProperty("WEB_CLIENT_CURRENCY", "EUR");
    }

    @SuppressWarnings({"UnusedDeclaration"})
    @Autowired
    private CoinMarketCapWebClientConfig coinMarketCapWebClientConfig;

    @Test
    @DisplayName("Should override the default if the ENV is settle")
    void shouldOverrideTheDefaultValues() {
        assertThat(coinMarketCapWebClientConfig.getBaseUrl()).isEqualTo("https://anotherUrl:9090");
        assertThat(coinMarketCapWebClientConfig.getAuthenticationKey()).isEqualTo("A_KEY");
        assertThat(coinMarketCapWebClientConfig.getTimeoutMs()).isEqualTo(10L);
        assertThat(coinMarketCapWebClientConfig.getNumberOfCryptos()).isEqualTo(50);
        assertThat(coinMarketCapWebClientConfig.getCurrency()).isEqualTo("EUR");
    }
}
