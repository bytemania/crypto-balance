package com.github.bytemania.cryptobalance.port.out;

import com.github.bytemania.cryptobalance.domain.dto.Crypto;

import java.util.List;

public interface LoadCoinMarketCapPort {
    List<Crypto> load();
}
