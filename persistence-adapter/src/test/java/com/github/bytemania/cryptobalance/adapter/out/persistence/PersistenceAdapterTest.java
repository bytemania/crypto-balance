package com.github.bytemania.cryptobalance.adapter.out.persistence;

import com.github.bytemania.cryptobalance.adapter.out.persistence.dto.CryptoRow;
import com.github.bytemania.cryptobalance.domain.dto.CryptoState;
import nl.altindag.log.LogCaptor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;

class PersistenceAdapterTest {

    private static final Database database = Mockito.mock(Database.class);
    private static final PersistenceAdapter persistenceAdapter = new PersistenceAdapter(database);

    private static final CryptoState BTC = new CryptoState("BTC", BigDecimal.ONE, BigDecimal.TEN);
    private static final CryptoState ETH = new CryptoState("ETH", BigDecimal.valueOf(0.5), BigDecimal.valueOf(20));
    private static final Set<CryptoState> STATE = Set.of(BTC, ETH);

    private static final Set<CryptoRow> ROWS = STATE.stream()
            .map(DatabaseMapper.INSTANCE::cryptoStateToCryptoRow)
            .collect(Collectors.toSet());

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
    void shouldDisconnectAndLogWhenDestroy() {
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
        given(database.load()).willReturn(ROWS);

        assertThat(persistenceAdapter.load()).isEqualTo(STATE);

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

    @Test
    @DisplayName("Should update")
    void shouldUpdate() {
        persistenceAdapter.update(STATE);
        then(database).should(times(1)).update(eq(ROWS));

        Assertions.assertThat(logCaptor.getLogs())
                .hasSize(1);
        Assertions.assertThat(logCaptor.getInfoLogs())
                .hasSize(1);
        Assertions.assertThat(logCaptor.getInfoLogs().get(0))
                .contains("Updating cryptos from Portfolio")
                .contains(BTC.toString())
                .contains(ETH.toString())
                .hasSize(133);
    }

    @Test
    @DisplayName("update should process an IllegalStateException")
    void shouldUpdateOnError() {
        willThrow(IllegalStateException.class).given(database).update(eq(ROWS));

        assertThatThrownBy(() -> persistenceAdapter.update(STATE))
                .isInstanceOf(IllegalStateException.class);

        Assertions.assertThat(logCaptor.getLogs())
                .hasSize(1);
        Assertions.assertThat(logCaptor.getInfoLogs())
                .hasSize(1);
        Assertions.assertThat(logCaptor.getInfoLogs().get(0))
                .contains("Updating cryptos from Portfolio")
                .contains(BTC.toString())
                .contains(ETH.toString())
                .hasSize(133);
    }

    @Test
    @DisplayName("Should remove")
    void shouldRemove() {
        Set<String> cryptosToRemove = Set.of("ADA", "DOGE");

        persistenceAdapter.remove(cryptosToRemove);

        then(database).should(times(1)).remove(cryptosToRemove);

        Assertions.assertThat(logCaptor.getLogs())
                .hasSize(1);
        Assertions.assertThat(logCaptor.getInfoLogs())
                .hasSize(1);
        Assertions.assertThat(logCaptor.getInfoLogs().get(0))
                .contains("Removing cryptos from Portfolio")
                .contains("DOGE")
                .contains("ADA")
                .hasSize(44);
    }

    @Test
    @DisplayName("Should remove on error")
    void shouldRemoveOnError() {
        Set<String> cryptosToRemove = Set.of("ADA", "DOGE");

        willThrow(IllegalStateException.class).given(database).remove(cryptosToRemove);

        assertThatThrownBy(() -> persistenceAdapter.remove(cryptosToRemove))
                .isInstanceOf(IllegalStateException.class);

        Assertions.assertThat(logCaptor.getLogs())
                .hasSize(1);
        Assertions.assertThat(logCaptor.getInfoLogs())
                .hasSize(1);
        Assertions.assertThat(logCaptor.getInfoLogs().get(0))
                .contains("Removing cryptos from Portfolio")
                .contains("DOGE")
                .contains("ADA")
                .hasSize(44);
    }

}
