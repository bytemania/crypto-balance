package com.github.bytemania.cryptobalance.adapter.in.web.server;

import com.fasterxml.jackson.databind.module.SimpleModule;
import com.github.bytemania.cryptobalance.adapter.in.web.server.dto.allocation.Result;
import com.github.bytemania.cryptobalance.adapter.in.web.server.impl.MoneySerializer;
import com.github.bytemania.cryptobalance.adapter.in.web.server.impl.Validation;
import com.github.bytemania.cryptobalance.adapter.in.web.server.impl.ValidationException;
import com.github.bytemania.cryptobalance.domain.dto.AllocationResult;
import com.github.bytemania.cryptobalance.domain.dto.Crypto;
import com.github.bytemania.cryptobalance.port.in.CreateAllocationPortIn;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@Slf4j
@RestController
public class AllocationController extends SimpleModule {

    private final ControllerConfig controllerConfig;
    private final CreateAllocationPortIn createAllocationPortIn;

    @Autowired
    public AllocationController(ControllerConfig controllerConfig,
                                CreateAllocationPortIn createAllocationPortIn) {
        log.info("AllocationController started");
        this.controllerConfig = controllerConfig;
        this.createAllocationPortIn = createAllocationPortIn;
        this.addSerializer(Double.class, new MoneySerializer());
    }

    @GetMapping("/allocate")
    @ResponseBody
    public Result allocate(
            @RequestParam(name = "stableCryptoSymbol") String stableCryptoSymbol,
            @RequestParam(name = "stableCryptoPercentage") double stableCryptoPercentage,
            @RequestParam(name = "valueToInvest") double valueToInvest,
            @RequestParam(name = "minValueToAllocate") double minValueToAllocate
            ) throws ValidationException {

        log.info("allocate called with stableCryptoSymbol:{}, stableCryptoPercentage:{}, valueToInvest:{}, minValueToAllocate:{}",
                stableCryptoSymbol, stableCryptoPercentage, valueToInvest, minValueToAllocate);

        Validation.allocation(stableCryptoPercentage, valueToInvest, minValueToAllocate);

        Crypto stableCoin = new Crypto(stableCryptoSymbol, stableCryptoPercentage, BigDecimal.ZERO, true);
        BigDecimal value = BigDecimal.valueOf(valueToInvest);
        BigDecimal minValue = BigDecimal.valueOf(minValueToAllocate);

        AllocationResult allocation = createAllocationPortIn.allocate(stableCoin, value, minValue);
        String currency = controllerConfig.getCurrency();
        return WebServerMapper.INSTANCE.allocationResultToResult(currency, allocation);
    }



}
