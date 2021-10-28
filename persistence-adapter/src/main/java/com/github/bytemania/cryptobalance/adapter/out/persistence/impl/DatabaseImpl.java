package com.github.bytemania.cryptobalance.adapter.out.persistence.impl;

import com.github.bytemania.cryptobalance.adapter.out.persistence.Database;
import com.github.bytemania.cryptobalance.adapter.out.persistence.DatabaseConfig;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.HTreeMap;
import org.mapdb.Serializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;

@Component
public class DatabaseImpl implements Database {

    private final DatabaseConfig databaseConfig;
    private DB db;
    @Autowired
    public DatabaseImpl(DatabaseConfig databaseConfig) {
        this.databaseConfig = databaseConfig;
    }

    public void connect() {
        db = DBMaker.fileDB(databaseConfig.getPortfolioDatabaseFileName()).make();
    }

    public ConcurrentMap<String, BigDecimal> load() {
        return getTable();
    }

    @Override
    public void remove(ConcurrentLinkedQueue<String> cryptoSymbols) {
        var map = getTable();
        map.keySet().removeAll(cryptoSymbols);
        db.commit();
    }

    @Override
    public void update(ConcurrentMap<String, BigDecimal> cryptos) {
        var map = getTable();
        map.putAll(cryptos);
        db.commit();
    }

    public void disconnect() {
        db.close();
    }

    private HTreeMap<String, BigDecimal> getTable() {
        if (db != null && !db.isClosed()) {
            return db
                    .hashMap(databaseConfig.getPortfolioDatabaseTable(), Serializer.STRING, Serializer.BIG_DECIMAL)
                    .createOrOpen();
        } else {
            throw new IllegalStateException("DB is closed " + databaseConfig.toString());
        }
    }
}
