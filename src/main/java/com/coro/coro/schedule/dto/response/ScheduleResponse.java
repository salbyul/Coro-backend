package com.coro.coro.schedule.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class ScheduleResponse {

    private List<ScheduleDTO> scheduleDTOList;
}
