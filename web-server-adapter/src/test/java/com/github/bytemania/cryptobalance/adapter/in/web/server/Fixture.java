package com.github.bytemania.cryptobalance.adapter.in.web.server;

import com.github.bytemania.cryptobalance.domain.dto.AllocationResult;
import com.github.bytemania.cryptobalance.domain.dto.CryptoAllocation;
import com.github.bytemania.cryptobalance.domain.dto.CryptoState;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Set;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Fixture {

    public static final Set<CryptoState> cryptoState = Set.of(
            new CryptoState("BUSD", BigDecimal.valueOf(200), BigDecimal.valueOf(200)),
            new CryptoState("BTC", BigDecimal.valueOf(0.02), BigDecimal.valueOf(1000)));

    public static final AllocationResult allocationResult = new AllocationResult(
            BigDecimal.valueOf(200),
            BigDecimal.valueOf(1201),
            BigDecimal.valueOf(1200),
            BigDecimal.ONE,
            BigDecimal.valueOf(25),
            new CryptoAllocation("BUSD", BigDecimal.ONE, BigDecimal.valueOf(121), BigDecimal.valueOf(200), 20, BigDecimal.valueOf(21)),
            Set.of(
                    new CryptoAllocation("DOGE", BigDecimal.valueOf(0.2), BigDecimal.valueOf(10), BigDecimal.valueOf(100), 15, BigDecimal.valueOf(30)),
                    new CryptoAllocation("ADA", BigDecimal.valueOf(2), BigDecimal.valueOf(220), BigDecimal.valueOf(450), 28, BigDecimal.valueOf(-40)),
                    new CryptoAllocation("BTC", BigDecimal.valueOf(65000), BigDecimal.valueOf(0.001), BigDecimal.valueOf(650), 55, BigDecimal.valueOf(20))
            ));

}
