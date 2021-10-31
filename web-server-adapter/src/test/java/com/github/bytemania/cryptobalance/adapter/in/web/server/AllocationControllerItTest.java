package com.github.bytemania.cryptobalance.adapter.in.web.server;

import com.github.bytemania.cryptobalance.adapter.in.web.server.impl.ControllerConfigImpl;
import com.github.bytemania.cryptobalance.domain.dto.Crypto;
import com.github.bytemania.cryptobalance.port.in.CreateAllocationPortIn;
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

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AllocationController.class)
@ContextConfiguration(classes = {
        AllocationController.class,
        ControllerConfig.class,
        ControllerConfigImpl.class,
        GlobalExceptionHandler.class
})
class AllocationControllerItTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CreateAllocationPortIn createAllocationPortIn;

    private LogCaptor logCaptor;

    @BeforeEach
    void beforeEach() {
        logCaptor = LogCaptor.forClass(AllocationController.class);
    }

    @AfterEach
    void afterEach() {
        logCaptor.close();
    }

    @Test
    @DisplayName("Should return Allocation From Service")
    void shouldReturnAllocationFromService() throws Throwable {
        var stableCoin = new Crypto("BUSD", 20.0, BigDecimal.ZERO, true);
        given(createAllocationPortIn.allocate(eq(stableCoin), eq(BigDecimal.valueOf(21.0)),
                eq(BigDecimal.valueOf(1.0))))
                .willReturn(Fixture.allocationResult);

        String uri = "/allocate?" +
                "stableCryptoSymbol=BUSD&" +
                "stableCryptoPercentage=20&" +
                "valueToInvest=21&" +
                "minValueToAllocate=1";

        mockMvc.perform(get(uri))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().stringValues(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
                .andExpect(content().json(
                        "{" +
                        "        \"currency\":\"USD\"," +
                        "        \"amountToInvest\":200.00," +
                        "        \"holdings\":1201.00," +
                        "        \"totalInvested\":1200.00," +
                        "        \"rest\":1.00," +
                        "        \"minValueToAllocate\":25.00," +
                        "        \"stableCrypto\":{" +
                        "            \"symbol\":\"BUSD\"," +
                        "            \"price\":1.00," +
                        "            \"holding\":121.00," +
                        "            \"invested\":200.00," +
                        "            \"marketCapPercentage\":20.0," +
                        "            \"rebalance\":21.00" +
                        "       }," +
                        "        \"cryptos\":" +
                        "           [" +
                        "               {" +
                        "                    \"symbol\":\"BTC\"," +
                        "                    \"price\":65000.00," +
                        "                    \"holding\":0.00," +
                        "                    \"invested\":650.00, " +
                        "                    \"marketCapPercentage\":55.0," +
                        "                    \"rebalance\":20.00" +
                        "               }," +
                        "               {" +
                        "                    \"symbol\":\"ADA\"," +
                        "                    \"price\":2.00," +
                        "                    \"holding\":220.00," +
                        "                    \"invested\":450.00," +
                        "                    \"marketCapPercentage\":28.0," +
                        "                    \"rebalance\":-40.00" +
                        "               }," +
                        "               {" +
                        "                    \"symbol\":\"DOGE\"," +
                        "                    \"price\":0.20," +
                        "                    \"holding\":10.00," +
                        "                    \"invested\":100.00," +
                        "                    \"marketCapPercentage\":15.0," +
                        "                    \"rebalance\":30.00" +
                        "               }" +
                        "           ]" +
                        "}"
                ));

        assertThat(logCaptor.getLogs())
                .hasSize(1);
        assertThat(logCaptor.getInfoLogs())
                .hasSize(1)
                .containsExactly("allocate called with stableCryptoSymbol:BUSD, stableCryptoPercentage:20.0, valueToInvest:21.0, minValueToAllocate:1.0");
    }

    @Test
    @DisplayName("Should process A ValidationError")
    void shouldProcessAValidationError() throws Throwable {
        var stableCoin = new Crypto("USDT", 20.0, BigDecimal.ZERO, true);
        given(createAllocationPortIn.allocate(eq(stableCoin), eq(BigDecimal.valueOf(21)),
                eq(BigDecimal.ONE)))
                .willReturn(Fixture.allocationResult);

        String uri = "/allocate?" +
                "stableCryptoSymbol=USDT&" +
                "stableCryptoPercentage=20&" +
                "valueToInvest=21&" +
                "minValueToAllocate=100";

        mockMvc.perform(get(uri))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(header().stringValues(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
                .andExpect(content().json("{" +
                        "\"status\":\"BAD_REQUEST\"," +
                        "\"error\":\"Validation Error\"," +
                        "\"message\":\"minValueToAllocate must be <= valueToInvest value: 100.0 valueToInvest: 21.0\"," +
                        "\"path\":\"/allocate\"}\n"));

        assertThat(logCaptor.getLogs())
                .hasSize(1);
        assertThat(logCaptor.getInfoLogs())
                .hasSize(1)
                .containsExactly("allocate called with stableCryptoSymbol:USDT, stableCryptoPercentage:20.0, valueToInvest:21.0, minValueToAllocate:100.0");
    }

    @Test
    @DisplayName("Should process A IllegalStateException")
    void shouldProcessIllegalStateException() throws Throwable {
        var stableCoin = new Crypto("USDT", 20.0, BigDecimal.ZERO, true);
        given(createAllocationPortIn.allocate(eq(stableCoin), eq(BigDecimal.valueOf(21.0)),
                eq(BigDecimal.valueOf(1.0))))
                .willThrow(new IllegalStateException("Invalid State"));

        String uri = "/allocate?" +
                "stableCryptoSymbol=USDT&" +
                "stableCryptoPercentage=20&" +
                "valueToInvest=21&" +
                "minValueToAllocate=1";

        mockMvc.perform(get(uri))
                .andDo(print())
                .andExpect(status().isServiceUnavailable())
                .andExpect(header().stringValues(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
                .andExpect(content().json("{" +
                        "\"status\":\"SERVICE_UNAVAILABLE\"," +
                        "\"error\":\"Service Error\"," +
                        "\"message\":\"Invalid State\"," +
                        "\"path\":\"/allocate\"}\n"));

        assertThat(logCaptor.getLogs())
                .hasSize(1);
        assertThat(logCaptor.getInfoLogs())
                .hasSize(1)
                .containsExactly("allocate called with stableCryptoSymbol:USDT, stableCryptoPercentage:20.0, valueToInvest:21.0, minValueToAllocate:1.0");
    }

    @Test
    @DisplayName("Should process An UnexpectedError")
    void shouldProcessAnUnexpectedError() throws Throwable {
        var stableCoin = new Crypto("USDT", 20.0, BigDecimal.ZERO, true);
        given(createAllocationPortIn.allocate(eq(stableCoin), eq(BigDecimal.valueOf(21.0)),
                eq(BigDecimal.valueOf(1.0))))
                .willThrow(new RuntimeException("Runtime Error"));

        String uri = "/allocate?" +
                "stableCryptoSymbol=USDT&" +
                "stableCryptoPercentage=20&" +
                "valueToInvest=21&" +
                "minValueToAllocate=1";

        mockMvc.perform(get(uri))
                .andDo(print())
                .andExpect(status().isInternalServerError())
                .andExpect(header().stringValues(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
                .andExpect(content().json("{" +
                        "\"status\":\"INTERNAL_SERVER_ERROR\"," +
                        "\"error\":\"Server Error\"," +
                        "\"message\":\"Runtime Error\"," +
                        "\"path\":\"/allocate\"}\n"));

        assertThat(logCaptor.getLogs())
                .hasSize(1);
        assertThat(logCaptor.getInfoLogs())
                .hasSize(1)
                .containsExactly("allocate called with stableCryptoSymbol:USDT, stableCryptoPercentage:20.0, valueToInvest:21.0, minValueToAllocate:1.0");
    }

    @Test
    @DisplayName("Should process A Bad Request")
    void shouldProcessABadRequest() throws Throwable {
        String uri = "/allocate";

        mockMvc.perform(get(uri))
                .andDo(print())
                .andExpect(status().isInternalServerError())
                .andExpect(header().stringValues(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
                .andExpect(content().json("{" +
                        "\"status\":\"INTERNAL_SERVER_ERROR\"," +
                        "\"error\":\"Server Error\"," +
                        "\"message\":\"Required request parameter 'stableCryptoSymbol' for method parameter type String is not present\"," +
                        "\"path\":\"/allocate\"}\n"));

        assertThat(logCaptor.getLogs()).isEmpty();
    }

}