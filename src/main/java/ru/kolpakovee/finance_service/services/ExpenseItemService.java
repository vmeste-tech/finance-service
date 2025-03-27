package ru.kolpakovee.finance_service.services;

import ru.kolpakovee.finance_service.records.ExpenseItemCreateDto;
import ru.kolpakovee.finance_service.records.ExpenseItemDto;
import ru.kolpakovee.finance_service.records.ExpenseItemUpdateDto;

import java.util.List;
import java.util.UUID;

public interface ExpenseItemService {
    ExpenseItemDto createExpenseItem(ExpenseItemCreateDto dto);

    ExpenseItemDto updateExpenseItem(UUID id, ExpenseItemUpdateDto dto);

    void deleteExpenseItem(UUID id);

    List<ExpenseItemDto> getApartmentExpenseItem(UUID apartmentId);
}

