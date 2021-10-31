package com.github.bytemania.cryptobalance.service;

import com.github.bytemania.cryptobalance.domain.BalanceStrategy;
import com.github.bytemania.cryptobalance.domain.balance_strategy.MarketCapAllocation;
import com.github.bytemania.cryptobalance.domain.dto.AllocationResult;
import com.github.bytemania.cryptobalance.domain.dto.Crypto;
import com.github.bytemania.cryptobalance.domain.dto.CryptoState;
import com.github.bytemania.cryptobalance.port.in.CreateAllocationPortIn;
import com.github.bytemania.cryptobalance.port.out.LoadCoinMarketCapPortOut;
import com.github.bytemania.cryptobalance.port.out.LoadPortfolioPortOut;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Set;

@Slf4j
@Service
public class CreateAllocationService implements CreateAllocationPortIn {

    private final LoadCoinMarketCapPortOut loadCoinMarketCapPortOut;
    private final LoadPortfolioPortOut loadPortfolioPortOut;

    @Autowired
    public CreateAllocationService(LoadCoinMarketCapPortOut loadCoinMarketCapPortOut, LoadPortfolioPortOut loadPortfolioPortOut) {
        this.loadCoinMarketCapPortOut = loadCoinMarketCapPortOut;
        this.loadPortfolioPortOut = loadPortfolioPortOut;
    }

    @Override
    public AllocationResult allocate(Crypto stableCoin, BigDecimal amountToInvest, BigDecimal minAmountToAllocate) {

        log.info("Service Called for crypto stableCoin={}, amountToInvest={}, minAmountToAllocate={}",
                stableCoin, amountToInvest, amountToInvest);

        Set<Crypto> cryptosFromCoinMarketCap = loadCoinMarketCapPortOut.load();
        Set<CryptoState> cryptosFromPortfolio = loadPortfolioPortOut.load();

        BalanceStrategy balanceStrategy = new MarketCapAllocation(
                        cryptosFromCoinMarketCap,
                        stableCoin,
                        amountToInvest,
                        minAmountToAllocate,
                        cryptosFromPortfolio);

        return balanceStrategy.allocate();
    }
}
