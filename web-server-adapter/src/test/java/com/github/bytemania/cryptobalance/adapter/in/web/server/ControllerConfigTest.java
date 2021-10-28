package com.github.bytemania.cryptobalance.adapter.in.web.server;

import com.github.bytemania.cryptobalance.adapter.in.web.server.impl.ControllerConfigImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = {ControllerConfig.class, ControllerConfigImpl.class})
class ControllerConfigTest {

    static {
        System.clearProperty("APP_CURRENCY");
    }

    @Autowired
    private ControllerConfig controllerConfig;

    @Test
    @DisplayName("Should give the default values if the ENV is not Set")
    void shouldGiveTheDefaultValues() {
        assertThat(controllerConfig.getCurrency()).isEqualTo("USD");
    }

}