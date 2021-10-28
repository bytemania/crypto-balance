package com.github.bytemania.cryptobalance.adapter.in.web.server;

import com.github.bytemania.cryptobalance.adapter.in.web.server.dto.allocation.Response;
import com.github.bytemania.cryptobalance.adapter.in.web.server.impl.Mapper;
import com.github.bytemania.cryptobalance.adapter.in.web.server.impl.Validation;
import com.github.bytemania.cryptobalance.adapter.in.web.server.impl.ValidationException;
import com.github.bytemania.cryptobalance.domain.dto.AllocationResult;
import com.github.bytemania.cryptobalance.domain.dto.Crypto;
import com.github.bytemania.cryptobalance.domain.util.Util;
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
public class AllocationController {

    private final ControllerConfig controllerConfig;
    private final CreateAllocationPortIn createAllocationPortIn;

    @Autowired
    public AllocationController(ControllerConfig controllerConfig,
                                CreateAllocationPortIn createAllocationPortIn) {
        log.info("AllocationController started");
        this.controllerConfig = controllerConfig;
        this.createAllocationPortIn = createAllocationPortIn;
    }

    @GetMapping("/allocate")
    @ResponseBody
    public Response allocate(
            @RequestParam(name = "stableCryptoSymbol") String stableCryptoSymbol,
            @RequestParam(name = "stableCryptoPercentage") double stableCryptoPercentage,
            @RequestParam(name = "valueToInvest") double valueToInvest,
            @RequestParam(name = "minValueToAllocate") double minValueToAllocate
            ) throws ValidationException {

        log.info("allocate called with stableCryptoSymbol:{}, stableCryptoPercentage:{}, valueToInvest:{}, minValueToAllocate:{}",
                stableCryptoSymbol, stableCryptoPercentage, valueToInvest, minValueToAllocate);

        Validation.allocation(stableCryptoPercentage, valueToInvest, minValueToAllocate);

        Crypto stableCoin = Crypto.of(stableCryptoSymbol, Util.normalize(stableCryptoPercentage), true);
        BigDecimal value = Util.normalize(BigDecimal.valueOf(valueToInvest));
        BigDecimal minValue = Util.normalize(BigDecimal.valueOf(minValueToAllocate));

        AllocationResult allocation = createAllocationPortIn.allocate(stableCoin, value, minValue);

        return Mapper.fromAllocationResult(controllerConfig.getCurrency(), valueToInvest, allocation);
    }



}
