package ru.kolpakovee.finance_service.records;

import java.util.UUID;

public record ExpenseItemDto(
        UUID id,
        UUID apartmentId,
        String name,
        String description
) {
}

