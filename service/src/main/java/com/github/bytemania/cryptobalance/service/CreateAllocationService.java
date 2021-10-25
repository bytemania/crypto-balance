package com.github.bytemania.cryptobalance.service;

import com.github.bytemania.cryptobalance.domain.BalanceStrategy;
import com.github.bytemania.cryptobalance.domain.balance_strategy.MarketCapAllocation;
import com.github.bytemania.cryptobalance.domain.dto.AllocationResult;
import com.github.bytemania.cryptobalance.domain.dto.Crypto;
import com.github.bytemania.cryptobalance.domain.dto.CryptoState;
import com.github.bytemania.cryptobalance.port.in.CreateAllocation;
import com.github.bytemania.cryptobalance.port.out.LoadCoinMarketCapPort;
import com.github.bytemania.cryptobalance.port.out.LoadPortfolioPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Service
public class CreateAllocationService implements CreateAllocation {

    private final LoadCoinMarketCapPort loadCoinMarketCapPort;
    private final LoadPortfolioPort loadPortfolioPort;

    @Autowired
    public CreateAllocationService(LoadCoinMarketCapPort loadCoinMarketCapPort, LoadPortfolioPort loadPortfolioPort) {
        this.loadCoinMarketCapPort = loadCoinMarketCapPort;
        this.loadPortfolioPort = loadPortfolioPort;
    }

    @Override
    public AllocationResult allocate(Crypto stableCoin, BigDecimal amountToInvest, BigDecimal minAmountToAllocate) {

        log.info("Service Called for crypto stableCoin={}, amountToInvest={}, minAmountToAllocate={}",
                stableCoin, amountToInvest, amountToInvest);

        List<Crypto> cryptosFromCoinMarketCap = loadCoinMarketCapPort.load();
        List<CryptoState> cryptosFromPortfolio = loadPortfolioPort.load();

        BalanceStrategy balanceStrategy = MarketCapAllocation.of(
                        cryptosFromCoinMarketCap,
                        stableCoin,
                        amountToInvest,
                        minAmountToAllocate,
                        cryptosFromPortfolio);

        return balanceStrategy.allocate();
    }
}
