package com.github.bytemania.cryptobalance.domain.util;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class Util {
    public static BigDecimal normalize(BigDecimal amount) {
        return amount.setScale(2, RoundingMode.HALF_UP);
    }

    public static double normalize(double percentage) {
        return BigDecimal.valueOf(percentage).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }
}
