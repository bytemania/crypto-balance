package com.github.bytemania.adapter.in.web.server;

import com.github.bytemania.adapter.in.web.server.impl.AllocationControllerConfigImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = {AllocationControllerConfig.class, AllocationControllerConfigImpl.class})
class AllocationControllerConfigTest {

    static {
        System.clearProperty("APP_CURRENCY");
    }

    @Autowired
    private AllocationControllerConfig allocationControllerConfig;

    @Test
    @DisplayName("Should give the default values if the ENV is not Set")
    void shouldGiveTheDefaultValues() {
        assertThat(allocationControllerConfig.getCurrency()).isEqualTo("USD");
    }

}