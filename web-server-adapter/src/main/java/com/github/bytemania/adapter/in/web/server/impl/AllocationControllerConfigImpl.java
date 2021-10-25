package com.github.bytemania.adapter.in.web.server.impl;

import com.github.bytemania.adapter.in.web.server.AllocationControllerConfig;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
public class AllocationControllerConfigImpl implements AllocationControllerConfig {

    @Value("#{systemProperties['APP_CURRENCY'] ?: 'USD'}")
    String currency;

}
