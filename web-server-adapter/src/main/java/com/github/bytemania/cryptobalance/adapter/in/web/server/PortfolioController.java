package com.github.bytemania.cryptobalance.adapter.in.web.server;

import com.github.bytemania.cryptobalance.adapter.in.web.server.dto.portfolio.GetPortfolio;
import com.github.bytemania.cryptobalance.adapter.in.web.server.dto.portfolio.UpdatePortfolio;
import com.github.bytemania.cryptobalance.adapter.in.web.server.impl.Mapper;
import com.github.bytemania.cryptobalance.adapter.in.web.server.impl.Validation;
import com.github.bytemania.cryptobalance.adapter.in.web.server.impl.ValidationException;
import com.github.bytemania.cryptobalance.domain.dto.CryptoState;
import com.github.bytemania.cryptobalance.port.in.LoadPortfolioPortIn;
import com.github.bytemania.cryptobalance.port.in.UpdatePortfolioPortIn;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@RestController
public class PortfolioController {

    private final ControllerConfig controllerConfig;
    private final LoadPortfolioPortIn loadPortfolioPortIn;
    private final UpdatePortfolioPortIn updatePortfolioPortIn;

    @Autowired
    public PortfolioController(ControllerConfig controllerConfig,
                               LoadPortfolioPortIn loadPortfolioPortIn,
                               UpdatePortfolioPortIn updatePortfolioPortIn) {
        this.controllerConfig = controllerConfig;
        this.loadPortfolioPortIn = loadPortfolioPortIn;
        this.updatePortfolioPortIn = updatePortfolioPortIn;
    }

    @GetMapping("/portfolio")
    @ResponseBody
    public GetPortfolio getPortfolio() {
        log.info("portfolio called");
        List<CryptoState> portfolio = loadPortfolioPortIn.load();
        return Mapper.fromCryptoStateList(controllerConfig.getCurrency(), portfolio);
    }

    @PatchMapping("/portfolio")
    @ResponseBody
    public GetPortfolio updatePortfolio(@RequestBody UpdatePortfolio updatePortfolio) throws ValidationException {
        log.info("update portfolio with values:{}", updatePortfolio);

        Validation.updatePortfolio(controllerConfig.getCurrency(), updatePortfolio);

        Set<CryptoState> cryptosToUpdate = Optional
                .ofNullable(updatePortfolio.getCryptosToUpdate())
                .orElse(Set.of())
                .stream()
                .map(Mapper::fromCrypto)
                .collect(Collectors.toSet());

        Set<String> cryptosToRemove = Optional
                .ofNullable(updatePortfolio.getCryptosToRemove())
                .orElse(Set.of());

        updatePortfolioPortIn.update(cryptosToUpdate, cryptosToRemove);

        List<CryptoState> portfolio = loadPortfolioPortIn.load();
        return Mapper.fromCryptoStateList(controllerConfig.getCurrency(), portfolio);
    }
}
