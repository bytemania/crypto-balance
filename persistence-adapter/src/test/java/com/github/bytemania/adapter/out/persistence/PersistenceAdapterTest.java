package com.github.bytemania.adapter.out.persistence;

import com.github.bytemania.cryptobalance.domain.dto.CryptoState;
import nl.altindag.log.LogCaptor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;

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
    private static final PersistenceAdapter persistenceAdapter = new PersistenceAdapter(database);

    private static LogCaptor logCaptor;

    @BeforeAll
    static void beforeAll() {
        logCaptor = LogCaptor.forClass(PersistenceAdapter.class);
    }

    @AfterAll
    static void afterAll() {
        logCaptor.close();
    }

    @BeforeEach
    void beforeEach() {
        reset(database);
        logCaptor.clearLogs();
    }

    @Test
    @DisplayName("Should connect and log when afterSetProperties is called")
    void shouldConnectAndLogWhenAfterSetPropertiesIsCalled() {
        persistenceAdapter.afterPropertiesSet();
        then(database).should(times(1)).connect();

        Assertions.assertThat(logCaptor.getLogs())
                .hasSize(1);
        Assertions.assertThat(logCaptor.getInfoLogs())
                .hasSize(1)
                .containsExactly("Database connected");
    }

    @Test
    @DisplayName("Should disconnect and log when destroy is called")
    void shouldDisconnectAndLogWhenDestroy() throws Exception {
        persistenceAdapter.destroy();
        then(database).should(times(1)).disconnect();

        Assertions.assertThat(logCaptor.getLogs())
                .hasSize(1);
        Assertions.assertThat(logCaptor.getInfoLogs())
                .hasSize(1)
                .containsExactly("Database disconnected");
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

        Assertions.assertThat(logCaptor.getLogs())
                .hasSize(1);
        Assertions.assertThat(logCaptor.getInfoLogs())
                .hasSize(1)
                .containsExactly("Fetching portfolio from the database");
    }

    @Test
    @DisplayName("Should Throw An IllegalStateException if the Database is Down ")
    void shouldThrowAnIllegalStateExceptionIfTheDatabaseIsDown(){
        given(database.load()).willThrow(IllegalStateException.class);
        assertThatThrownBy(persistenceAdapter::load).isInstanceOf(IllegalStateException.class);
        then(database).should(times(1)).load();


        Assertions.assertThat(logCaptor.getLogs())
                .hasSize(1);
        Assertions.assertThat(logCaptor.getInfoLogs())
                .hasSize(1)
                .containsExactly("Fetching portfolio from the database");
    }
}
