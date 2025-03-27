package ru.kolpakovee.finance_service.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import ru.kolpakovee.finance_service.entities.ExpenseItemEntity;
import ru.kolpakovee.finance_service.records.ExpenseItemDto;

@Mapper
public interface ExpenseItemMapper {

    ExpenseItemMapper INSTANCE = Mappers.getMapper(ExpenseItemMapper.class);

    ExpenseItemDto toDto(ExpenseItemEntity entity);
}
