package com.coro.coro.mock;

import com.coro.coro.common.service.port.DateTimeHolder;

public class FakeDateTime implements DateTimeHolder {

    @Override
    public String now() {
        return "2023-10-15";
    }
}
