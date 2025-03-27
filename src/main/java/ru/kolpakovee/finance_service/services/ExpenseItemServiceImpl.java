package ru.kolpakovee.finance_service.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.kolpakovee.finance_service.entities.ExpenseItemEntity;
import ru.kolpakovee.finance_service.exceptions.ResourceNotFoundException;
import ru.kolpakovee.finance_service.mappers.ExpenseItemMapper;
import ru.kolpakovee.finance_service.records.ExpenseItemCreateDto;
import ru.kolpakovee.finance_service.records.ExpenseItemDto;
import ru.kolpakovee.finance_service.records.ExpenseItemUpdateDto;
import ru.kolpakovee.finance_service.repositories.ExpenseItemRepository;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ExpenseItemServiceImpl implements ExpenseItemService {

    private final ExpenseItemRepository repository;

    @Override
    public ExpenseItemDto createExpenseItem(ExpenseItemCreateDto dto) {
        ExpenseItemEntity entity = new ExpenseItemEntity();
        entity.setApartmentId(dto.apartmentId());
        entity.setName(dto.name());
        entity.setDescription(dto.description());
        ExpenseItemEntity saved = repository.save(entity);
        return ExpenseItemMapper.INSTANCE.toDto(saved);
    }

    @Override
    public ExpenseItemDto updateExpenseItem(UUID id, ExpenseItemUpdateDto dto) {
        ExpenseItemEntity entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Expense item not found"));
        entity.setName(dto.name());
        entity.setDescription(dto.description());
        ExpenseItemEntity updated = repository.save(entity);
        return ExpenseItemMapper.INSTANCE.toDto(updated);
    }

    @Override
    public void deleteExpenseItem(UUID id) {
        repository.deleteById(id);
    }

    @Override
    public List<ExpenseItemDto> getApartmentExpenseItem(UUID apartmentId) {
        return repository.findAllByApartmentId(apartmentId).stream()
                .map(ExpenseItemMapper.INSTANCE::toDto)
                .toList();
    }
}

