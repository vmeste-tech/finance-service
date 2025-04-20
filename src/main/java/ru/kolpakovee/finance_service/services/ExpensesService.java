package ru.kolpakovee.finance_service.services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.kolpakovee.finance_service.clients.UserServiceClient;
import ru.kolpakovee.finance_service.entities.ExpensesEntity;
import ru.kolpakovee.finance_service.mappers.ExpensesMapper;
import ru.kolpakovee.finance_service.records.CreateExpenseRequest;
import ru.kolpakovee.finance_service.records.ExpensesDto;
import ru.kolpakovee.finance_service.repositories.ExpensesRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ExpensesService {

    private final ExpensesRepository expensesRepository;
    private final UserServiceClient userServiceClient;

    public List<ExpensesDto> getExpensesByPeriod(LocalDateTime start, LocalDateTime end) {
        if (start != null && end != null && start.isAfter(end)) {
            throw new IllegalArgumentException("Некорректный временной интервал");
        }

        UUID apartmentId = userServiceClient.getApartmentByToken().apartmentId();

        return expensesRepository.findByApartmentIdAndPeriod(apartmentId, start, end)
                .stream()
                .map(ExpensesMapper.INSTANCE::toDto)
                .toList();
    }

    @Transactional
    public ExpensesDto createExpense(CreateExpenseRequest request) {
        ExpensesEntity expensesEntity = new ExpensesEntity();
        expensesEntity.setAmount(request.amount());
        expensesEntity.setDescription(request.description());
        expensesEntity.setUserId(userServiceClient.getUserInfo().id());
        expensesEntity.setCreatedDate(LocalDateTime.now());
        // TODO: save photo to S3
        // expensesEntity.setPhotoUrl("");
        expensesEntity.setItemId(request.itemId());

        return ExpensesMapper.INSTANCE.toDto(expensesRepository.save(expensesEntity));
    }
}
