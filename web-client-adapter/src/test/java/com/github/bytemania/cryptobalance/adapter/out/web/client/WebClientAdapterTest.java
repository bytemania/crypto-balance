package com.github.bytemania.cryptobalance.adapter.out.web.client;


import com.github.bytemania.cryptobalance.adapter.out.web.client.exception.CoinMarketCapClientException;
import com.github.bytemania.cryptobalance.domain.dto.Crypto;
import nl.altindag.log.LogCaptor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

class WebClientAdapterTest {

    private static final CoinMarketCapWebClient coinMarketCapWebClient = Mockito.mock(CoinMarketCapWebClient.class);
    private static final WebClientAdapter webClientAdapter = new WebClientAdapter(coinMarketCapWebClient);
    private static LogCaptor logCaptor;

    @BeforeAll
    static void beforeAll() {
        logCaptor = LogCaptor.forClass(WebClientAdapter.class);
    }

    @AfterAll
    static void afterAll() {
        logCaptor.close();
    }

    @BeforeEach
    void beforeEach() {
        logCaptor.clearLogs();
    }

    @Test
    @DisplayName("Should create client and log in AfterSetProperties")
    void shouldCreateClientAndLogInAfterSetProperties() {
        webClientAdapter.afterPropertiesSet();
        then(coinMarketCapWebClient).should(times(1)).create();
        org.assertj.core.api.Assertions.assertThat(logCaptor.getLogs())
                .hasSize(1);
        Assertions.assertThat(logCaptor.getInfoLogs())
                .hasSize(1)
                .containsExactly("Coin Market Cap Web Client created");
    }

    @Test
    @DisplayName("Should process and translate the data received from Coin Market Cap")
    void shouldProcessAndTranslateTheDataReceivedFromCoinMarketCap() throws CoinMarketCapClientException {
        given(coinMarketCapWebClient.doGet()).willReturn(Fixture.A_VALID_LISTING);
        Set<Crypto> cryptos = webClientAdapter.load();

        assertThat(cryptos)
                .hasSize(2)
                .containsExactly(
                        new Crypto("BTC", 46.27, BigDecimal.ZERO, false),
                        new Crypto("USDT", 2.71, BigDecimal.ZERO, true));

       assertThat(logCaptor.getLogs())
                .hasSize(1);
        assertThat(logCaptor.getInfoLogs())
                .hasSize(1)
                .containsExactly("Fetching crypto cap from Coin Market Cap Web Client");
    }

    @Test
    @DisplayName("Should throw an IllegalStateException on error load")
    void shouldThrowAnIllegalStateExceptionOnErrorLoad() throws CoinMarketCapClientException {
        given(coinMarketCapWebClient.doGet()).willThrow(new CoinMarketCapClientException("Cannot login"));
        assertThatThrownBy(webClientAdapter::load)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Cannot login")
                .hasCauseInstanceOf(CoinMarketCapClientException.class);

        assertThat(logCaptor.getLogs())
                .hasSize(2);
        assertThat(logCaptor.getInfoLogs())
                .hasSize(1)
                .containsExactly("Fetching crypto cap from Coin Market Cap Web Client");
        assertThat(logCaptor.getWarnLogs())
                .hasSize(1)
                .containsExactly("Error getting crypto cap from Coin Market Cap Web Client");
    }
}
