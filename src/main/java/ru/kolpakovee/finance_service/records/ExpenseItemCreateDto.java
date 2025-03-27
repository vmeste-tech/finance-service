package ru.kolpakovee.finance_service.records;

import java.util.UUID;

public record ExpenseItemCreateDto(
        UUID apartmentId,
        String name,
        String description
) {
}

