package com.github.bytemania.cryptobalance.adapter.in.web.server.impl;


import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;

import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

class MoneySerializerTest {

    @Test
    @DisplayName("moneySerializer for null")
    void moneySerializerForNull() throws IOException {
        JsonGenerator jsonGenerator = Mockito.mock(JsonGenerator.class);
        JsonSerializer<Double> serializer = new MoneySerializer();
        serializer.serialize(null, jsonGenerator, null);
        then(jsonGenerator).should(times(1)).writeNull();
    }

    @Test
    @DisplayName("moneySerializer")
    void moneySerializer() throws IOException {
        JsonGenerator jsonGenerator = Mockito.mock(JsonGenerator.class);
        JsonSerializer<Double> serializer = new MoneySerializer();
        serializer.serialize(10.5, jsonGenerator, null);
        then(jsonGenerator).should(times(1)).writeNumber("10.50");
    }

    @Test
    @DisplayName("moneySerializer rounding up")
    void moneySerializerRoundingUp() throws IOException {
        JsonGenerator jsonGenerator = Mockito.mock(JsonGenerator.class);
        JsonSerializer<Double> serializer = new MoneySerializer();
        serializer.serialize(10.515, jsonGenerator, null);
        then(jsonGenerator).should(times(1)).writeNumber("10.52");
    }

    @Test
    @DisplayName("moneySerializer rounding down")
    void moneySerializerRoundingDown() throws IOException {
        JsonGenerator jsonGenerator = Mockito.mock(JsonGenerator.class);
        JsonSerializer<Double> serializer = new MoneySerializer();
        serializer.serialize(10.514, jsonGenerator, null);
        then(jsonGenerator).should(times(1)).writeNumber("10.51");
    }

    @Test
    @DisplayName("moneySerializer Only Decimals")
    void moneySerializerOnlyDecimals() throws IOException {
        JsonGenerator jsonGenerator = Mockito.mock(JsonGenerator.class);
        JsonSerializer<Double> serializer = new MoneySerializer();
        serializer.serialize(0.5, jsonGenerator, null);
        then(jsonGenerator).should(times(1)).writeNumber("0.50");
    }

}
