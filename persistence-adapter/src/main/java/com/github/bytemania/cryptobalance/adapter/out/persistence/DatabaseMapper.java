package com.github.bytemania.cryptobalance.adapter.out.persistence;

import com.github.bytemania.cryptobalance.adapter.out.persistence.dto.CryptoRow;
import com.github.bytemania.cryptobalance.domain.dto.CryptoState;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface DatabaseMapper {

    DatabaseMapper INSTANCE = Mappers.getMapper(DatabaseMapper.class);

    CryptoState cryptoRowToCryptoState(CryptoRow cryptoRow);

    CryptoRow cryptoStateToCryptoRow(CryptoState cryptoState);
}
