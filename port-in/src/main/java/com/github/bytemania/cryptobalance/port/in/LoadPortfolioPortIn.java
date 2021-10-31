package com.github.bytemania.cryptobalance.port.in;

import com.github.bytemania.cryptobalance.domain.dto.CryptoState;

import java.util.Set;

public interface LoadPortfolioPortIn {
    Set<CryptoState> load();
}
