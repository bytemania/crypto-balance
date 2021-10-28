package com.github.bytemania.cryptobalance.adapter.in.web.server;

import com.github.bytemania.cryptobalance.adapter.in.web.server.dto.portfolio.Crypto;
import com.github.bytemania.cryptobalance.adapter.in.web.server.impl.ControllerConfigImpl;
import com.github.bytemania.cryptobalance.adapter.in.web.server.impl.Mapper;
import com.github.bytemania.cryptobalance.port.in.LoadPortfolioPortIn;
import com.github.bytemania.cryptobalance.port.in.UpdatePortfolioPortIn;
import nl.altindag.log.LogCaptor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AllocationController.class)
@ContextConfiguration(classes = {
        PortfolioController.class,
        ControllerConfig.class,
        ControllerConfigImpl.class,
        GlobalExceptionHandler.class
})
class PortfolioControllerItTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LoadPortfolioPortIn loadPortfolioPortIn;

    @MockBean
    private UpdatePortfolioPortIn updatePortfolioPortIn;

    private LogCaptor logCaptor;

    @BeforeEach
    void beforeEach() {
        logCaptor = LogCaptor.forClass(PortfolioController.class);
    }

    @AfterEach
    void afterEach() {
        logCaptor.close();
    }

    @Test
    @DisplayName("Should get the Portfolio")
    void shouldGetThePortfolio() throws Exception {

        given(loadPortfolioPortIn.load()).willReturn(Fixture.cryptoState);

        mockMvc.perform(get("/portfolio"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().stringValues(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
                .andExpect(content().json(
                        "{" +
                                "   \"currency\":\"USD\"," +
                                "   \"totalAmountInvested\":1200.0," +
                                "   \"cryptos\":[" +
                                "       {\"symbol\":\"USDT\",\"amountInvested\":200.0}," +
                                "       {\"symbol\":\"BTC\",\"amountInvested\":1000.0}" +
                                "    ]" +
                                "}"));

        assertThat(logCaptor.getLogs())
                .hasSize(1);
        assertThat(logCaptor.getInfoLogs())
                .hasSize(1)
                .containsExactly("portfolio called");
    }

    @Test
    @DisplayName("Should get the Portfolio with Error")
    void shouldGetThePortfolioWithError() throws Exception {

        given(loadPortfolioPortIn.load()).willThrow(new IllegalStateException("Cannot get database"));

        mockMvc.perform(get("/portfolio"))
                .andDo(print())
                .andExpect(status().isServiceUnavailable())
                .andExpect(header().stringValues(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
                .andExpect(content().json(
                        "{" +
                                "   \"status\":\"SERVICE_UNAVAILABLE\"," +
                                "   \"error\":\"Service Error\"," +
                                "   \"message\":\"Cannot get database\"," +
                                "   \"path\":\"/portfolio\"" +
                                "}"
                        ));

        assertThat(logCaptor.getLogs())
                .hasSize(1);
        assertThat(logCaptor.getInfoLogs())
                .hasSize(1)
                .containsExactly("portfolio called");
    }

    @Test
    @DisplayName("Should Update Crypto")
    void shouldUpdateCrypto() throws Exception {
        given(loadPortfolioPortIn.load()).willReturn(Fixture.cryptoState);

        mockMvc.perform(patch("/portfolio")
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                        .content("{" +
                                 "  \"currency\":\"USD\"," +
                                 "  \"cryptosToUpdate\": [" +
                                 "      {\"symbol\": \"BTC\", \"amountInvested\": 20.5}," +
                                 "      {\"symbol\": \"ETH\", \"amountInvested\": 10}" +
                                 "    ]," +
                                 "  \"cryptosToRemove\": [\"DOGE\", \"DOT\"]" +
                                 "}"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().stringValues(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
                .andExpect(content().json(
                        "{" +
                                "   \"currency\":\"USD\"," +
                                "   \"totalAmountInvested\":1200.0," +
                                "   \"cryptos\":[" +
                                "       {\"symbol\":\"USDT\",\"amountInvested\":200.0}," +
                                "       {\"symbol\":\"BTC\",\"amountInvested\":1000.0}" +
                                "    ]" +
                                "}"));

        then(updatePortfolioPortIn)
                .should(times(1))
                .update(
                        eq(Set.of(
                            Mapper.fromCrypto(Crypto.builder().symbol("BTC").amountInvested(20.5).build()),
                            Mapper.fromCrypto(Crypto.builder().symbol("ETH").amountInvested(10).build()))),
                        eq(Set.of("DOGE", "DOT")));

        assertThat(logCaptor.getLogs())
                .hasSize(1);
        assertThat(logCaptor.getInfoLogs())
                .hasSize(1)
                .containsExactly("update portfolio with values:UpdatePortfolio(currency=USD, " +
                        "cryptosToUpdate=[Crypto(symbol=BTC, amountInvested=20.5), " +
                        "Crypto(symbol=ETH, amountInvested=10.0)], cryptosToRemove=[DOT, DOGE])");
    }

    @Test
    @DisplayName("Should Update Crypto Without Cryptos to Remove")
    void shouldUpdateCryptoWithoutCryptosToRemove() throws Exception {
        given(loadPortfolioPortIn.load()).willReturn(Fixture.cryptoState);

        mockMvc.perform(patch("/portfolio")
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                        .content("{" +
                                "  \"currency\":\"USD\"," +
                                "  \"cryptosToUpdate\": [" +
                                "      {\"symbol\": \"BTC\", \"amountInvested\": 20.5}," +
                                "      {\"symbol\": \"ETH\", \"amountInvested\": 10}" +
                                "    ]" +
                                "}"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().stringValues(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
                .andExpect(content().json(
                        "{" +
                                "   \"currency\":\"USD\"," +
                                "   \"totalAmountInvested\":1200.0," +
                                "   \"cryptos\":[" +
                                "       {\"symbol\":\"USDT\",\"amountInvested\":200.0}," +
                                "       {\"symbol\":\"BTC\",\"amountInvested\":1000.0}" +
                                "    ]" +
                                "}"));

        then(updatePortfolioPortIn)
                .should(times(1))
                .update(
                        eq(Set.of(
                                Mapper.fromCrypto(Crypto.builder().symbol("BTC").amountInvested(20.5).build()),
                                Mapper.fromCrypto(Crypto.builder().symbol("ETH").amountInvested(10).build()))),
                        eq(Set.of()));

        assertThat(logCaptor.getLogs())
                .hasSize(1);
        assertThat(logCaptor.getInfoLogs())
                .hasSize(1)
                .containsExactly("update portfolio with values:UpdatePortfolio(currency=USD, " +
                        "cryptosToUpdate=[Crypto(symbol=BTC, amountInvested=20.5), " +
                        "Crypto(symbol=ETH, amountInvested=10.0)], cryptosToRemove=null)");
    }

    @Test
    @DisplayName("Should Update Cryptos Without Cryptos To Update")
    void shouldUpdateCryptoWithoutCryptosToUpdate() throws Exception {
        given(loadPortfolioPortIn.load()).willReturn(Fixture.cryptoState);

        mockMvc.perform(patch("/portfolio")
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                        .content("{" +
                                "  \"currency\":\"USD\"," +
                                "  \"cryptosToRemove\": [\"DOGE\", \"DOT\"]" +
                                "}"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().stringValues(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
                .andExpect(content().json(
                        "{" +
                                "   \"currency\":\"USD\"," +
                                "   \"totalAmountInvested\":1200.0," +
                                "   \"cryptos\":[" +
                                "       {\"symbol\":\"USDT\",\"amountInvested\":200.0}," +
                                "       {\"symbol\":\"BTC\",\"amountInvested\":1000.0}" +
                                "    ]" +
                                "}"));

        then(updatePortfolioPortIn)
                .should(times(1))
                .update(
                        eq(Set.of()),
                        eq(Set.of("DOGE", "DOT")));

        assertThat(logCaptor.getLogs())
                .hasSize(1);
        assertThat(logCaptor.getInfoLogs())
                .hasSize(1)
                .containsExactly("update portfolio with values:UpdatePortfolio(currency=USD, " +
                        "cryptosToUpdate=null, cryptosToRemove=[DOT, DOGE])");
    }

    @Test
    @DisplayName("Update With Error on Update DB Service")
    void shouldUpdateErrorOnUpdateService() throws Exception {
        willThrow(new IllegalStateException("DB Error"))
                .given(updatePortfolioPortIn).update(eq(Set.of()), eq(Set.of("DOGE", "DOT")));

        mockMvc.perform(patch("/portfolio")
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                        .content("{" +
                                "  \"currency\":\"USD\"," +
                                "  \"cryptosToRemove\": [\"DOGE\", \"DOT\"]" +
                                "}"))
                .andDo(print())
                .andExpect(status().isServiceUnavailable())
                .andExpect(header().stringValues(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
                .andExpect(content().json(
                        "{" +
                                "  \"status\":\"SERVICE_UNAVAILABLE\"," +
                                "  \"error\":\"Service Error\"," +
                                "  \"message\":\"DB Error\"," +
                                "  \"path\":\"/portfolio\"" +
                                "}"
                ));

        assertThat(logCaptor.getLogs())
                .hasSize(1);
        assertThat(logCaptor.getInfoLogs())
                .hasSize(1)
                .containsExactly("update portfolio with values:UpdatePortfolio(currency=USD, " +
                        "cryptosToUpdate=null, cryptosToRemove=[DOT, DOGE])");
    }

    @Test
    @DisplayName("Update With Error on Load DB Service")
    void shouldUpdateErrorOnLoad() throws Exception {
        given(loadPortfolioPortIn.load()).willThrow(new IllegalStateException("Cannot get database"));

        mockMvc.perform(patch("/portfolio")
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                        .content("{" +
                                "  \"currency\":\"USD\"," +
                                "  \"cryptosToRemove\": [\"DOGE\", \"DOT\"]" +
                                "}"))
                .andDo(print())
                .andExpect(status().isServiceUnavailable())
                .andExpect(header().stringValues(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
                .andExpect(content().json(
                        "{" +
                                "  \"status\":\"SERVICE_UNAVAILABLE\"," +
                                "  \"error\":\"Service Error\"," +
                                "  \"message\":\"Cannot get database\"," +
                                "  \"path\":\"/portfolio\"" +
                                "}"
                ));

        assertThat(logCaptor.getLogs())
                .hasSize(1);
        assertThat(logCaptor.getInfoLogs())
                .hasSize(1)
                .containsExactly("update portfolio with values:UpdatePortfolio(currency=USD, " +
                        "cryptosToUpdate=null, cryptosToRemove=[DOT, DOGE])");
    }

    @Test
    @DisplayName("Update With Error on ValidationException")
    void shouldUpdateErrorOnValidationException() throws Exception {
        mockMvc.perform(patch("/portfolio")
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                        .content("{" +
                                "  \"currency\":\"NONE\"," +
                                "  \"cryptosToRemove\": [\"DOGE\", \"DOT\"]" +
                                "}"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(header().stringValues(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
                .andExpect(content().json(
                        "{" +
                                "  \"status\":\"BAD_REQUEST\"," +
                                "  \"error\":\"Validation Error\"," +
                                "  \"message\":\"currency must be USD value: NONE\"," +
                                "  \"path\":\"/portfolio\"" +
                                "}"
                ));


        then(updatePortfolioPortIn).should(times(0)).update(anySet(), anySet());
        then(loadPortfolioPortIn).should(times(0)).load();

        assertThat(logCaptor.getLogs())
                .hasSize(1);
        assertThat(logCaptor.getInfoLogs())
                .hasSize(1)
                .containsExactly("update portfolio with values:UpdatePortfolio(currency=NONE, " +
                        "cryptosToUpdate=null, cryptosToRemove=[DOT, DOGE])");
    }
}