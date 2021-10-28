package com.github.bytemania.cryptobalance.adapter.in.web.server;

import com.github.bytemania.cryptobalance.domain.dto.AllocationResult;
import com.github.bytemania.cryptobalance.domain.dto.CryptoAllocation;
import com.github.bytemania.cryptobalance.domain.dto.CryptoState;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Fixture {

    public static final List<CryptoState> cryptoState = List.of(
            CryptoState.of("USDT", BigDecimal.valueOf(200)),
            CryptoState.of("BTC", BigDecimal.valueOf(1000)));

    public static final AllocationResult allocationResult = AllocationResult.of(
            BigDecimal.valueOf(21.00),
            BigDecimal.valueOf(1.00),
            List.of(CryptoAllocation.of(
                            "BTC",
                            60,
                            false,
                            BigDecimal.valueOf(60),
                            CryptoAllocation.Operation.BUY,
                            BigDecimal.valueOf(20),
                            BigDecimal.valueOf(40)),
                    CryptoAllocation.of(
                            "ADA",
                            20,
                            false,
                            BigDecimal.valueOf(20),
                            CryptoAllocation.Operation.KEEP,
                            BigDecimal.valueOf(0),
                            BigDecimal.valueOf(20)),
                    CryptoAllocation.of(
                            "USDT",
                            20,
                            true,
                            BigDecimal.valueOf(20),
                            CryptoAllocation.Operation.KEEP,
                            BigDecimal.ZERO,
                            BigDecimal.valueOf(20))));

}
