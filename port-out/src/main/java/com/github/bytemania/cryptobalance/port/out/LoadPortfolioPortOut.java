package com.github.bytemania.cryptobalance.port.out;

import com.github.bytemania.cryptobalance.domain.dto.CryptoState;

import java.util.List;

public interface LoadPortfolioPortOut {
    List<CryptoState> load();
}
