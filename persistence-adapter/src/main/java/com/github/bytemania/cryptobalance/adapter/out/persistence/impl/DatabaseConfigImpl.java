package com.github.bytemania.cryptobalance.adapter.out.persistence.impl;

import com.github.bytemania.cryptobalance.adapter.out.persistence.DatabaseConfig;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString
public class DatabaseConfigImpl implements DatabaseConfig {
    @Value("${DB_FILE:portfolio.db}")
    private String portfolioDatabaseFileName;

    @Value("${DB_MAP:portfolio}")
    private String portfolioDatabaseTable;
}
