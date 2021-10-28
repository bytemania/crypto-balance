package com.github.bytemania.cryptobalance.adapter.in.web.server;

import com.github.bytemania.cryptobalance.adapter.in.web.server.impl.ControllerConfigImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = {ControllerConfig.class, ControllerConfigImpl.class})
@TestPropertySource(properties = {"APP_CURRENCY=EUR"})
class ControllerConfigEnvTest {

    static {
        System.setProperty("APP_CURRENCY", "EUR");
    }

    @Autowired
    private ControllerConfig controllerConfig;

    @Test
    @DisplayName("Should give the default values if the ENV is set")
    void shouldGiveTheEnvironmentValues() {
        assertThat(controllerConfig.getCurrency()).isEqualTo("EUR");

        System.clearProperty("APP_CURRENCY");
    }

}