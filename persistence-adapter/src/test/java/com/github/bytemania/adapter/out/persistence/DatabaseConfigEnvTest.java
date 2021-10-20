package com.github.bytemania.adapter.out.persistence;

import com.github.bytemania.adapter.out.persistence.impl.DatabaseConfigImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(classes = {DatabaseConfig.class, DatabaseConfigImpl.class})
@TestPropertySource(properties = {"DB_FILE=db", "DB_MAP=map"})
class DatabaseConfigEnvTest {

    static {
        System.setProperty("DB_FILE", "db");
        System.setProperty("DB_MAP", "map");
    }

    @SuppressWarnings({"UnusedDeclaration"})
    @Autowired
    private DatabaseConfig databaseConfig;

    @Test
    @DisplayName("Should override the default if the ENV is settle")
    void shouldOverrideTheDefaultValues() {
        assertThat(databaseConfig.getPortfolioDatabaseFileName()).isEqualTo("db");
        assertThat(databaseConfig.getPortfolioDatabaseTable()).isEqualTo("map");

        System.clearProperty("DB_FILE");
        System.clearProperty("DB_MAP");
    }
}
