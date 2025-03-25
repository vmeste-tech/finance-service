package ru.kolpakovee.finance_service.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatcher;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.kolpakovee.finance_service.clients.PenaltyServiceClient;
import ru.kolpakovee.finance_service.clients.UserServiceClient;
import ru.kolpakovee.finance_service.entities.DebtEntity;
import ru.kolpakovee.finance_service.entities.ExpensesEntity;
import ru.kolpakovee.finance_service.enums.DebtStatus;
import ru.kolpakovee.finance_service.records.*;
import ru.kolpakovee.finance_service.repositories.DebtsRepository;
import ru.kolpakovee.finance_service.repositories.ExpensesRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FinanceServiceTest {

    @Mock
    private ExpensesRepository expensesRepository;

    @Mock
    private DebtsRepository debtsRepository;

    @Mock
    private PenaltyServiceClient penaltyServiceClient;

    @Mock
    private UserServiceClient userServiceClient;

    @InjectMocks
    private FinanceService financeService;

    /**
     * Вспомогательный класс для мокирования возвращаемого объекта от userServiceClient.getApartmentByToken().
     * Предполагается, что у такого объекта есть метод users() возвращающий List<UserInfoDto>.
     */
    static class ApartmentInfoDtoStub {
        private final List<UserInfoDto> users;

        ApartmentInfoDtoStub(List<UserInfoDto> users) {
            this.users = users;
        }

        public List<UserInfoDto> users() {
            return users;
        }
    }

    /**
     * Тест для случая, когда месяц уже завершён (текущая дата позже range.end()) и в БД найдены рассчитанные долги.
     */
    @Test
    void testGetUserDebts_whenMonthEnded_debtsExist() {
        // Выбираем период в прошлом, например 202201 (январь 2022)
        int period = 202201;

        // Создаем тестовый список пользователей
        UserInfoDto user1 = new UserInfoDto(UUID.randomUUID(), "Alice", "Smith", "", "", LocalDateTime.now(), "");
        UserInfoDto user2 = new UserInfoDto(UUID.randomUUID(), "Bob", "Jones", "", "", LocalDateTime.now(), "");
        List<UserInfoDto> users = List.of(user1, user2);

        // Мокаем вызов userServiceClient.getApartmentByToken()
        ApartmentInfo apartmentInfo = ApartmentInfo.builder()
                .apartmentId(UUID.randomUUID())
                .name("Test Apartment")
                .address("Test Address")
                .users(users)
                .build();
        when(userServiceClient.getApartmentByToken()).thenReturn(apartmentInfo);

        // Построим карту userNames внутри сервиса (на основе users)
        // Мокаем debtsRepository, возвращая список долгов
        DebtEntity debtEntity = new DebtEntity();
        debtEntity.setPeriod(period);
        debtEntity.setAmount(100.0);
        debtEntity.setStatus(DebtStatus.UNPAID);
        debtEntity.setDebtorId(user1.id());
        debtEntity.setCreditorId(user2.id());
        List<DebtEntity> debtEntities = List.of(debtEntity);
        when(debtsRepository.findAllByDebtorIdInAndPeriod(
                anySet(), eq(period)))
                .thenReturn(debtEntities);

        // Для выбранного периода (январь 2022) range.end() будет давно, значит LocalDateTime.now() > range.end()
        List<DebtDto> result = financeService.getUserDebts(UUID.randomUUID(), period);

        assertNotNull(result);
        assertEquals(1, result.size());
        DebtDto dto = result.getFirst();
        // Проверяем, что имена заменены через карту userNames
        assertEquals("Alice", dto.debtor());
        assertEquals("Bob", dto.creditor());
        assertEquals(100.0, dto.amount());
        assertEquals(DebtStatus.UNPAID, dto.status());
        assertEquals(period, dto.period());

        // Проверяем вызов репозитория долгов
        verify(debtsRepository).findAllByDebtorIdInAndPeriod(anySet(), eq(period));
    }

    /**
     * Тест для случая, когда месяц ещё не завершён и производится расчёт долга.
     */
    @Test
    void testGetUserDebts_whenMonthNotEnded_calculation() {
        // Выбираем период в будущем, например 202503 (март 2025)
        int period = 202503;

        // Создаем тестовый список пользователей
        UserInfoDto user1 = new UserInfoDto(UUID.randomUUID(), "Alice", "Smith", "", "", LocalDateTime.now(), "");
        UserInfoDto user2 = new UserInfoDto(UUID.randomUUID(), "Bob", "Jones", "", "", LocalDateTime.now(), "");
        UserInfoDto user3 = new UserInfoDto(UUID.randomUUID(), "Charlie", "Brown", "", "", LocalDateTime.now(), "");
        List<UserInfoDto> users = List.of(user1, user2, user3);

        // Мокаем вызов userServiceClient.getApartmentByToken()
        ApartmentInfo apartmentInfo = ApartmentInfo.builder()
                .apartmentId(UUID.randomUUID())
                .name("Test Apartment")
                .address("Test Address")
                .users(users)
                .build();
        when(userServiceClient.getApartmentByToken()).thenReturn(apartmentInfo);

        // Для будущего периода LocalDateTime.now() не больше, чем range.end(), поэтому расчёт производится.
        // Мокаем расходы
        ExpensesEntity exp1 = new ExpensesEntity();
        exp1.setUserId(user1.id());
        exp1.setAmount(600.0);
        ExpensesEntity exp2 = new ExpensesEntity();
        exp2.setUserId(user2.id());
        exp2.setAmount(900.0);
        ExpensesEntity exp3 = new ExpensesEntity();
        exp3.setUserId(user3.id());
        exp3.setAmount(30.0);
        List<ExpensesEntity> expenses = List.of(exp1, exp2, exp3);
        when(expensesRepository.findByApartmentIdAndPeriod(any(UUID.class), any(), any()))
                .thenReturn(expenses);

        // Мокаем штрафы
        PenaltyResponse pen1 = PenaltyResponse.builder()
                .user(user1)
                .fineAmount(90.0)
                .assignedDate(LocalDateTime.now().minusDays(1))
                .build();
        PenaltyResponse pen2 = PenaltyResponse.builder()
                .user(user2)
                .fineAmount(120.0)
                .assignedDate(LocalDateTime.now().minusDays(1))
                .build();
        PenaltyResponse pen3 = PenaltyResponse.builder()
                .user(user3)
                .fineAmount(0.0)
                .assignedDate(LocalDateTime.now().minusDays(1))
                .build();
        List<PenaltyResponse> penalties = List.of(pen1, pen2, pen3);
        when(penaltyServiceClient.getApartmentPenalties(any(UUID.class)))
                .thenReturn(penalties);

        // Чтобы не зависеть от логики DebtCalculator, мокируем его статический метод
        try (MockedStatic<DebtCalculator> debtCalcMock =
                     mockStatic(DebtCalculator.class)) {

            // Формируем участников, как они будут агрегированы
            Participant p1 = new Participant(user1.id(), user1.name(), 600.0, 90.0);
            Participant p2 = new Participant(user2.id(), user2.name(), 900.0, 120.0);
            Participant p3 = new Participant(user3.id(), user3.name(), 30.0, 0.0);
            List<Participant> participants = List.of(p1, p2, p3);

            // Создадим фиктивный Transfer – допустим, p2 должен перевести p1 сумму 50.0
            Transfer transfer = new Transfer(p2, p1, 50.0);
            List<Transfer> transfers = List.of(transfer);
            debtCalcMock.when(() -> DebtCalculator.calculateDebts(anyList())).thenReturn(transfers);

            // Вызываем тестируемый метод
            List<DebtDto> result = financeService.getUserDebts(UUID.randomUUID(), period);

            // Проверяем, что метод получил данные по расходам и штрафам
            verify(expensesRepository).findByApartmentIdAndPeriod(any(UUID.class), any(), any());
            verify(penaltyServiceClient).getApartmentPenalties(any(UUID.class));

            // Проверяем, что сохранение долгов было вызвано с корректным значением
            verify(debtsRepository).saveAll(argThat(new ArgumentMatcher<List<DebtEntity>>() {
                @Override
                public boolean matches(List<DebtEntity> entities) {
                    if (entities == null || entities.size() != 1) {
                        return false;
                    }
                    DebtEntity entity = entities.getFirst();
                    return entity.getAmount() == 50.0 &&
                            entity.getPeriod() == period &&
                            entity.getStatus() == DebtStatus.UNPAID &&
                            entity.getDebtorId().equals(p2.getUserId()) &&
                            entity.getCreditorId().equals(p1.getUserId());
                }
            }));

            // Проверяем возвращаемый результат
            assertNotNull(result);
            assertEquals(1, result.size());
            DebtDto dto = result.getFirst();
            assertEquals(p2.getName(), dto.debtor());
            assertEquals(p1.getName(), dto.creditor());
            assertEquals(50.0, dto.amount());
            assertEquals(DebtStatus.UNPAID, dto.status());
            assertEquals(period, dto.period());
        }
    }
}
