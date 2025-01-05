package com.kuro.expensetracker.utils;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;

public class DateTimeUtil {

    public static LocalDateTime getStartOfToday() {
        return LocalDate.now().atTime(0, 0, 0);
    }

    public static LocalDateTime getStartOfDay(LocalDate date) {
        return date.atTime(0, 0, 0);
    }

    public static LocalDateTime getEndOfToday() {
        return LocalDate.now().atTime(23, 59, 59);
    }

    public static LocalDateTime getEndOfDay(LocalDate date) {
        return date.atTime(23, 59, 59);
    }


    public static LocalDateTime getStartOfWeek() {
        return getStartOfDay(LocalDate.now()).with(DayOfWeek.MONDAY);
    }

    public static LocalDateTime getEndOfWeek() {
        return getEndOfDay(LocalDate.now()).with(DayOfWeek.SUNDAY);
    }

    public static LocalDateTime getFirstDayOfYear() {
        int year = LocalDateTime.now().getYear();
        return getStartOfDay(LocalDate.ofYearDay(year, 1));
    }

    public static LocalDateTime getLastDayOfYear() {
        int year = LocalDateTime.now().getYear();
        LocalDate lastDayOfYear = Year.of(year).atDay(Year.of(year).length());
        return getEndOfDay(lastDayOfYear);
    }
}
