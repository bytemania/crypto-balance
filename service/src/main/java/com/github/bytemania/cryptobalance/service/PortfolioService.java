package com.github.bytemania.cryptobalance.service;

import com.github.bytemania.cryptobalance.domain.dto.CryptoState;
import com.github.bytemania.cryptobalance.port.in.LoadPortfolioPortIn;
import com.github.bytemania.cryptobalance.port.in.UpdatePortfolioPortIn;
import com.github.bytemania.cryptobalance.port.out.LoadPortfolioPortOut;
import com.github.bytemania.cryptobalance.port.out.RemovePortfolioPortOut;
import com.github.bytemania.cryptobalance.port.out.UpdatePortfolioPortOut;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

@Slf4j
@Service
public class PortfolioService implements LoadPortfolioPortIn, UpdatePortfolioPortIn {

    private final LoadPortfolioPortOut loadPortfolioPortOut;
    private final UpdatePortfolioPortOut updatePortfolioPortOut;
    private final RemovePortfolioPortOut removePortfolioPortOut;

    @Autowired
    public PortfolioService(LoadPortfolioPortOut loadPortfolioPortOut,
                            UpdatePortfolioPortOut updatePortfolioPortOut,
                            RemovePortfolioPortOut removePortfolioPortOut) {
        this.loadPortfolioPortOut = loadPortfolioPortOut;
        this.updatePortfolioPortOut = updatePortfolioPortOut;
        this.removePortfolioPortOut = removePortfolioPortOut;
    }

    @Override
    public Set<CryptoState> load() {
        log.info("Portfolio Service load");
        return loadPortfolioPortOut.load();
    }

    @Override
    public void update(Set<CryptoState> cryptosToUpdate, Set<String> cryptosToRemove) {
        log.info("Portfolio Service Collection cryptosToUpdate:{}, cryptosToRemove:{}",
                cryptosToUpdate, cryptosToRemove);

        updatePortfolioPortOut.update(cryptosToUpdate);
        removePortfolioPortOut.remove(cryptosToRemove);
    }

}
