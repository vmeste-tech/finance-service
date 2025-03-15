package ru.kolpakovee.finance_service.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import ru.kolpakovee.finance_service.entities.ExpensesEntity;
import ru.kolpakovee.finance_service.records.ExpensesDto;

@Mapper
public interface ExpensesMapper {

    ExpensesMapper INSTANCE = Mappers.getMapper(ExpensesMapper.class);

    ExpensesDto toDto(ExpensesEntity entity);
}
