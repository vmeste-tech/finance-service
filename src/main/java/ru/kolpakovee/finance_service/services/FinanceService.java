package ru.kolpakovee.finance_service.services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.kolpakovee.finance_service.clients.PenaltyServiceClient;
import ru.kolpakovee.finance_service.clients.UserServiceClient;
import ru.kolpakovee.finance_service.entities.DebtEntity;
import ru.kolpakovee.finance_service.entities.ExpensesEntity;
import ru.kolpakovee.finance_service.enums.DebtStatus;
import ru.kolpakovee.finance_service.exceptions.DebtPaymentException;
import ru.kolpakovee.finance_service.exceptions.ResourceNotFoundException;
import ru.kolpakovee.finance_service.mappers.DebtMapper;
import ru.kolpakovee.finance_service.records.*;
import ru.kolpakovee.finance_service.repositories.DebtsRepository;
import ru.kolpakovee.finance_service.repositories.ExpensesRepository;
import ru.kolpakovee.finance_service.utiles.DateTimeUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FinanceService {

    private final ExpensesRepository expensesRepository;
    private final DebtsRepository debtsRepository;
    private final PenaltyServiceClient penaltyServiceClient;
    private final UserServiceClient userServiceClient;

    @Transactional
    public List<DebtDto> getUserDebts(UUID apartmentId, int period) {
        PeriodRange periodRange = DateTimeUtils.getPeriodRange(period);
        List<UserInfoDto> users = userServiceClient.getApartmentByToken().users();
        Map<UUID, String> userNames = buildUserNamesMap(users);

        // Если период завершён, возвращаем долги из БД
        if (LocalDateTime.now().isAfter(periodRange.end())) {
            List<DebtEntity> existingDebts = debtsRepository.findAllByDebtorIdInAndPeriod(userNames.keySet(), period);
            if (!existingDebts.isEmpty()) {
                return existingDebts.stream()
                        .map(entity -> DebtMapper.INSTANCE.toDto(entity, userNames))
                        .collect(Collectors.toList());
            }
        }

        // Удаляем старые долги для данного периода, если они имеются
        debtsRepository.deleteAllByDebtorIdInAndPeriod(userNames.keySet(), period);

        // Агрегируем финансовые данные участников
        Map<UUID, Participant> participantMap = aggregateUserFinances(apartmentId, periodRange, users);
        List<Participant> participants = new ArrayList<>(participantMap.values());

        // Вычисляем долги с использованием калькулятора
        List<Transfer> transfers = DebtCalculator.calculateDebts(participants);

        List<DebtEntity> newDebtEntities = transfers.stream()
                .map(t -> {
                    DebtEntity entity = new DebtEntity();
                    entity.setAmount(t.getAmount());
                    entity.setStatus(DebtStatus.UNPAID);
                    entity.setPeriod(period);
                    entity.setDebtorId(t.getDebtor().getUserId());
                    entity.setCreditorId(t.getCreditor().getUserId());
                    return entity;
                })
                .collect(Collectors.toList());

        newDebtEntities = debtsRepository.saveAll(newDebtEntities);
        return newDebtEntities.stream()
                .map(entity -> DebtMapper.INSTANCE.toDto(entity, userNames))
                .collect(Collectors.toList());
    }

    public List<Participant> getUserFinances(UUID apartmentId, int period) {
        PeriodRange periodRange = DateTimeUtils.getPeriodRange(period);
        List<UserInfoDto> users = userServiceClient.getApartmentByToken().users();
        Map<UUID, Participant> participantMap = aggregateUserFinances(apartmentId, periodRange, users);
        return new ArrayList<>(participantMap.values());
    }

    @Transactional
    public DebtDto payDebt(UUID debtId) {
        DebtEntity entity = debtsRepository.findById(debtId)
                .orElseThrow(() -> new ResourceNotFoundException("Debt with ID " + debtId + " not found."));

        PeriodRange periodRange = DateTimeUtils.getPeriodRange(entity.getPeriod());
        if (LocalDateTime.now().isBefore(periodRange.end())) {
            throw new DebtPaymentException("Debt cannot be paid until period end.");
        }

        entity.setStatus(DebtStatus.PAID);
        debtsRepository.save(entity);

        List<UserInfoDto> users = userServiceClient.getApartmentByToken().users();
        Map<UUID, String> userNames = buildUserNamesMap(users);
        return DebtMapper.INSTANCE.toDto(entity, userNames);
    }

    // Вспомогательные методы:

    private Map<UUID, String> buildUserNamesMap(List<UserInfoDto> users) {
        return users.stream()
                .collect(Collectors.toMap(UserInfoDto::id, UserInfoDto::name));
    }

    private Map<UUID, Participant> buildParticipantMap(List<UserInfoDto> users) {
        return users.stream()
                .collect(Collectors.toMap(UserInfoDto::id, u -> new Participant(u.id(), u.name(), 0.0, 0.0)));
    }

    /**
     * Агрегирует финансовые данные участников (расходы и штрафы) за указанный период.
     *
     * @param apartmentId Идентификатор квартиры.
     * @param periodRange Период, за который проводится агрегация.
     * @param users       Список пользователей.
     * @return Map, где ключ – идентификатор пользователя, а значение – агрегированные данные Participant.
     */
    private Map<UUID, Participant> aggregateUserFinances(UUID apartmentId, PeriodRange periodRange, List<UserInfoDto> users) {
        List<ExpensesEntity> expenses = expensesRepository.findByApartmentIdAndPeriod(apartmentId, periodRange.start(), periodRange.end());
        List<PenaltyResponse> penalties = penaltyServiceClient.getApartmentPenalties(apartmentId)
                .stream()
                .filter(p -> p.assignedDate().isAfter(periodRange.start()) && p.assignedDate().isBefore(periodRange.end()))
                .collect(Collectors.toList());
        Map<UUID, Participant> participantMap = buildParticipantMap(users);
        updateParticipantsWithExpenses(participantMap, expenses);
        updateParticipantsWithPenalties(participantMap, penalties);
        return participantMap;
    }

    private void updateParticipantsWithExpenses(Map<UUID, Participant> participantMap, List<ExpensesEntity> expenses) {
        expenses.forEach(expense -> {
            UUID userId = expense.getUserId();
            Participant participant = participantMap.get(userId);
            if (participant != null) {
                participant.setExpense(participant.getExpense() + expense.getAmount());
            }
        });
    }

    private void updateParticipantsWithPenalties(Map<UUID, Participant> participantMap, List<PenaltyResponse> penalties) {
        penalties.forEach(penalty -> {
            UUID userId = penalty.user().id();
            Participant participant = participantMap.get(userId);
            if (participant != null) {
                participant.setFine(participant.getFine() + penalty.fineAmount());
            }
        });
    }
}
