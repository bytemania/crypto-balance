package com.github.bytemania.adapter.in.web.server;

import com.github.bytemania.adapter.in.web.server.impl.AllocationControllerConfigImpl;
import com.github.bytemania.cryptobalance.domain.dto.Crypto;
import com.github.bytemania.cryptobalance.domain.util.Util;
import com.github.bytemania.cryptobalance.port.in.CreateAllocation;
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

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AllocationController.class)
@ContextConfiguration(classes = {
        AllocationController.class,
        AllocationControllerConfig.class,
        AllocationControllerConfigImpl.class,
        GlobalExceptionHandler.class
})
class AllocationControllerItTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CreateAllocation createAllocation;

    @Test
    @DisplayName("Should return Allocation From Service")
    void shouldReturnAllocationFromService() throws Throwable {
        var stableCoin = Crypto.of("USDT", 20.0, true);
        given(createAllocation.allocate(eq(stableCoin), eq(Util.normalize(BigDecimal.valueOf(21))),
                eq(Util.normalize(BigDecimal.ONE))))
                .willReturn(Fixture.allocationResult);

        String uri = "/allocate?" +
                "stableCryptoSymbol=USDT&" +
                "stableCryptoPercentage=20&" +
                "valueToInvest=21&" +
                "minValueToAllocate=1";

        mockMvc.perform(get(uri))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().stringValues(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
                .andExpect(content().json("{" +
                        "\"currency\":\"USD\"," +
                        "\"amountToInvest\":21.0," +
                        "\"rest\":1.0," +
                        "\"amountInvested\":80.0," +
                        "\"stableCrypto\":{" +
                        "   \"symbol\":\"USDT\"," +
                        "   \"marketCapPercentage\":20.0," +
                        "   \"amountToInvest\":20.0," +
                        "   \"operation\":\"KEEP\"," +
                        "   \"rebalanceToInvest\":0.0," +
                        "   \"currentInvested\":20.0}," +
                        "\"cryptos\":[" +
                        "   {" +
                        "       \"symbol\":\"BTC\"," +
                        "       \"marketCapPercentage\":60.0," +
                        "       \"amountToInvest\":60.0," +
                        "       \"operation\":\"BUY\"," +
                        "       \"rebalanceToInvest\":20.0," +
                        "       \"currentInvested\":40.0" +
                        "   },{" +
                        "       \"symbol\":\"ADA\"," +
                        "       \"marketCapPercentage\":20.0," +
                        "       \"amountToInvest\":20.0," +
                        "       \"operation\":\"KEEP\"," +
                        "       \"rebalanceToInvest\":0.0," +
                        "       \"currentInvested\":20.0}]" +
                        "   }\n"));
    }

    @Test
    @DisplayName("Should process A ValidationError")
    void shouldProcessAValidationError() throws Throwable {
        var stableCoin = Crypto.of("USDT", 20.0, true);
        given(createAllocation.allocate(eq(stableCoin), eq(Util.normalize(BigDecimal.valueOf(21))),
                eq(Util.normalize(BigDecimal.ONE))))
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
    }

    @Test
    @DisplayName("Should process A IllegalStateException")
    void shouldProcessIllegalStateException() throws Throwable {
        var stableCoin = Crypto.of("USDT", 20.0, true);
        given(createAllocation.allocate(eq(stableCoin), eq(Util.normalize(BigDecimal.valueOf(21))),
                eq(Util.normalize(BigDecimal.ONE))))
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
    }

    @Test
    @DisplayName("Should process An UnexpectedError")
    void shouldProcessAnUnexpectedError() throws Throwable {
        var stableCoin = Crypto.of("USDT", 20.0, true);
        given(createAllocation.allocate(eq(stableCoin), eq(Util.normalize(BigDecimal.valueOf(21))),
                eq(Util.normalize(BigDecimal.ONE))))
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
    }

}