package ru.kolpakovee.finance_service.records;

public record CreateExpenseRequest(
        double amount,
        String description
) {
}
