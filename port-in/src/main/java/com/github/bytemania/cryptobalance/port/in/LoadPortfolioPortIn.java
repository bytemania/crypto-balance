package com.github.bytemania.cryptobalance.port.in;

import com.github.bytemania.cryptobalance.domain.dto.CryptoState;

import java.util.List;

public interface LoadPortfolioPortIn {
    List<CryptoState> load();
}
