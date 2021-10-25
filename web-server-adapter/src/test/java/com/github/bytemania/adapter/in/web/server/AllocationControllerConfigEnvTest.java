package com.github.bytemania.adapter.in.web.server;

import com.github.bytemania.adapter.in.web.server.impl.AllocationControllerConfigImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = {AllocationControllerConfig.class, AllocationControllerConfigImpl.class})
@TestPropertySource(properties = {"APP_CURRENCY=EUR"})
class AllocationControllerConfigEnvTest {

    static {
        System.setProperty("APP_CURRENCY", "EUR");
    }

    @Autowired
    private AllocationControllerConfig allocationControllerConfig;

    @Test
    @DisplayName("Should give the default values if the ENV is set")
    void shouldGiveTheEnvironmentValues() {
        assertThat(allocationControllerConfig.getCurrency()).isEqualTo("EUR");

        System.clearProperty("APP_CURRENCY");
    }

}