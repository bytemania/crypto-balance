package com.github.bytemania.adapter.in.web.server.impl;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Validation {

    public static void allocation(double stableCryptoPercentage,
                                  double valueToInvest,
                                  double minValueToAllocate) throws ValidationException {
        if (stableCryptoPercentage < 0 || stableCryptoPercentage > 100) {
            throw new ValidationException("stableCryptoPercentage must be [0..100] value: " + stableCryptoPercentage);
        }

        if (valueToInvest < 0) {
            throw new ValidationException("valueToInvest must be >= 0 value: " + valueToInvest);
        }

        if (minValueToAllocate < 0) {
            throw new ValidationException("minValueToAllocate must be >= 0 value: " + minValueToAllocate);
        }

        if (minValueToAllocate > valueToInvest) {
            throw new ValidationException("minValueToAllocate must be <= valueToInvest value: " + minValueToAllocate
                    + " valueToInvest: " + valueToInvest);
        }
    }

}
