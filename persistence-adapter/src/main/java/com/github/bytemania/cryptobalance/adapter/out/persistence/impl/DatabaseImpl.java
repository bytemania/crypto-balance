package com.github.bytemania.cryptobalance.adapter.out.persistence.impl;

import com.github.bytemania.cryptobalance.adapter.out.persistence.Database;
import com.github.bytemania.cryptobalance.adapter.out.persistence.DatabaseConfig;
import com.github.bytemania.cryptobalance.adapter.out.persistence.dto.CryptoRow;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.HTreeMap;
import org.mapdb.Serializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class DatabaseImpl implements Database {

    private final DatabaseConfig databaseConfig;
    private final Serializer<CryptoRow> serializer;

    private DB db;

    @Autowired
    public DatabaseImpl(DatabaseConfig databaseConfig) {
        this.databaseConfig = databaseConfig;
        this.serializer = new CryptoRowSerializer();
    }

    @Override
    public void connect() {
        db = DBMaker.fileDB(databaseConfig.getPortfolioDatabaseFileName()).make();
    }

    @Override
    public void disconnect() {
        db.close();
    }

    @Override
    public Set<CryptoRow> load() {
        return new HashSet<>(getTable().values());
    }

    @Override
    public void remove(Set<String> cryptoSymbols) {
        var map = getTable();
        map.keySet().removeAll(cryptoSymbols);
        db.commit();
    }

    @Override
    public void update(Set<CryptoRow> cryptos) {
        var map = getTable();
        cryptos.forEach(cryptoRow -> map.put(cryptoRow.getSymbol(),cryptoRow));
        db.commit();
    }


    private HTreeMap<String, CryptoRow> getTable() {
        if (db != null && !db.isClosed()) {
            return db
                    .hashMap(databaseConfig.getPortfolioDatabaseTable(), Serializer.STRING, serializer)
                    .createOrOpen();
        } else {
            throw new IllegalStateException("DB is closed " + databaseConfig.toString());
        }
    }
}
