package com.antopina.schedulingappointmentplanner.HomePage.calendar;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class CalendarUtils {
    public static LocalDate selectedDate;

    public static String formattedDate(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return date.format(formatter);
    }

    public static String formattedTime(LocalTime time) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm a");
        return time.format(formatter);
    }

    public static String formattedShortTime(LocalTime time) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        return time.format(formatter);
    }

    public static String monthYearFromDate(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM yyy");
        return date.format(formatter);
    }

    public static String monthDayFromDate(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM d");
        return date.format(formatter);
    }

    public static ArrayList<LocalDate> daysInMonthArray() {
        ArrayList<LocalDate> daysInMonthArray = new ArrayList<>();

        // Get YearMonth and details for the current, previous, and next months
        YearMonth yearMonth = YearMonth.from(selectedDate);
        YearMonth prevYearMonth = YearMonth.from(selectedDate.minusMonths(1));
        LocalDate firstOfMonth = selectedDate.withDayOfMonth(1);

        int daysInMonth = yearMonth.lengthOfMonth();
        int prevMonthDays = prevYearMonth.lengthOfMonth();

        // Adjust dayOfWeek to start with Sunday (Sunday = 0, Monday = 1, ..., Saturday = 6)
        int dayOfWeek = firstOfMonth.getDayOfWeek().getValue() % 7;

        // Add previous month's days
        for (int i = dayOfWeek; i > 0; i--) {
            daysInMonthArray.add(firstOfMonth.minusDays(i));
        }

        // Add current month's days
        for (int i = 1; i <= daysInMonth; i++) {
            daysInMonthArray.add(LocalDate.of(yearMonth.getYear(), yearMonth.getMonth(), i));
        }

        // Add next month's days to fill remaining slots
        int remainingDays = 42 - daysInMonthArray.size();
        for (int i = 1; i <= remainingDays; i++) {
            daysInMonthArray.add(firstOfMonth.plusDays(daysInMonthArray.size() - dayOfWeek + 1));
        }

        return daysInMonthArray;
    }


    public static ArrayList<LocalDate> daysInWeekArray(LocalDate selectedDate) {
        ArrayList<LocalDate> days = new ArrayList<>();
        LocalDate startOfWeek = selectedDate.minusDays(selectedDate.getDayOfWeek().getValue()); // Start on Monday
        for (int i = 0; i < 7; i++) {
            days.add(startOfWeek.plusDays(i));
        }
        return days;
    }


    private static LocalDate sundayForDate(LocalDate current) {
        return current.minusDays(current.getDayOfWeek().getValue() % 7);
    }

}
