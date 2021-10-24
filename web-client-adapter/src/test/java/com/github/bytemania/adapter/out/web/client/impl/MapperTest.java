package com.github.bytemania.adapter.out.web.client.impl;

import com.github.bytemania.adapter.out.web.client.Fixture;
import com.github.bytemania.adapter.out.web.client.dto.Listing;
import com.github.bytemania.cryptobalance.domain.dto.Crypto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class MapperTest {

    @Test
    @DisplayName("Should convert to the internal domain")
    void shouldConvertToTheInternalDomain() {
        Listing listing = Fixture.A_VALID_LISTING;

        List<Crypto> cryptos = Mapper.fromListing(listing);
        assertThat(cryptos)
                .hasSize(2)
                .containsExactly(
                        Crypto.of("BTC", 46.27, false),
                        Crypto.of("USDT", 2.71, true));
    }
}
