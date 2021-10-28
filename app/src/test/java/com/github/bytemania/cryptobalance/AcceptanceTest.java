package com.github.bytemania.cryptobalance;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.ServletContext;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {CryptoBalanceConfiguration.class })
@WebAppConfiguration
@Profile("acceptance-test")
@TestPropertySource(properties = {
        "WEB_CLIENT_BASE_URL=http://localhost:9090",
        "WEB_CLIENT_AUTH_KEY=UNKNOWN_KEY",
        "WEB_CLIENT_TIMEOUT_MS=1000",
        "WEB_CLIENT_NUMBER_OF_CRYPTOS=10",
        "APP_CURRENCY=USD"
})
public class AcceptanceTest {

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
        Files.deleteIfExists(Path.of("portfolio.db"));
    }

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
    }

    @Test
    @DisplayName("Should load the servlets")
    void shouldLoadTheServlets() {
        ServletContext servletContext = webApplicationContext.getServletContext();

        assertThat(servletContext).isNotNull();
        assertThat(servletContext).isInstanceOf(MockServletContext.class);
        assertThat(webApplicationContext.getBean("portfolioController")).isNotNull();
        assertThat(webApplicationContext.getBean("allocationController")).isNotNull();
    }


    @Test
    @DisplayName("create portfolio and rebalance test")
    void createPortfolioAndRebalanceTest() throws Exception {

        final String filename = "ok-usd-10.json";
        String body = Fixture.readFileResource(filename);
        MockResponse response = new MockResponse()
                .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setBody(body);
        mockBackEnd.enqueue(response);

        mockMvc.perform(patch("/portfolio")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .content("{" +
                        "  \"currency\":\"USD\"," +
                        "  \"cryptosToUpdate\": [" +
                        "      {\"symbol\": \"BTC\", \"amountInvested\": 100}," +
                        "      {\"symbol\": \"ETH\", \"amountInvested\": 50}" +
                        "    ]" +
                        "}"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().stringValues(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
                .andExpect(content().json(
                        "{" +
                                "   \"currency\":\"USD\"," +
                                "   \"totalAmountInvested\":150.0," +
                                "   \"cryptos\":[" +
                                "       {\"symbol\":\"BTC\",\"amountInvested\":100.0}," +
                                "       {\"symbol\":\"ETH\",\"amountInvested\":50.0}" +
                                "    ]" +
                                "}"));

        mockMvc.perform(get("/portfolio")
                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().stringValues(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
                .andExpect(content().json(
                        "{" +
                                "   \"currency\":\"USD\"," +
                                "   \"totalAmountInvested\":150.0," +
                                "   \"cryptos\":[" +
                                "       {\"symbol\":\"BTC\",\"amountInvested\":100.0}," +
                                "       {\"symbol\":\"ETH\",\"amountInvested\":50.0}" +
                                "    ]" +
                                "}"));

        String allocateUri = "/allocate?" +
                "stableCryptoSymbol=USDT&" +
                "stableCryptoPercentage=20&" +
                "valueToInvest=200&" +
                "minValueToAllocate=25";

        mockMvc.perform(get(allocateUri))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(
                        "{" +
                                "  \"currency\":\"USD\"," +
                                "  \"amountToInvest\":200.0," +
                                "  \"rest\":0.0," +
                                "  \"amountInvested\":150.0," +
                                "  \"stableCrypto\":{" +
                                "    \"symbol\":\"USDT\"," +
                                "    \"marketCapPercentage\":20.0," +
                                "    \"amountToInvest\":70.0," +
                                "    \"operation\":\"BUY\"," +
                                "    \"rebalanceToInvest\":70.0," +
                                "    \"currentInvested\":0.0}," +
                                "  \"cryptos\":[" +
                                "    {" +
                                "      \"symbol\":\"BTC\"," +
                                "      \"marketCapPercentage\":46.16," +
                                "      \"amountToInvest\":161.56," +
                                "      \"operation\":\"BUY\"," +
                                "      \"rebalanceToInvest\":61.56," +
                                "      \"currentInvested\":100.0" +
                                "    }," +
                                "    {" +
                                "      \"symbol\":\"ETH\"," +
                                "      \"marketCapPercentage\":18.77," +
                                "      \"amountToInvest\":65.7," +
                                "      \"operation\":\"BUY\"," +
                                "      \"rebalanceToInvest\":15.7," +
                                "      \"currentInvested\":50.0" +
                                "    }," +
                                "    {" +
                                "      \"symbol\":\"ADA\"," +
                                "      \"marketCapPercentage\":7.0," +
                                "      \"amountToInvest\":25.0," +
                                "      \"operation\":\"BUY\"," +
                                "      \"rebalanceToInvest\":25.0," +
                                "      \"currentInvested\":0.0" +
                                "    }," +
                                "    {" +
                                "      \"symbol\":\"BNB\"," +
                                "      \"marketCapPercentage\":7.0," +
                                "      \"amountToInvest\":25.0," +
                                "      \"operation\":\"BUY\"," +
                                "      \"rebalanceToInvest\":25.0," +
                                "      \"currentInvested\":0.0" +
                                "    }," +
                                "    {" +
                                "      \"symbol\":\"SOL\"," +
                                "      \"marketCapPercentage\":0.78," +
                                "      \"amountToInvest\":2.75," +
                                "      \"operation\":\"BUY\"," +
                                "      \"rebalanceToInvest\":2.75," +
                                "      \"currentInvested\":0.0" +
                                "    }" +
                                "  ]" +
                                "}"
                ));
    }

}
