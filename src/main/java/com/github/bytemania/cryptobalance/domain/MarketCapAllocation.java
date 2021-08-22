package com.github.bytemania.cryptobalance.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Value;

import java.math.BigDecimal;
import java.util.List;

@Value(staticConstructor = "of")
@Getter(AccessLevel.PRIVATE)
public class MarketCapAllocation implements BalanceStrategy {

    List<CryptoAllocation> cryptoAllocations;
    String stableCoinSymbol;
    double stableCoinAllocationPercentage;
    BigDecimal valueToAllocate;
    BigDecimal minValueToAllocate;


    @Override
    public List<CryptoAllocation> allocate() {
        return null;
    }
}
