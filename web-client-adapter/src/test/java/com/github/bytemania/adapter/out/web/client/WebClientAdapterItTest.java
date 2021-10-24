package com.github.bytemania.adapter.out.web.client;

import com.github.bytemania.adapter.out.web.client.impl.CoinMarketCapWebClientConfigImpl;
import com.github.bytemania.adapter.out.web.client.impl.CoinMarketCapWebClientImpl;
import com.github.bytemania.cryptobalance.port.out.LoadCoinMarketCapPort;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = {
        CoinMarketCapWebClientConfig.class,
        CoinMarketCapWebClientConfigImpl.class,
        CoinMarketCapWebClient.class,
        CoinMarketCapWebClientImpl.class,
        WebClientAdapter.class
})
@TestPropertySource(properties = {
        "WEB_CLIENT_BASE_URL=http://localhost:9090",
        "WEB_CLIENT_AUTH_KEY=UNKNOWN_KEY",
        "WEB_CLIENT_TIMEOUT_MS=1000",
        "WEB_CLIENT_NUMBER_OF_CRYPTOS=10",
        "APP_CURRENCY=USD"
})
class WebClientAdapterItTest {

    static {
        System.setProperty("WEB_CLIENT_BASE_URL", "http://localhost:9090");
        System.setProperty("WEB_CLIENT_AUTH_KEY", "UNKNOWN_KEY");
        System.setProperty("WEB_CLIENT_TIMEOUT_MS", "1000");
        System.setProperty("WEB_CLIENT_NUMBER_OF_CRYPTOS", "10");
        System.setProperty("APP_CURRENCY", "USD");
    }

    private static MockWebServer mockBackEnd;

    @BeforeAll
    static void beforeAll() throws IOException {
        mockBackEnd = new MockWebServer();
        mockBackEnd.start(9090);
    }

    @AfterAll
    static void afterAll() throws IOException {
        mockBackEnd.shutdown();
    }

    @Autowired
    private LoadCoinMarketCapPort webClientAdapter;

    @Test
    @DisplayName("should create a WebClientAdapter")
    void shouldCreateAWebClientAdapter() throws InterruptedException {
        final String filename = "ok-usd-10.json";
        String body = Fixture.readFileResource(filename);
        MockResponse response = new MockResponse()
                .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setBody(body);
        mockBackEnd.enqueue(response);

        assertThat(webClientAdapter).isNotNull();

        webClientAdapter.load();

        RecordedRequest request = mockBackEnd.takeRequest();
        assertThat(request.getMethod()).isEqualTo(HttpMethod.GET.toString());
        assertThat(request.getHeader("X-CMC_PRO_API_KEY")).isEqualTo("UNKNOWN_KEY");
        assertThat(request.getHeader(HttpHeaders.ACCEPT)).isEqualTo(MediaType.APPLICATION_JSON_VALUE);
    }
}
