package com.coro.coro.common.service.adaptor;

import com.coro.coro.common.service.port.DateTimeHolder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class DateTimeImpl implements DateTimeHolder {

    @Override
    public String now() {
        return LocalDateTime.now().toString();
    }
}
