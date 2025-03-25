package ru.kolpakovee.finance_service.records;

public record UserFinanceDto(
        String name,
        double fines,
        double expenses
) {
}
