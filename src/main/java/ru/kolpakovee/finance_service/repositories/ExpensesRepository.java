package ru.kolpakovee.finance_service.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.kolpakovee.finance_service.entities.ExpensesEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface ExpensesRepository extends JpaRepository<ExpensesEntity, UUID> {

    @Query("SELECT e FROM ExpensesEntity e " +
            "JOIN ExpenseItemEntity ei ON e.itemId = ei.id " +
            "WHERE ei.apartmentId = :apartmentId " +
            "AND (:start IS NULL OR e.createdDate >= :start) " +
            "AND (:end IS NULL OR e.createdDate <= :end)")
    List<ExpensesEntity> findByApartmentIdAndPeriod(
            @Param("apartmentId") UUID apartmentId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);
}
