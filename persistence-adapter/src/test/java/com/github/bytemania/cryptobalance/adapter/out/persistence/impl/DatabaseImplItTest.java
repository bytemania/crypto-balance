package com.github.bytemania.cryptobalance.adapter.out.persistence.impl;

import com.github.bytemania.cryptobalance.adapter.out.persistence.DatabaseConfig;
import com.github.bytemania.cryptobalance.adapter.out.persistence.dto.CryptoRow;
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
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class DatabaseImplItTest {

    private static final String DATABASE = "portfolio.db";
    private static final String TABLE = "portfolio";
    private static final CryptoRow BTC = new CryptoRow("BTC", BigDecimal.valueOf(0.00015), BigDecimal.valueOf(100.0));
    private static final CryptoRow ETH = new CryptoRow("ETH", BigDecimal.valueOf(0.04), BigDecimal.valueOf(10.0));
    private static final CryptoRow ADA = new CryptoRow("ADA", BigDecimal.valueOf(15.0), BigDecimal.valueOf(40.0));
    private static final Set<CryptoRow> cryptos = Set.of(BTC, ETH, ADA);
    private final static DatabaseConfig databaseConfig = new DatabaseConfigImpl(DATABASE, TABLE);
    private final static Serializer<CryptoRow> CRYPTO_ROW_SERIALIZER = new CryptoRowSerializer();

    @BeforeEach
    void beforeEach() {
        var db = DBMaker.fileDB(DATABASE).make();
        var map = db.hashMap(TABLE, Serializer.STRING, CRYPTO_ROW_SERIALIZER).createOrOpen();
        cryptos.forEach(cryptoRow -> map.put(cryptoRow.getSymbol(), cryptoRow));
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
        assertThat(cryptos).containsExactlyElementsOf(cryptos);
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
        final CryptoRow NEW_ADA = new CryptoRow("ADA", BigDecimal.valueOf(100.0), BigDecimal.valueOf(1000.0));
        final CryptoRow DOGE = new CryptoRow("DOGE", BigDecimal.valueOf(120.0), BigDecimal.valueOf(100.0));
        Set<CryptoRow> cryptosToUpdate = Set.of(NEW_ADA, DOGE);

        var db = new DatabaseImpl(databaseConfig);
        db.connect();

        db.update(cryptosToUpdate);
        var result = db.load();

        assertThat(result)
                .hasSize(4)
                .contains(BTC, ETH, NEW_ADA, DOGE);

        db.disconnect();
    }

    @Test
    @DisplayName("Should throw IllegalStateException if is the db is disconnected on Update")
    void shouldThrowErrorDbDisconnectedOnUpdate() {
        var db = new DatabaseImpl(databaseConfig);
        db.connect();
        db.disconnect();

        assertThatThrownBy(() -> db.update(Set.of()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("DB is closed DatabaseConfigImpl(portfolioDatabaseFileName=portfolio.db, portfolioDatabaseTable=portfolio)");
    }

    @Test
    @DisplayName("Should Remove Cryptos")
    void shouldRemoveCryptos() {
        Set<String> cryptosToRemove = Set.of("ADA");

        var db = new DatabaseImpl(databaseConfig);
        db.connect();

        db.remove(cryptosToRemove);
        var result = db.load();

        assertThat(result)
                .hasSize(2)
                .contains(BTC, ETH);
        db.disconnect();
    }

    @Test
    @DisplayName("Should Not Remove Cryptos if CryptosToRemove is empty")
    void shouldNotRemoveCryptos() {
        var db = new DatabaseImpl(databaseConfig);
        db.connect();

        db.remove(Set.of());
        var result = db.load();

        assertThat(result)
                .hasSize(3)
                .contains(BTC, ETH, ADA);
        db.disconnect();
    }

    @Test
    @DisplayName("Should throw IllegalStateException if is the db is disconnected on Remove")
    void shouldThrowErrorDbDisconnectedOnRemove() {
        var db = new DatabaseImpl(databaseConfig);
        db.connect();
        db.disconnect();

        assertThatThrownBy(() -> db.remove(Set.of()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("DB is closed DatabaseConfigImpl(portfolioDatabaseFileName=portfolio.db, portfolioDatabaseTable=portfolio)");
    }
}
