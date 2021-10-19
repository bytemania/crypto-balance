package com.github.bytemania.adapter.out.persistence.impl;

import com.github.bytemania.adapter.out.persistence.DatabaseConfig;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class DatabaseConfigImpl implements DatabaseConfig {
    @Value("#{systemProperties['DB_FILE'] ?: 'portfolio.db'}")
    private String portfolioDatabaseFileName;

    @Value("#{systemProperties['DB_MAP'] ?: 'portfolio'}")
    private String portfolioDatabaseTable;
}
