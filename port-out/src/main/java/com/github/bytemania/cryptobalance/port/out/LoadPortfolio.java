package com.github.bytemania.cryptobalance.port.out;

import com.github.bytemania.cryptobalance.domain.CryptoState;

import java.util.List;

public interface LoadPortfolio {
    List<CryptoState> load();
}
