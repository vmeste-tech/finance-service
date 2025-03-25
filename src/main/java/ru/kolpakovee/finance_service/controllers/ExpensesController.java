package ru.kolpakovee.finance_service.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.kolpakovee.finance_service.records.CreateExpenseRequest;
import ru.kolpakovee.finance_service.records.ExpensesDto;
import ru.kolpakovee.finance_service.services.ExpensesService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@CrossOrigin
@RestController
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@RequestMapping("/api/v1/expenses")
@Tag(name = "Управление расходами", description = "API для управления расходами")
public class ExpensesController {

    private final ExpensesService expensesService;

    @GetMapping
    @Operation(summary = "Получение расходов за указанный период",
            description = "Позволяет получить список всех расходов за указанный период времени.")
    public List<ExpensesDto> getExpenses(@RequestParam(required = false)
                                         @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                         @RequestParam(required = false)
                                         @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        LocalDateTime start = startDate != null ? startDate.atStartOfDay() : null;
        LocalDateTime end = endDate != null ? endDate.atTime(23, 59, 59) : null;

        return expensesService.getExpensesByPeriod(start, end);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Создание нового расхода", description = "Позволяет создать запись о новом расходе.")
    public ExpensesDto createExpenses(@RequestBody CreateExpenseRequest request) {
        return expensesService.createExpense(request);
    }
}
