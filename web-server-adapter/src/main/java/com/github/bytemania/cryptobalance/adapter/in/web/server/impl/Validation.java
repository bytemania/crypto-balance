package com.github.bytemania.cryptobalance.adapter.in.web.server.impl;

import com.github.bytemania.cryptobalance.adapter.in.web.server.dto.portfolio.Crypto;
import com.github.bytemania.cryptobalance.adapter.in.web.server.dto.portfolio.UpdatePortfolio;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

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

    public static void updatePortfolio(String envCurrency, UpdatePortfolio updatePortfolio) throws ValidationException {
        if (!envCurrency.equalsIgnoreCase(updatePortfolio.getCurrency())) {
            throw new ValidationException("currency must be " + envCurrency + " value: " + updatePortfolio.getCurrency());
        }

        if (isEmpty(updatePortfolio.getCryptosToUpdate()) && isEmpty(updatePortfolio.getCryptosToRemove())) {
            throw new ValidationException("You should have at least one crypto to update or remove");
        }

        for (Crypto crypto : Optional.ofNullable(updatePortfolio.getCryptosToUpdate()).orElse(Set.of())) {
            if (isEmpty(crypto.getSymbol())) {
                throw new ValidationException("All cryptos to update must have a defined symbol");
            }

            if (crypto.getAmountInvested() == null || crypto.getAmountInvested() <= 0.0) {
                throw new ValidationException("All cryptos to update must have a positive amount to invest");
            }
        }
    }

    private static boolean isEmpty(Object o) {
        if (o == null) return true;
        if (o instanceof Collection<?>) return ((Collection<?>) o).isEmpty();
        if (o instanceof String) return ((String) o).isEmpty();
        return false;
    }
}
