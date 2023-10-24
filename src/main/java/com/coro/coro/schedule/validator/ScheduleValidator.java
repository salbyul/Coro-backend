package com.coro.coro.schedule.validator;

import com.coro.coro.schedule.domain.Schedule;
import com.coro.coro.schedule.exception.ScheduleException;

import java.time.LocalDate;

import static com.coro.coro.common.response.error.ErrorType.*;

public class ScheduleValidator {

    public static void validateSchedule(final Schedule schedule, final LocalDate comparableDate) {
        validateTitle(schedule.getTitle());
        validateContent(schedule.getContent());
        validateTheDay(schedule.getTheDay(), comparableDate);
    }

    private static void validateTitle(final String title) {
        if (isEmpty(title) || title.length() < 1 || title.length() > 100) {
            throw new ScheduleException(SCHEDULE_NOT_VALID_TITLE);
        }
    }

    private static boolean isEmpty(final String value) {
        return value == null || value.length() == 0;
    }

    private static void validateContent(final String content) {
        if (content.length() > 1000) {
            throw new ScheduleException(SCHEDULE_NOT_VALID_CONTENT);
        }
    }

    private static void validateTheDay(final LocalDate theDay, final LocalDate comparableDate) {
        if (theDay.isBefore(comparableDate)) {
            throw new ScheduleException(SCHEDULE_NOT_VALID_THE_DAY);
        }
    }
}
