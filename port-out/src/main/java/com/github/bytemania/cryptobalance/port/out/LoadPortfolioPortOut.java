package com.github.bytemania.cryptobalance.port.out;

import com.github.bytemania.cryptobalance.domain.dto.CryptoState;

import java.util.Set;

public interface LoadPortfolioPortOut {
    Set<CryptoState> load();
}
