package com.coro.coro.schedule.exception;

import com.coro.coro.common.exception.GlobalException;
import com.coro.coro.common.response.error.ErrorType;

public class ScheduleException extends GlobalException {

    private static final String SCHEDULE = "Schedule";

    public ScheduleException(final ErrorType errorType) {
        super(SCHEDULE, errorType);
    }
}
