package com.github.bytemania.cryptobalance.adapter.out.persistence.dto;

import lombok.Value;

import java.io.Serializable;
import java.math.BigDecimal;

@Value
public class CryptoRow implements Serializable {
    String symbol;
    BigDecimal holding;
    BigDecimal invested;
}
