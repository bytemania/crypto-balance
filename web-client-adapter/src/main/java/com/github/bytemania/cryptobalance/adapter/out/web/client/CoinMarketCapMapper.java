package com.github.bytemania.cryptobalance.adapter.out.web.client;

import com.github.bytemania.cryptobalance.adapter.out.web.client.dto.CryptoCurrency;
import com.github.bytemania.cryptobalance.adapter.out.web.client.dto.Listing;
import com.github.bytemania.cryptobalance.domain.dto.Crypto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper
abstract class CoinMarketCapMapper {

    public static CoinMarketCapMapper INSTANCE = Mappers.getMapper(CoinMarketCapMapper.class);

    Set<Crypto> listingToCryptos(Listing listing) {
        return listing.getData().stream().map(this::cryptoCurrencyToCrypto).collect(Collectors.toSet());
    }

    @Mapping(source = "cryptoCurrency.quote.quoteDetail.marketCapDominance", target = "marketCapPercentage")
    @Mapping(source = "cryptoCurrency.quote.quoteDetail.price", target = "price", qualifiedByName = "doubleToBigDecimal")
    @Mapping(target = "stableCoin", expression = "java(cryptoCurrency.getTags().contains(\"stablecoin\"))")
    abstract Crypto cryptoCurrencyToCrypto(CryptoCurrency cryptoCurrency);

    @Named("doubleToBigDecimal")
    BigDecimal doubleToBigDecimal(double value) {
        return BigDecimal.valueOf(value);
    }
}
