package com.github.bytemania.adapter.out.persistence;

import com.github.bytemania.adapter.out.persistence.impl.DatabaseConfigImpl;
import com.github.bytemania.adapter.out.persistence.impl.DatabaseImpl;
import com.github.bytemania.cryptobalance.port.out.LoadPortfolioPort;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(classes = {DatabaseConfig.class, DatabaseConfigImpl.class, Database.class, DatabaseImpl.class, PersistenceAdapter.class})
@TestPropertySource(properties = {"DB_FILE=db", "DB_MAP=map"})
public class PersistenceAdapterItTest {

    static {
        System.setProperty("DB_FILE", "db");
        System.setProperty("DB_MAP", "map");
    }

    @AfterAll
    static void tearDown() throws IOException {
        Files.deleteIfExists(Path.of("db"));
    }

    @SuppressWarnings({"UnusedDeclaration"})
    @Autowired
    private LoadPortfolioPort persistenceAdapter;

    @Test
    @DisplayName("PersistenceAdapter should be created")
    void PersistenceAdapterShouldBeCreated() {
        assertThat(persistenceAdapter).isNotNull();
        persistenceAdapter.load();
    }

}
