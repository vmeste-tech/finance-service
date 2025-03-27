package ru.kolpakovee.finance_service.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.kolpakovee.finance_service.entities.ExpenseItemEntity;

import java.util.List;
import java.util.UUID;

@Repository
public interface ExpenseItemRepository extends JpaRepository<ExpenseItemEntity, UUID> {

    List<ExpenseItemEntity> findAllByApartmentId(UUID apartmentId);
}
