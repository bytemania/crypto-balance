package com.github.bytemania.cryptobalance.port.out;

import com.github.bytemania.cryptobalance.domain.dto.CryptoState;

import java.util.Set;

public interface UpdatePortfolioPortOut {

    void update(Set<CryptoState> cryptosToUpdate);

}
