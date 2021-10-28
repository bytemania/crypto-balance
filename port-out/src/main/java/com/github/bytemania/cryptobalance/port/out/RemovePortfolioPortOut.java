package com.github.bytemania.cryptobalance.port.out;

import java.util.Set;

public interface RemovePortfolioPortOut {

    void remove(Set<String> cryptosToRemove);

}
