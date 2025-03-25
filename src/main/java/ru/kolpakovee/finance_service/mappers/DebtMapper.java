package ru.kolpakovee.finance_service.mappers;

import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.kolpakovee.finance_service.entities.DebtEntity;
import ru.kolpakovee.finance_service.records.DebtDto;

import java.util.Map;
import java.util.UUID;

@Mapper
public interface DebtMapper {

    DebtMapper INSTANCE = Mappers.getMapper(DebtMapper.class);

    @Mapping(target = "debtor", expression = "java(userNames.get(entity.getDebtorId()))")
    @Mapping(target = "creditor", expression = "java(userNames.get(entity.getCreditorId()))")
    DebtDto toDto(DebtEntity entity, @Context Map<UUID, String> userNames);
}
