package com.github.bytemania.cryptobalance.adapter.out.persistence;

import java.math.BigDecimal;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;

public interface Database {
    void connect();

    void disconnect();

    ConcurrentMap<String, BigDecimal> load();

    void remove(ConcurrentLinkedQueue<String> cryptoSymbols);

    void update(ConcurrentMap<String, BigDecimal> cryptos);
}
