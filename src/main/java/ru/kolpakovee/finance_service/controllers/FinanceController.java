package ru.kolpakovee.finance_service.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.kolpakovee.finance_service.records.DebtDto;
import ru.kolpakovee.finance_service.records.Participant;
import ru.kolpakovee.finance_service.services.FinanceService;

import java.util.List;
import java.util.UUID;

@CrossOrigin
@RestController
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@RequestMapping("/api/v1/finance")
@Tag(name = "Управление финансами", description = "API для управления финансами")
public class FinanceController {

    private final FinanceService financesService;

    @Operation(
            summary = "Получение списка долгов",
            description = "Возвращает список долгов пользователей за указанный период времени."
    )
    @GetMapping("/debts/{apartmentId}")
    public List<DebtDto> getDebts(@PathVariable UUID apartmentId, @RequestParam int period) {
        return financesService.getUserDebts(apartmentId, period);
    }

    @Operation(
            summary = "Получение финансов пользователей",
            description = "Возвращает финансовую информацию по пользователям за указанный период времени. Если даты не указаны, возвращаются данные за всё время."
    )
    @GetMapping("/{apartmentId}")
    public List<Participant> getUserFinances(@PathVariable UUID apartmentId, @RequestParam int period) {
        return financesService.getUserFinances(apartmentId, period);
    }

    @Operation(
            summary = "Оплата долга",
            description = "Отмечает долг как оплаченный по его идентификатору."
    )
    @PatchMapping("/debts/{debtId}/pay")
    public DebtDto payDebt(@PathVariable UUID debtId) {
        return financesService.payDebt(debtId);
    }
}
