package com.github.bytemania.cryptobalance.adapter.out.persistence;

import com.github.bytemania.cryptobalance.adapter.out.persistence.impl.Mapper;
import com.github.bytemania.cryptobalance.domain.dto.CryptoState;
import com.github.bytemania.cryptobalance.port.out.LoadPortfolioPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
class PersistenceAdapter implements InitializingBean, DisposableBean, LoadPortfolioPort {

    private final Database database;

    @Autowired
    PersistenceAdapter(Database database) {
        this.database = database;
    }

    @Override
    public void afterPropertiesSet() {
        database.connect();
        log.info("Database connected");
    }

    @Override
    public void destroy() {
        database.disconnect();
        log.info("Database disconnected");
    }

    @Override
    public List<CryptoState> load() {
        log.info("Fetching portfolio from the database");
        return database.load().entrySet().stream()
                .map(Mapper::fromEntry)
                .collect(Collectors.toList());
    }
}
