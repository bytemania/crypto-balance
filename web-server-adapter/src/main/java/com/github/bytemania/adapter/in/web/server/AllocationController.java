package com.github.bytemania.adapter.in.web.server;

import com.github.bytemania.adapter.in.web.server.dto.Response;
import com.github.bytemania.adapter.in.web.server.impl.Mapper;
import com.github.bytemania.adapter.in.web.server.impl.Validation;
import com.github.bytemania.adapter.in.web.server.impl.ValidationException;
import com.github.bytemania.cryptobalance.domain.dto.AllocationResult;
import com.github.bytemania.cryptobalance.domain.dto.Crypto;
import com.github.bytemania.cryptobalance.domain.util.Util;
import com.github.bytemania.cryptobalance.port.in.CreateAllocation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
public class AllocationController {

    private final AllocationControllerConfig allocationControllerConfig;
    private final CreateAllocation createAllocation;

    @Autowired
    public AllocationController(AllocationControllerConfig allocationControllerConfig,
                                CreateAllocation createAllocation) {
        this.allocationControllerConfig = allocationControllerConfig;
        this.createAllocation = createAllocation;
    }

    @GetMapping("/allocate")
    @ResponseBody
    public Response allocate(
            @RequestParam(name = "stableCryptoSymbol") String stableCryptoSymbol,
            @RequestParam(name = "stableCryptoPercentage") double stableCryptoPercentage,
            @RequestParam(name = "valueToInvest") double valueToInvest,
            @RequestParam(name = "minValueToAllocate") double minValueToAllocate
            ) throws ValidationException {

        Validation.allocation(stableCryptoPercentage, valueToInvest, minValueToAllocate);

        Crypto stableCoin = Crypto.of(stableCryptoSymbol, Util.normalize(stableCryptoPercentage), true);
        BigDecimal value = Util.normalize(BigDecimal.valueOf(valueToInvest));
        BigDecimal minValue = Util.normalize(BigDecimal.valueOf(minValueToAllocate));

        AllocationResult allocation = createAllocation.allocate(stableCoin, value, minValue);

        return Mapper.fromAllocationResult(allocationControllerConfig.getCurrency(), allocation);
    }



}
