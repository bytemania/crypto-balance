package com.github.bytemania.cryptobalance.adapter.in.web.server.dto.common;

import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.experimental.NonFinal;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;
import org.springframework.http.HttpStatus;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

@Value
@NonFinal
@RequiredArgsConstructor
@Jacksonized
@SuperBuilder
public class ResponseError {

    private static String generateTimeStamp() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ssZ");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        return simpleDateFormat.format(new Date());
    }

    String timestamp = generateTimeStamp();
    HttpStatus status;
    String error;
    String message;
    String path;
}
