package com.github.bytemania.cryptobalance.adapter.out.web.client;

import com.github.bytemania.cryptobalance.domain.dto.Crypto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class CoinMarketCapMapperTest {

    @Test
    @DisplayName("convert from listing to set of cryptos")
    void convertFromListingToSetOfCryptos() {
        Set<Crypto> cryptos = CoinMarketCapMapper.INSTANCE.listingToCryptos(Fixture.A_VALID_LISTING);

        assertThat(cryptos)
                .hasSize(2)
                .containsExactly(new Crypto("BTC", 46.2667, BigDecimal.valueOf(100.4), false),
                        new Crypto("USDT", 27144, BigDecimal.valueOf(20.34563), true));
    }

    @Test
    @DisplayName("convert CryptoCurrency to Crypto")
    void convertCryptoCurrencyToCrypto() {
        assertThat(CoinMarketCapMapper.INSTANCE.cryptoCurrencyToCrypto(Fixture.A_VALID_LISTING.getData().get(0)))
                .isEqualTo(new Crypto("BTC", 46.2667, BigDecimal.valueOf(100.4), false));

        assertThat(CoinMarketCapMapper.INSTANCE.cryptoCurrencyToCrypto(Fixture.A_VALID_LISTING.getData().get(1)))
                .isEqualTo(new Crypto("USDT", 27144, BigDecimal.valueOf(20.34563), true));

        assertThat(CoinMarketCapMapper.INSTANCE.cryptoCurrencyToCrypto(null)).isNull();
    }

    @Test
    @DisplayName("convert double to BigDecimal")
    void convertDoubleToBigDecimal() {
        assertThat(CoinMarketCapMapper.INSTANCE.doubleToBigDecimal(23.56)).isEqualTo(BigDecimal.valueOf(23.56));
    }

}