package com.github.bytemania.cryptobalance.adapter.in.web.server.impl;

import com.github.bytemania.cryptobalance.adapter.in.web.server.dto.portfolio.Crypto;
import com.github.bytemania.cryptobalance.adapter.in.web.server.dto.portfolio.UpdatePortfolio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ValidationTest {

    @Test
    @DisplayName("Should validate the parameters")
    void shouldValidateTheParameters() throws ValidationException {
        Validation.allocation(0.0, 0, 0);
        Validation.allocation(100.0, 1, 0);
        Validation.allocation(20.0, 200, 25);

        assertThatThrownBy(() -> Validation.allocation(-1.0, 0, 0))
                .isInstanceOf(ValidationException.class)
                .hasMessage("stableCryptoPercentage must be [0..100] value: -1.0");

        assertThatThrownBy(() -> Validation.allocation(100.001, 0, 0))
                .isInstanceOf(ValidationException.class)
                .hasMessage("stableCryptoPercentage must be [0..100] value: 100.001");

        assertThatThrownBy(() -> Validation.allocation(50, -1.0, 0))
                .isInstanceOf(ValidationException.class)
                .hasMessage("valueToInvest must be >= 0 value: -1.0");

        assertThatThrownBy(() -> Validation.allocation(50, 1.0, -1.0))
                .isInstanceOf(ValidationException.class)
                .hasMessage("minValueToAllocate must be >= 0 value: -1.0");

        assertThatThrownBy(() -> Validation.allocation(50, 10.0, 100.0))
                .isInstanceOf(ValidationException.class)
                .hasMessage("minValueToAllocate must be <= valueToInvest value: 100.0 valueToInvest: 10.0");
    }

    @Test
    @DisplayName("Should validate the updateCrypto")
    void shouldValidateTheUpdateCrypto() throws ValidationException {
        assertThatThrownBy(() -> Validation.updatePortfolio("usd", UpdatePortfolio.builder().build()))
                .isInstanceOf(ValidationException.class)
                .hasMessage("currency must be usd value: null");

        assertThatThrownBy(() -> Validation
                .updatePortfolio("usd", UpdatePortfolio.builder().currency("eur").build()))
                .isInstanceOf(ValidationException.class)
                .hasMessage("currency must be usd value: eur");

        assertThatThrownBy(() ->
                Validation.updatePortfolio("usd", UpdatePortfolio.builder().currency("USD").build()))
                .isInstanceOf(ValidationException.class)
                .hasMessage("You should have at least one crypto to update or remove");

        assertThatThrownBy(() ->
                Validation.updatePortfolio("usd",
                        UpdatePortfolio.builder()
                                .currency("USD")
                                .cryptosToUpdate(Set.of())
                                .build()))
                .isInstanceOf(ValidationException.class)
                .hasMessage("You should have at least one crypto to update or remove");

        assertThatThrownBy(() ->
                Validation.updatePortfolio("usd",
                        UpdatePortfolio.builder()
                                .currency("USD")
                                .cryptosToRemove(Set.of())
                                .build()))
                .isInstanceOf(ValidationException.class)
                .hasMessage("You should have at least one crypto to update or remove");

        assertThatThrownBy(() ->
                Validation.updatePortfolio("usd",
                        UpdatePortfolio.builder()
                                .currency("USD")
                                .cryptosToUpdate(Set.of())
                                .cryptosToRemove(Set.of())
                                .build()))
                .isInstanceOf(ValidationException.class)
                .hasMessage("You should have at least one crypto to update or remove");

        assertThatThrownBy(() ->
                Validation.updatePortfolio("usd",
                        UpdatePortfolio.builder()
                                .currency("USD")
                                .cryptosToUpdate(Set.of())
                                .cryptosToRemove(Set.of())
                                .build()))
                .isInstanceOf(ValidationException.class)
                .hasMessage("You should have at least one crypto to update or remove");

        assertThatThrownBy(() ->
                Validation.updatePortfolio("usd",
                        UpdatePortfolio.builder()
                                .currency("USD")
                                .cryptosToUpdate(Set.of(Crypto.builder().build()))
                                .cryptosToRemove(Set.of())
                                .build()))
                .isInstanceOf(ValidationException.class)
                .hasMessage("All cryptos to update must have a defined symbol");

        assertThatThrownBy(() ->
                Validation.updatePortfolio("usd",
                        UpdatePortfolio.builder()
                                .currency("USD")
                                .cryptosToUpdate(Set.of(Crypto.builder().symbol("BTC").build()))
                                .cryptosToRemove(Set.of())
                                .build()))
                .isInstanceOf(ValidationException.class)
                .hasMessage("All cryptos to update must have a positive amount to invest");

        assertThatThrownBy(() ->
                Validation.updatePortfolio("usd",
                        UpdatePortfolio.builder()
                                .currency("USD")
                                .cryptosToUpdate(Set.of(Crypto.builder().symbol("BTC").amountInvested(0.0).build()))
                                .cryptosToRemove(Set.of())
                                .build()))
                .isInstanceOf(ValidationException.class)
                .hasMessage("All cryptos to update must have a positive amount to invest");

        assertThatThrownBy(() ->
                Validation.updatePortfolio("usd",
                        UpdatePortfolio.builder()
                                .currency("USD")
                                .cryptosToUpdate(Set.of(Crypto.builder().symbol("BTC").amountInvested(-1.0).build()))
                                .cryptosToRemove(Set.of())
                                .build()))
                .isInstanceOf(ValidationException.class)
                .hasMessage("All cryptos to update must have a positive amount to invest");


        Validation.updatePortfolio("USD", UpdatePortfolio.builder()
                .currency("USD")
                .cryptosToUpdate(Set.of(Crypto.builder().symbol("BTC").amountInvested(20.0).build()))
                .cryptosToRemove(Set.of("ETH"))
                .build());

        Validation.updatePortfolio("USD", UpdatePortfolio.builder()
                .currency("USD")
                .cryptosToUpdate(Set.of(Crypto.builder().symbol("BTC").amountInvested(20.0).build()))
                .build());

        Validation.updatePortfolio("USD", UpdatePortfolio.builder()
                .currency("USD")
                .cryptosToRemove(Set.of("ETH"))
                .build());
    }
}
