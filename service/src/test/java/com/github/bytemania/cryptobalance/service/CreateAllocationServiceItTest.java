package com.github.bytemania.cryptobalance.service;

import com.github.bytemania.cryptobalance.domain.dto.AllocationResult;
import com.github.bytemania.cryptobalance.domain.dto.Crypto;
import com.github.bytemania.cryptobalance.domain.dto.CryptoState;
import com.github.bytemania.cryptobalance.port.in.CreateAllocation;
import com.github.bytemania.cryptobalance.port.out.LoadCoinMarketCapPort;
import com.github.bytemania.cryptobalance.port.out.LoadPortfolioPort;
import nl.altindag.log.LogCaptor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@SpringBootTest(classes = {CreateAllocationService.class, LoadCoinMarketCapPort.class, LoadPortfolioPort.class})
class CreateAllocationServiceItTest {

    @MockBean
    private LoadCoinMarketCapPort loadCoinMarketCapPort;
    @MockBean
    private LoadPortfolioPort loadPortfolioPort;

    @Autowired
    private CreateAllocation createAllocation;

    private LogCaptor logCaptor;

    @BeforeEach
    void beforeEach() {
        logCaptor = LogCaptor.forClass(CreateAllocationService.class);
    }

    @AfterEach
    void afterEach() throws IOException {
        logCaptor.close();
    }

    @Test
    @DisplayName("Should Create Allocation")
    void shouldCreateAllocation() {

        var coinMarketCap = List.of(Crypto.of("BTC", 55.57, false));
        var state = List.of(CryptoState.of("BTC", BigDecimal.valueOf(100)));

        given(loadCoinMarketCapPort.load()).willReturn(coinMarketCap);
        given(loadPortfolioPort.load()).willReturn(state);

        var stableCrypto = Crypto.of("BUSD", 20.0, true);
        var amountToInvest = BigDecimal.valueOf(100);
        var minAmountToAllocate = BigDecimal.valueOf(25);

        AllocationResult result = createAllocation.allocate(stableCrypto, amountToInvest, minAmountToAllocate);

        assertThat(result).isNotNull();

        assertThat(logCaptor.getLogs())
                .hasSize(1);
        assertThat(logCaptor.getInfoLogs())
                .hasSize(1)
                .containsExactly("Service Called for crypto " +
                        "stableCoin=Crypto(symbol=BUSD, marketCapPercentage=20.0, stableCoin=true), " +
                        "amountToInvest=100, " +
                        "minAmountToAllocate=100");
    }

    @Test
    @DisplayName("Should Fail if CoinMarketCapFails")
    void shouldFailIfCoinMarketCapFail() {
        given(loadCoinMarketCapPort.load()).willThrow(new IllegalStateException("Cannot connect CoinMarketCap"));

        var stableCrypto = Crypto.of("BUSD", 20.0, true);
        var amountToInvest = BigDecimal.valueOf(100);
        var minAmountToAllocate = BigDecimal.valueOf(25);

        assertThatThrownBy(() -> createAllocation.allocate(stableCrypto, amountToInvest, minAmountToAllocate))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Cannot connect CoinMarketCap");

        assertThat(logCaptor.getLogs())
                .hasSize(1);
        assertThat(logCaptor.getInfoLogs())
                .hasSize(1)
                .containsExactly("Service Called for crypto " +
                        "stableCoin=Crypto(symbol=BUSD, marketCapPercentage=20.0, stableCoin=true), " +
                        "amountToInvest=100, " +
                        "minAmountToAllocate=100");
    }

    @Test
    @DisplayName("Should Fail if Portfolio Fails")
    void shouldFailIfPortfolioFails() {

        var coinMarketCap = List.of(Crypto.of("BTC", 55.57, false));

        given(loadCoinMarketCapPort.load()).willReturn(coinMarketCap);
        given(loadPortfolioPort.load()).willThrow(new IllegalStateException("Cannot Connect Portfolio"));

        var stableCrypto = Crypto.of("BUSD", 20.0, true);
        var amountToInvest = BigDecimal.valueOf(100);
        var minAmountToAllocate = BigDecimal.valueOf(25);

        assertThatThrownBy(() -> createAllocation.allocate(stableCrypto, amountToInvest, minAmountToAllocate))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Cannot Connect Portfolio");

        assertThat(logCaptor.getLogs())
                .hasSize(1);
        assertThat(logCaptor.getInfoLogs())
                .hasSize(1)
                .containsExactly("Service Called for crypto " +
                        "stableCoin=Crypto(symbol=BUSD, marketCapPercentage=20.0, stableCoin=true), " +
                        "amountToInvest=100, " +
                        "minAmountToAllocate=100");
    }
}