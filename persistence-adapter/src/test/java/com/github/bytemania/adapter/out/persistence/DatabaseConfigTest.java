package com.github.bytemania.adapter.out.persistence;

import com.github.bytemania.adapter.out.persistence.impl.DatabaseConfigImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;


@SpringBootTest(classes = {DatabaseConfig.class, DatabaseConfigImpl.class})
class DatabaseConfigTest {

    static {
        System.clearProperty("DB_FILE");
        System.clearProperty("DB_MAP");
    }

    @SuppressWarnings({"UnusedDeclaration"})
    @Autowired
    private DatabaseConfig databaseConfig;

    @Test
    @DisplayName("Should give the default values if the ENV is not set")
    void shouldGiveTheDefaultValues() {
        assertThat(databaseConfig.getPortfolioDatabaseFileName()).isEqualTo("portfolio.db");
        assertThat(databaseConfig.getPortfolioDatabaseTable()).isEqualTo("portfolio");
    }
}
