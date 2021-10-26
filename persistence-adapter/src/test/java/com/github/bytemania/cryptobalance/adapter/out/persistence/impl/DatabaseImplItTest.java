package com.github.bytemania.cryptobalance.adapter.out.persistence.impl;

import com.github.bytemania.cryptobalance.adapter.out.persistence.DatabaseConfig;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapdb.DBMaker;
import org.mapdb.Serializer;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class DatabaseImplItTest {

    private static final String DATABASE = "portfolio.db";
    private static final String TABLE = "portfolio";
    private static final Map<String, BigDecimal> cryptos = Map.of(
            "BTC", BigDecimal.valueOf(100),
            "ETH",  BigDecimal.valueOf(10),
            "ADA",  BigDecimal.valueOf(40));
    private final static DatabaseConfig databaseConfig = new DatabaseConfigImpl(DATABASE, TABLE);

    @BeforeAll
    static void setup() {
        var db = DBMaker.fileDB(DATABASE).make();
        var map = db.hashMap(TABLE, Serializer.STRING, Serializer.BIG_DECIMAL).createOrOpen();
        map.putAll(cryptos);
        db.commit();
        db.close();
    }

    @AfterAll
    static void tearDown() throws IOException {
             assertThat(Files.deleteIfExists(Path.of(DATABASE))).isTrue();
    }

    @Test
    @DisplayName("Should retrieve all cryptos from the database")
    void shouldRetrieveAllCryptosFromTheDatabase() {
        var db = new DatabaseImpl(databaseConfig);
        db.connect();
        var cryptos = db.load();
        assertThat(cryptos).containsExactlyEntriesOf(cryptos);
        db.disconnect();
    }

    @Test
    @DisplayName("Should throw IllegalStateException if is the db is null")
    void shouldThrowErrorDbNull() {
        var db = new DatabaseImpl(databaseConfig);
        assertThatThrownBy(db::load)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("DB is closed DatabaseConfigImpl(portfolioDatabaseFileName=portfolio.db, portfolioDatabaseTable=portfolio)");
    }

    @Test
    @DisplayName("Should throw IllegalStateException if is the db is disconnected")
    void shouldThrowErrorDbDisconnected() {
        var db = new DatabaseImpl(databaseConfig);
        db.connect();
        db.disconnect();
        assertThatThrownBy(db::load)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("DB is closed DatabaseConfigImpl(portfolioDatabaseFileName=portfolio.db, portfolioDatabaseTable=portfolio)");
    }

}
