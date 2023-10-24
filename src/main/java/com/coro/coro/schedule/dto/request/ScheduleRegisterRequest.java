package com.coro.coro.schedule.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class ScheduleRegisterRequest {

    private String title;
    private String content;
    private LocalDate date;
}
