package com.github.bytemania.adapter.out.persistence;

import java.math.BigDecimal;
import java.util.concurrent.ConcurrentMap;

public interface Database {
    void connect();
    ConcurrentMap<String, BigDecimal> load();
    void disconnect();
}
