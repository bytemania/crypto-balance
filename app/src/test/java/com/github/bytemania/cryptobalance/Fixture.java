package com.github.bytemania.cryptobalance;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.AssertionsForClassTypes.fail;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Fixture {

    public static String readFileResource(String filename) {
        InputStream jsonStream = Fixture.class.getClassLoader().getResourceAsStream(filename);
        String jsonString = "";
        if (jsonStream == null) {
            fail("Can't read the file");
        } else {
            try {
                jsonString = new String(jsonStream.readAllBytes(), StandardCharsets.UTF_8);
            } catch (IOException e) {
                fail("Can't read the file");
            }
        }
        return jsonString;
    }

}
