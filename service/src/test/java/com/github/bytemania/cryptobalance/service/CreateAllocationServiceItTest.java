package com.github.bytemania.cryptobalance.service;

import com.github.bytemania.cryptobalance.domain.dto.AllocationResult;
import com.github.bytemania.cryptobalance.domain.dto.Crypto;
import com.github.bytemania.cryptobalance.domain.dto.CryptoState;
import com.github.bytemania.cryptobalance.port.in.CreateAllocationPortIn;
import com.github.bytemania.cryptobalance.port.out.LoadCoinMarketCapPortOut;
import com.github.bytemania.cryptobalance.port.out.LoadPortfolioPortOut;
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
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@SpringBootTest(classes = {CreateAllocationService.class, LoadCoinMarketCapPortOut.class, LoadPortfolioPortOut.class})
class CreateAllocationServiceItTest {

    @MockBean
    private LoadCoinMarketCapPortOut loadCoinMarketCapPortOut;
    @MockBean
    private LoadPortfolioPortOut loadPortfolioPortOut;

    @Autowired
    private CreateAllocationPortIn createAllocationPortIn;

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

        var coinMarketCap = Set.of(new Crypto("BTC", 55.57, BigDecimal.ONE, false));
        var state = Set.of(new CryptoState("BTC", BigDecimal.ONE, BigDecimal.valueOf(100)));

        given(loadCoinMarketCapPortOut.load()).willReturn(coinMarketCap);
        given(loadPortfolioPortOut.load()).willReturn(state);

        var stableCrypto = new Crypto("BUSD", 20.0, BigDecimal.ZERO, true);
        var amountToInvest = BigDecimal.valueOf(100);
        var minAmountToAllocate = BigDecimal.valueOf(25);

        AllocationResult result = createAllocationPortIn.allocate(stableCrypto, amountToInvest, minAmountToAllocate);

        assertThat(result).isNotNull();

        assertThat(logCaptor.getLogs())
                .hasSize(1);
        assertThat(logCaptor.getInfoLogs())
                .hasSize(1)
                .containsExactly("Service Called for crypto stableCoin=Crypto(symbol=BUSD, marketCapPercentage=20.0, " +
                        "price=0, stableCoin=true), amountToInvest=100, minAmountToAllocate=100");
    }

    @Test
    @DisplayName("Should Fail if CoinMarketCapFails")
    void shouldFailIfCoinMarketCapFail() {
        given(loadCoinMarketCapPortOut.load()).willThrow(new IllegalStateException("Cannot connect CoinMarketCap"));

        var stableCrypto = new Crypto("BUSD", 20.0, BigDecimal.ZERO, true);
        var amountToInvest = BigDecimal.valueOf(100);
        var minAmountToAllocate = BigDecimal.valueOf(25);

        assertThatThrownBy(() -> createAllocationPortIn.allocate(stableCrypto, amountToInvest, minAmountToAllocate))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Cannot connect CoinMarketCap");

        assertThat(logCaptor.getLogs())
                .hasSize(1);
        assertThat(logCaptor.getInfoLogs())
                .hasSize(1)
                .containsExactly("Service Called for crypto stableCoin=Crypto(symbol=BUSD, " +
                        "marketCapPercentage=20.0, price=0, stableCoin=true), amountToInvest=100, " +
                        "minAmountToAllocate=100");
    }

    @Test
    @DisplayName("Should Fail if Portfolio Fails")
    void shouldFailIfPortfolioFails() {

        var coinMarketCap = List.of(new Crypto("BTC", 55.57, BigDecimal.ZERO, false));

        given(loadCoinMarketCapPortOut.load()).willReturn(null);
        given(loadPortfolioPortOut.load()).willThrow(new IllegalStateException("Cannot Connect Portfolio"));

        var stableCrypto = new Crypto("BUSD", 20.0, BigDecimal.ZERO, true);
        var amountToInvest = BigDecimal.valueOf(100);
        var minAmountToAllocate = BigDecimal.valueOf(25);

        assertThatThrownBy(() -> createAllocationPortIn.allocate(stableCrypto, amountToInvest, minAmountToAllocate))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Cannot Connect Portfolio");

        assertThat(logCaptor.getLogs())
                .hasSize(1);
        assertThat(logCaptor.getInfoLogs())
                .hasSize(1)
                .containsExactly("Service Called for crypto stableCoin=Crypto(symbol=BUSD, " +
                        "marketCapPercentage=20.0, price=0, stableCoin=true), amountToInvest=100, " +
                        "minAmountToAllocate=100");
    }
}