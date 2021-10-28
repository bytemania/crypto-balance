package com.github.bytemania.cryptobalance.service;

import com.github.bytemania.cryptobalance.domain.dto.CryptoState;
import com.github.bytemania.cryptobalance.port.out.LoadPortfolioPortOut;
import com.github.bytemania.cryptobalance.port.out.RemovePortfolioPortOut;
import com.github.bytemania.cryptobalance.port.out.UpdatePortfolioPortOut;
import nl.altindag.log.LogCaptor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.times;

@SpringBootTest(classes = {PortfolioService.class, UpdatePortfolioPortOut.class, RemovePortfolioPortOut.class})
class PortfolioServiceItTest {

    @MockBean
    private LoadPortfolioPortOut loadPortfolioPortOut;

    @MockBean
    private UpdatePortfolioPortOut updatePortfolioPortOut;

    @MockBean
    private RemovePortfolioPortOut removePortfolioPortOut;

    @Autowired
    private PortfolioService portfolioService;

    private LogCaptor logCaptor;

    @BeforeEach
    void beforeEach() {
        logCaptor = LogCaptor.forClass(PortfolioService.class);
    }

    @AfterEach
    void afterEach() {
        logCaptor.close();
    }

    @Test
    @DisplayName("Should load the Portfolio")
    void shouldLoadThePortfolio() {
        var cryptos = List.of(CryptoState.of("ETH", BigDecimal.TEN));

        given(loadPortfolioPortOut.load()).willReturn(cryptos);

        var result = portfolioService.load();
        assertThat(result).isSameAs(cryptos);

        then(loadPortfolioPortOut).should(times(1)).load();

        assertThat(logCaptor.getLogs())
                .hasSize(1);
        assertThat(logCaptor.getInfoLogs())
                .hasSize(1)
                .containsExactly("Portfolio Service load");
    }

    @Test
    @DisplayName("Should process Error on load the Portfolio")
    void shouldProcessErrorOnLoadThePortfolio() {
        var cryptos = List.of(CryptoState.of("ETH", BigDecimal.TEN));

        given(loadPortfolioPortOut.load()).willThrow(new IllegalStateException("DB ERROR"));

        assertThatThrownBy(() -> portfolioService.load())
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("DB ERROR");

        assertThat(logCaptor.getLogs())
                .hasSize(1);
        assertThat(logCaptor.getInfoLogs())
                .hasSize(1)
                .containsExactly("Portfolio Service load");
    }

    @Test
    @DisplayName("Should update the Portfolio")
    void shouldUpdateThePortfolio() {
        var cryptosToUpdate = Set.of(CryptoState.of("BTC", BigDecimal.ONE),
                CryptoState.of("ADA", BigDecimal.TEN));

        var cryptosToRemove = Set.of("DOGE", "ETH");

        portfolioService.update(cryptosToUpdate, cryptosToRemove);

        then(updatePortfolioPortOut).should(times(1)).update(cryptosToUpdate);
        then(removePortfolioPortOut).should(times(1)).remove(cryptosToRemove);

        assertThat(logCaptor.getLogs())
                .hasSize(1);
        assertThat(logCaptor.getInfoLogs())
                .hasSize(1);
        assertThat(logCaptor.getInfoLogs().get(0))
                .contains("Portfolio Service Collection cryptosToUpdate")
                .contains("CryptoState(symbol=ADA, invested=10)")
                .contains("CryptoState(symbol=BTC, invested=1)")
                .contains("ETH")
                .contains("DOGE")
                .hasSize(149);
    }

    @Test
    @DisplayName("Should fail UpdatePortfolio Fails")
    void shouldFailIfUpdatePortfolioFails() {
        var cryptosToUpdate = Set.of(CryptoState.of("BTC", BigDecimal.ONE),
                CryptoState.of("ADA", BigDecimal.TEN));

        var cryptosToRemove = Set.of("DOGE", "ETH");

        willThrow(new IllegalStateException("DB ERROR")).given(updatePortfolioPortOut).update(cryptosToUpdate);

        assertThatThrownBy(() -> portfolioService.update(cryptosToUpdate, cryptosToRemove))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("DB ERROR");

        then(removePortfolioPortOut).should(times(0)).remove(cryptosToRemove);

        assertThat(logCaptor.getLogs())
                .hasSize(1);
        assertThat(logCaptor.getInfoLogs())
                .hasSize(1);
        assertThat(logCaptor.getInfoLogs().get(0))
                .contains("Portfolio Service Collection cryptosToUpdate")
                .contains("CryptoState(symbol=ADA, invested=10)")
                .contains("CryptoState(symbol=BTC, invested=1)")
                .contains("ETH")
                .contains("DOGE")
                .hasSize(149);
    }

    @Test
    @DisplayName("Should fail RemovePortfolio Fails")
    void shouldFailIfRemovePortfolioFails() {
        var cryptosToUpdate = Set.of(CryptoState.of("BTC", BigDecimal.ONE),
                CryptoState.of("ADA", BigDecimal.TEN));

        var cryptosToRemove = Set.of("DOGE", "ETH");

        willThrow(new IllegalStateException("DB ERROR")).given(removePortfolioPortOut).remove(cryptosToRemove);

        assertThatThrownBy(() -> portfolioService.update(cryptosToUpdate, cryptosToRemove))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("DB ERROR");

        then(updatePortfolioPortOut).should(times(1)).update(cryptosToUpdate);

        assertThat(logCaptor.getLogs())
                .hasSize(1);
        assertThat(logCaptor.getInfoLogs())
                .hasSize(1);
        assertThat(logCaptor.getInfoLogs().get(0))
                .contains("Portfolio Service Collection cryptosToUpdate")
                .contains("CryptoState(symbol=ADA, invested=10)")
                .contains("CryptoState(symbol=BTC, invested=1)")
                .contains("ETH")
                .contains("DOGE")
                .hasSize(149);
    }

}
