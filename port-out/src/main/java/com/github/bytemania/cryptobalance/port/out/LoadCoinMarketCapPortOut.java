package com.github.bytemania.cryptobalance.port.out;

import com.github.bytemania.cryptobalance.domain.dto.Crypto;

import java.util.Set;

public interface LoadCoinMarketCapPortOut {
    Set<Crypto> load();
}
