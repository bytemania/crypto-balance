package com.github.bytemania.adapter.out.web.client.impl;

import com.github.bytemania.adapter.out.web.client.CoinMarketCapWebClient;
import com.github.bytemania.adapter.out.web.client.CoinMarketCapWebClientConfig;
import com.github.bytemania.adapter.out.web.client.Fixture;
import com.github.bytemania.adapter.out.web.client.dto.Listing;
import com.github.bytemania.adapter.out.web.client.exception.CoinMarketCapClientException;
import nl.altindag.log.LogCaptor;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest(classes = {
        CoinMarketCapWebClientConfig.class,
        CoinMarketCapWebClientConfigImpl.class,
        CoinMarketCapWebClient.class,
        CoinMarketCapWebClientImpl.class
})
@TestPropertySource(properties = {
        "WEB_CLIENT_BASE_URL=http://localhost:9090",
        "WEB_CLIENT_AUTH_KEY=UNKNOWN_KEY",
        "WEB_CLIENT_TIMEOUT_MS=1000",
        "WEB_CLIENT_NUMBER_OF_CRYPTOS=10",
        "WEB_CLIENT_CURRENCY=USD"
})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class CoinMarketCapWebClientImplItTest {

    private static MockWebServer mockBackEnd;

    static {
        System.setProperty("WEB_CLIENT_BASE_URL", "http://localhost:9090");
        System.setProperty("WEB_CLIENT_AUTH_KEY", "UNKNOWN_KEY");
        System.setProperty("WEB_CLIENT_TIMEOUT_MS", "1000");
        System.setProperty("WEB_CLIENT_NUMBER_OF_CRYPTOS", "10");
        System.setProperty("WEB_CLIENT_CURRENCY", "USD");
    }


    @BeforeAll
    static void beforeAll() throws IOException {
        mockBackEnd = new MockWebServer();
        mockBackEnd.start(9090);
    }

    @AfterAll
    static void afterAll() throws IOException {
        mockBackEnd.shutdown();
    }

    private LogCaptor logCaptor;

    @BeforeEach
    void beforeEach() {
        logCaptor = LogCaptor.forClass(CoinMarketCapWebClientImpl.class);
        logCaptor.clearLogs();
    }

    @AfterEach
    void afterEach() {
        logCaptor.close();
    }

    @Autowired
    private CoinMarketCapWebClient client;

    @Test
    @DisplayName("Should process a valid response")
    void shouldProcessAValidResponse() throws Exception {
        final String filename = "ok-usd-10.json";
        String body = Fixture.readFileResource(filename);

        MockResponse response = new MockResponse()
                .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setBody(body);

        mockBackEnd.enqueue(response);

        Listing expected = Fixture.readFileResourceAndParse(filename);

        client.create();
        Listing result = client.doGet();

        RecordedRequest request = mockBackEnd.takeRequest();
        assertThat(request.getMethod()).isEqualTo(HttpMethod.GET.toString());
        assertThat(request.getHeader("X-CMC_PRO_API_KEY")).isEqualTo("UNKNOWN_KEY");
        assertThat(request.getHeader(HttpHeaders.ACCEPT)).isEqualTo(MediaType.APPLICATION_JSON_VALUE);
        assertThat(result).isEqualTo(expected);

        assertThat(logCaptor.getLogs())
                .hasSize(2);
        assertThat(logCaptor.getInfoLogs())
                .hasSize(2)
                .containsExactly(
                        "Web client Started For Coin Market Cap",
                        "Coin Market Cap called at=2021-10-21 20:00:11+0000, currency=USD, numberOfCryptos=10, creditSpent=1");
    }

    @Test
    @DisplayName("Should process a invalid response")
    void shouldProcessAInvalidResponse() throws Exception {
        MockResponse response = new MockResponse()
                .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setBody("{\n" +
                        "  \"status\": {\n" +
                        "    \"timestamp\": \"2021-10-21T20:00:11.716Z\",\n" +
                        "    \"error_code\": 1,\n" +
                        "    \"error_message\": \"No more credits\",\n" +
                        "    \"elapsed\": 14,\n" +
                        "    \"credit_count\": 1,\n" +
                        "    \"notice\": null,\n" +
                        "    \"total_count\": 6683\n" +
                        "  },\n" +
                        "\"data\": []" +
                        "}");

        mockBackEnd.enqueue(response);

        client.create();

        assertThatThrownBy(() -> client.doGet())
                .isInstanceOf(CoinMarketCapClientException.class)
                .hasMessage("Error getting message from CoinMarketCap error:1 message:No more credits");

        RecordedRequest request = mockBackEnd.takeRequest();
        assertThat(request.getMethod()).isEqualTo(HttpMethod.GET.toString());
        assertThat(request.getHeader("X-CMC_PRO_API_KEY")).isEqualTo("UNKNOWN_KEY");
        assertThat(request.getHeader(HttpHeaders.ACCEPT)).isEqualTo(MediaType.APPLICATION_JSON_VALUE);

        assertThat(logCaptor.getLogs())
                .hasSize(2);
        assertThat(logCaptor.getInfoLogs())
                .hasSize(1)
                .containsExactly("Web client Started For Coin Market Cap");
        assertThat(logCaptor.getWarnLogs())
                .hasSize(1)
                .containsExactly("Error getting message from CoinMarketCap error:1 message:No more credits");
    }

    @Test
    @DisplayName("Should process a invalid payload")
    void shouldProcessAInvalidPayload() throws Exception {
        MockResponse response = new MockResponse()
                .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setBody("Invalid Payload");

        mockBackEnd.enqueue(response);

        client.create();

        assertThatThrownBy(() -> client.doGet())
                .isInstanceOf(CoinMarketCapClientException.class)
                .hasMessage("Error getting message from CoinMarketCap: Parse Error");

        RecordedRequest request = mockBackEnd.takeRequest();
        assertThat(request.getMethod()).isEqualTo(HttpMethod.GET.toString());
        assertThat(request.getHeader("X-CMC_PRO_API_KEY")).isEqualTo("UNKNOWN_KEY");
        assertThat(request.getHeader(HttpHeaders.ACCEPT)).isEqualTo(MediaType.APPLICATION_JSON_VALUE);

        assertThat(logCaptor.getLogs())
                .hasSize(2);
        assertThat(logCaptor.getInfoLogs())
                .hasSize(1)
                .containsExactly("Web client Started For Coin Market Cap");
        assertThat(logCaptor.getWarnLogs())
                .hasSize(1)
                .containsExactly("Error getting message from CoinMarketCap: Parse Error");
    }

    @Test
    @DisplayName("Should process a authentication error")
    void shouldProcessAAuthenticationError() throws Exception {
        final String filename = "401_invalid_api_key.json";
        String body = Fixture.readFileResource(filename);

        MockResponse response = new MockResponse()
                .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setBody(body)
                .setStatus("401 Unauthorized")
                .setResponseCode(401);

        mockBackEnd.enqueue(response);

        client.create();

        assertThatThrownBy(() -> client.doGet())
                .isInstanceOf(CoinMarketCapClientException.class)
                .hasMessage("Connection to Coin MarketCap Error");

        RecordedRequest request = mockBackEnd.takeRequest();
        assertThat(request.getMethod()).isEqualTo(HttpMethod.GET.toString());
        assertThat(request.getHeader("X-CMC_PRO_API_KEY")).isEqualTo("UNKNOWN_KEY");
        assertThat(request.getHeader(HttpHeaders.ACCEPT)).isEqualTo(MediaType.APPLICATION_JSON_VALUE);

        assertThat(logCaptor.getLogs())
                .hasSize(2);
        assertThat(logCaptor.getInfoLogs())
                .hasSize(1)
                .containsExactly("Web client Started For Coin Market Cap");
        assertThat(logCaptor.getWarnLogs())
                .hasSize(1)
                .containsExactly("Connection to Coin MarketCap Error");
    }

    @Test
    @DisplayName("Should process a timeout")
    void shouldProcessATimeout() throws Exception {
        final String filename = "ok-usd-10.json";
        String body = Fixture.readFileResource(filename);

        MockResponse response = new MockResponse()
                .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setBody(body)
                .setBodyDelay(2, TimeUnit.SECONDS);

        mockBackEnd.enqueue(response);

        client.create();

        assertThatThrownBy(() -> client.doGet())
                .isInstanceOf(CoinMarketCapClientException.class)
                .hasMessage("Connection timeout after 1000ms");

        RecordedRequest request = mockBackEnd.takeRequest();
        assertThat(request.getMethod()).isEqualTo(HttpMethod.GET.toString());
        assertThat(request.getHeader("X-CMC_PRO_API_KEY")).isEqualTo("UNKNOWN_KEY");
        assertThat(request.getHeader(HttpHeaders.ACCEPT)).isEqualTo(MediaType.APPLICATION_JSON_VALUE);

        assertThat(logCaptor.getLogs())
                .hasSize(2);
        assertThat(logCaptor.getInfoLogs())
                .hasSize(1)
                .containsExactly("Web client Started For Coin Market Cap");
        assertThat(logCaptor.getWarnLogs())
                .hasSize(1)
                .containsExactly("Connection Timed out after 1000ms");
    }

    @Test
    @DisplayName("Should process a Server Error")
    void shouldProcessAServerError() throws Exception {
        MockResponse response = new MockResponse()
                .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setBody("Invalid Payload")
                .setStatus("Server Error: 500")
                .setResponseCode(500);

        mockBackEnd.enqueue(response);

        client.create();

        assertThatThrownBy(() -> client.doGet())
                .isInstanceOf(CoinMarketCapClientException.class)
                .hasMessage("Connection to Coin MarketCap Error");

        RecordedRequest request = mockBackEnd.takeRequest();
        assertThat(request.getMethod()).isEqualTo(HttpMethod.GET.toString());
        assertThat(request.getHeader("X-CMC_PRO_API_KEY")).isEqualTo("UNKNOWN_KEY");
        assertThat(request.getHeader(HttpHeaders.ACCEPT)).isEqualTo(MediaType.APPLICATION_JSON_VALUE);

        assertThat(logCaptor.getLogs())
                .hasSize(2);
        assertThat(logCaptor.getInfoLogs())
                .hasSize(1)
                .containsExactly("Web client Started For Coin Market Cap");
        assertThat(logCaptor.getWarnLogs())
                .hasSize(1)
                .containsExactly("Connection to Coin MarketCap Error");
    }


    @Test
    @DisplayName("Should process when a server is not connected")
    void shouldProcessServerNotConnected() {
        assertThatThrownBy(() -> client.doGet())
                .isInstanceOf(CoinMarketCapClientException.class)
                .hasMessage("Error getting message from CoinMarketCap client must be created");

        assertThat(logCaptor.getLogs())
                .hasSize(1);
        assertThat(logCaptor.getWarnLogs())
                .hasSize(1)
                .containsExactly("Error getting message from CoinMarketCap client must be created");
    }
}
