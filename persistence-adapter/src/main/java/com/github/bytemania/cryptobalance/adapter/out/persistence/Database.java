package com.github.bytemania.cryptobalance.adapter.out.persistence;

import com.github.bytemania.cryptobalance.adapter.out.persistence.dto.CryptoRow;

import java.util.Set;

public interface Database {
    void connect();

    void disconnect();

    Set<CryptoRow> load();

    void remove(Set<String> cryptoSymbols);

    void update(Set<CryptoRow> cryptos);
}
