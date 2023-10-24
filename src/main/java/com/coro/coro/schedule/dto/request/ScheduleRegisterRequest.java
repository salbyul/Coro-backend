package com.coro.coro.schedule.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ScheduleRegisterRequest {

    private String title;
    private String content;
    private LocalDate date;
}
