package ru.kolpakovee.finance_service.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.kolpakovee.finance_service.records.ExpenseItemCreateDto;
import ru.kolpakovee.finance_service.records.ExpenseItemDto;
import ru.kolpakovee.finance_service.records.ExpenseItemUpdateDto;
import ru.kolpakovee.finance_service.services.ExpenseItemService;

import java.util.List;
import java.util.UUID;

@CrossOrigin
@RestController
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@RequestMapping("/api/v1/expense-items")
@Tag(name = "Управление статьями затрат", description = "API для управления статьями затрат")
public class ExpenseItemController {

    private final ExpenseItemService expenseItemService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Создать новую статью затрат", description = "Добавляет новую статью затрат в систему.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Статья затрат успешно создана"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные в запросе")
    })
    public ExpenseItemDto createExpenseItem(
            @RequestBody ExpenseItemCreateDto dto) {
        return expenseItemService.createExpenseItem(dto);
    }

    @GetMapping("/{apartmentId}")
    @Operation(summary = "Получить статьи затрат по квартире",
            description = "Возвращает список всех статей затрат, связанных с указанной квартирой.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Статьи затрат успешно получены"),
            @ApiResponse(responseCode = "404", description = "Квартира не найдена")
    })
    public List<ExpenseItemDto> getApartmentExpenseItem(
            @Parameter(description = "Идентификатор квартиры", required = true)
            @PathVariable UUID apartmentId) {
        return expenseItemService.getApartmentExpenseItem(apartmentId);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Обновить статью затрат", description = "Обновляет данные существующей статьи затрат по её ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Статья затрат успешно обновлена"),
            @ApiResponse(responseCode = "404", description = "Статья затрат не найдена"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные в запросе")
    })
    public ExpenseItemDto updateExpenseItem(
            @Parameter(description = "Идентификатор статьи затрат", required = true)
            @PathVariable UUID id,
            @RequestBody ExpenseItemUpdateDto dto) {
        return expenseItemService.updateExpenseItem(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Удалить статью затрат", description = "Удаляет статью затрат по её ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Статья затрат успешно удалена"),
            @ApiResponse(responseCode = "404", description = "Статья затрат не найдена")
    })
    public void deleteExpenseItem(
            @Parameter(description = "Идентификатор статьи затрат", required = true)
            @PathVariable UUID id) {
        expenseItemService.deleteExpenseItem(id);
    }
}
