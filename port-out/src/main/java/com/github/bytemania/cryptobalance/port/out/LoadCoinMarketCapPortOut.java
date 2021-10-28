package com.github.bytemania.cryptobalance.port.out;

import com.github.bytemania.cryptobalance.domain.dto.Crypto;

import java.util.List;

public interface LoadCoinMarketCapPortOut {
    List<Crypto> load();
}
