package ru.kolpakovee.finance_service.utiles;

import lombok.experimental.UtilityClass;
import ru.kolpakovee.finance_service.records.PeriodRange;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;

/**
 * Утилитный класс для работы с датами.
 */
@UtilityClass
public class DateTimeUtils {

    /**
     * Принимает period в формате YYYYMM и возвращает диапазон дат (начало и конец месяца).
     *
     * @param period период в формате YYYYMM, например, 202401
     * @return PeriodRange с start (начало месяца) и end (конец месяца)
     */
    public static PeriodRange getPeriodRange(int period) {
        int year = period / 100;
        int month = period % 100;
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = startDate.with(TemporalAdjusters.lastDayOfMonth()).atTime(LocalTime.MAX);
        return new PeriodRange(startDateTime, endDateTime);
    }
}