package com.github.bytemania.cryptobalance.port.out;

import com.github.bytemania.cryptobalance.domain.Crypto;

import java.util.List;

public interface LoadCoinMarketCap {
    List<Crypto> load();
}
