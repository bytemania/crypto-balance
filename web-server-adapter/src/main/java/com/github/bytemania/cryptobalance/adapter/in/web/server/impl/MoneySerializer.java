package com.github.bytemania.cryptobalance.adapter.in.web.server.impl;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

public class MoneySerializer extends JsonSerializer<Double> {

    @Override
    public void serialize(Double value, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        if (null == value) {
            jsonGenerator.writeNull();
        } else {
            final String pattern = "0.00";
            final DecimalFormat decimalFormat = new DecimalFormat(pattern);
            final double newValue = BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_UP).doubleValue();
            final String output = decimalFormat.format(newValue);
            jsonGenerator.writeNumber(output);
        }
    }
}
