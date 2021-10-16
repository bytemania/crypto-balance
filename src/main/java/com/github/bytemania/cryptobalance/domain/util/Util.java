package com.github.bytemania.cryptobalance.domain.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class Util {
    public static BigDecimal normalize(BigDecimal amount) {
        return amount.setScale(2, RoundingMode.HALF_UP);
    }
}
