package com.coro.coro.common.service.adaptor;

import com.coro.coro.common.service.port.UUIDHolder;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class UUIDGenerator implements UUIDHolder {

    @Override
    public String generateUUID() {
        return UUID.randomUUID().toString();
    }
}
