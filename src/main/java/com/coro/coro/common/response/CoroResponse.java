package com.coro.coro.common.response;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public abstract class CoroResponse {

    private final String dateTime;

    protected CoroResponse() {
        this.dateTime = LocalDateTime.now().toString();
    }
}
