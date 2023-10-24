package com.coro.coro.schedule.dto.response;

import com.coro.coro.schedule.domain.Schedule;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class ScheduleDTO {

    private final Long id;
    private final String title;
    private final String content;
    private final LocalDate theDay;

    public ScheduleDTO(final Schedule schedule) {
        this.id = schedule.getId();
        this.title = schedule.getTitle();
        this.content = schedule.getContent();
        this.theDay = schedule.getTheDay();
    }
}
