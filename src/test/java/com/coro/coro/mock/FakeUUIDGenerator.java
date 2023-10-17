package com.coro.coro.mock;

import com.coro.coro.common.service.port.UUIDHolder;

public class FakeUUIDGenerator implements UUIDHolder {

    @Override
    public String generateUUID() {
        return "uid".repeat(30);
    }
}
