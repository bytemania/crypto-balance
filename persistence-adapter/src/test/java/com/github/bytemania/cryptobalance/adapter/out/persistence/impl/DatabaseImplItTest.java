package com.github.bytemania.cryptobalance.adapter.out.persistence.impl;

import com.github.bytemania.cryptobalance.adapter.out.persistence.DatabaseConfig;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapdb.DBMaker;
import org.mapdb.Serializer;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class DatabaseImplItTest {

    private static final String DATABASE = "portfolio.db";
    private static final String TABLE = "portfolio";
    private static final Map<String, BigDecimal> cryptos = Map.of(
            "BTC", BigDecimal.valueOf(100),
            "ETH",  BigDecimal.valueOf(10),
            "ADA",  BigDecimal.valueOf(40));
    private final static DatabaseConfig databaseConfig = new DatabaseConfigImpl(DATABASE, TABLE);

    @BeforeEach
    void beforeEach() {
        var db = DBMaker.fileDB(DATABASE).make();
        var map = db.hashMap(TABLE, Serializer.STRING, Serializer.BIG_DECIMAL).createOrOpen();
        map.putAll(cryptos);
        db.commit();
        db.close();
    }

    @AfterEach
    void afterEach() throws IOException {
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

    @Test
    @DisplayName("Should Update Cryptos")
    void shouldUpdateCryptos() {
        ConcurrentMap<String, BigDecimal> cryptosToUpdate = new ConcurrentHashMap<>();
        cryptosToUpdate.put("ADA", BigDecimal.valueOf(20));
        cryptosToUpdate.put("DOGE", BigDecimal.valueOf(21));

        var db = new DatabaseImpl(databaseConfig);
        db.connect();

        db.update(cryptosToUpdate);
        var result = db.load();

        assertThat(result)
                .hasSize(4)
                .contains(
                        entry("BTC", BigDecimal.valueOf(100)),
                        entry("ETH", BigDecimal.valueOf(10)),
                        entry("ADA", BigDecimal.valueOf(20)),
                        entry("DOGE", BigDecimal.valueOf(21)));

        db.disconnect();
    }

    @Test
    @DisplayName("Should throw IllegalStateException if is the db is disconnected on Update")
    void shouldThrowErrorDbDisconnectedOnUpdate() {
        var db = new DatabaseImpl(databaseConfig);
        db.connect();
        db.disconnect();

        ConcurrentMap<String, BigDecimal> cryptosToUpdate = new ConcurrentHashMap<>();
        cryptosToUpdate.put("ADA", BigDecimal.valueOf(20));
        cryptosToUpdate.put("DOGE", BigDecimal.valueOf(21));

        assertThatThrownBy(() -> db.update(cryptosToUpdate))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("DB is closed DatabaseConfigImpl(portfolioDatabaseFileName=portfolio.db, portfolioDatabaseTable=portfolio)");
    }

    @Test
    @DisplayName("Should Remove Cryptos")
    void shouldRemoveCryptos() {
        ConcurrentLinkedQueue<String> cryptosToRemove = new ConcurrentLinkedQueue<>();
        cryptosToRemove.add("ADA");

        var db = new DatabaseImpl(databaseConfig);
        db.connect();

        db.remove(cryptosToRemove);
        var result = db.load();

        assertThat(result)
                .hasSize(2)
                .contains(
                        entry("BTC", BigDecimal.valueOf(100)),
                        entry("ETH", BigDecimal.valueOf(10)));

        db.disconnect();
    }

    @Test
    @DisplayName("Should throw IllegalStateException if is the db is disconnected on Remove")
    void shouldThrowErrorDbDisconnectedOnRemove() {
        var db = new DatabaseImpl(databaseConfig);
        db.connect();
        db.disconnect();

        assertThatThrownBy(() -> db.remove(new ConcurrentLinkedQueue<>()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("DB is closed DatabaseConfigImpl(portfolioDatabaseFileName=portfolio.db, portfolioDatabaseTable=portfolio)");
    }
}
