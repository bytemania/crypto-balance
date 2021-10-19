package com.github.bytemania.adapter.out.persistence;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import com.github.bytemania.adapter.out.persistence.util.MemoryAppender;
import com.github.bytemania.cryptobalance.domain.CryptoState;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;

class PersistenceAdapterTest {

    private static final Database database = Mockito.mock(Database.class);
    private static MemoryAppender memoryAppender;
    private static final String LOGGER_NAME = "com.github.bytemania.adapter.out.persistence";
    private static final PersistenceAdapter persistenceAdapter = new PersistenceAdapter(database);

    @BeforeAll
    static void classSetup() {
        Logger logger = (Logger) LoggerFactory.getLogger(LOGGER_NAME);
        memoryAppender = new MemoryAppender();
        memoryAppender.setContext((LoggerContext) LoggerFactory.getILoggerFactory());
        logger.setLevel(Level.DEBUG);
        logger.addAppender(memoryAppender);
        memoryAppender.start();
    }

    @BeforeEach
    void setup() {
        memoryAppender.reset();
        reset(database);
    }

    @Test
    @DisplayName("Should connect and log when afterSetProperties is called")
    void shouldConnectAndLogWhenAfterSetPropertiesIsCalled() throws Exception {
        persistenceAdapter.afterPropertiesSet();
        then(database).should(times(1)).connect();
        assertThat(memoryAppender.contains("Database connected", Level.INFO)).isTrue();
    }

    @Test
    @DisplayName("Should disconnect and log when destroy is called")
    void shouldDisconnectAndLogWhenDestroy() throws Exception {
        persistenceAdapter.destroy();
        then(database).should(times(1)).disconnect();
        assertThat(memoryAppender.contains("Database disconnected", Level.INFO)).isTrue();
    }

    @Test
    @DisplayName("Should Fetch Portfolio when load is called")
    void shouldFetchPortfolioWhenLoadIsCalled(){
        ConcurrentMap<String, BigDecimal> expectedReturn = new ConcurrentHashMap<>();
        expectedReturn.put("BTC", BigDecimal.TEN);
        expectedReturn.put("ETH", BigDecimal.ZERO);
        given(database.load()).willReturn(expectedReturn);
        List<CryptoState> result = persistenceAdapter.load();
        assertThat(result).isEqualTo(List.of(CryptoState.of("BTC", BigDecimal.TEN),
                CryptoState.of("ETH", BigDecimal.ONE)));
        then(database).should(times(1)).load();
    }

    @Test
    @DisplayName("Should Throw An IllegalStateException if the Database is Down ")
    void shouldThrowAnIllegalStateExceptionIfTheDatabaseIsDown(){
        given(database.load()).willThrow(IllegalStateException.class);
        assertThatThrownBy(persistenceAdapter::load).isInstanceOf(IllegalStateException.class);
        then(database).should(times(1)).load();
    }
}
