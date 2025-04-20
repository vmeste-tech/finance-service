package ru.kolpakovee.finance_service.records;

import java.util.UUID;

public record CreateExpenseRequest(
        double amount,
        String description,
        String photoBase64,
        UUID itemId
) {
}
