package com.coro.coro.application.domain;

import com.coro.coro.application.exception.ApplicationException;

import java.util.Arrays;

import static com.coro.coro.common.response.error.ErrorType.*;

public enum ApplicationStatus {
    REFUSE("refuse"), ACCEPT("accept"), WAIT("wait");

    private final String value;

    ApplicationStatus(final String value) {
        this.value = value;
    }

    public static ApplicationStatus getApplicationStatus(final String value) {
        return Arrays.stream(values())
                .filter(status -> status.value.equals(value))
                .findFirst()
                .orElseThrow(() -> new ApplicationException(APPLICATION_STATUS_NOT_VALID));
    }
}
