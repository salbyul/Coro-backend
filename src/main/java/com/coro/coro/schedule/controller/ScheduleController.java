package com.coro.coro.schedule.controller;

import com.coro.coro.common.annotation.Date;
import com.coro.coro.common.response.APIResponse;
import com.coro.coro.member.service.User;
import com.coro.coro.schedule.dto.request.ScheduleRegisterRequest;
import com.coro.coro.schedule.dto.response.ScheduleDTO;
import com.coro.coro.schedule.dto.response.ScheduleResponse;
import com.coro.coro.schedule.service.ScheduleService;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@Builder
@RequestMapping("/api/schedules")
public class ScheduleController implements ScheduleControllerDocs {

    private final ScheduleService scheduleService;

    @PostMapping
    @Override
    public APIResponse register(@ModelAttribute(name = "moim") final Long moimId,
                                @RequestBody final ScheduleRegisterRequest registerRequest,
                                @AuthenticationPrincipal final User user) {
        Long scheduleId = scheduleService.register(registerRequest, moimId, user.getId());
        return APIResponse.create()
                .addObject("scheduleId", scheduleId);
    }

    @GetMapping("/month")
    @Override
    public APIResponse getMonthlySchedule(@ModelAttribute(name = "moim") final Long moimId,
                                          @Date final LocalDate date,
                                          @AuthenticationPrincipal final User user) {
        List<ScheduleDTO> scheduleDTOList = scheduleService.getMonthlySchedule(user.getId(), moimId, date);
        ScheduleResponse scheduleResponse = new ScheduleResponse(scheduleDTOList);
        return APIResponse.create()
                .addObject("schedule", scheduleResponse);
    }

    @GetMapping
    @Override
    public APIResponse getSchedules(@ModelAttribute(name = "moim") final Long moimId,
                                   @Date final LocalDate date,
                                   @AuthenticationPrincipal final User user) {
        List<ScheduleDTO> scheduleDTOList = scheduleService.getScheduleDTOs(user.getId(), moimId, date);
        ScheduleResponse scheduleResponse = new ScheduleResponse(scheduleDTOList);
        return APIResponse.create()
                .addObject("schedule", scheduleResponse);
    }

    @DeleteMapping
    @Override
    public APIResponse deleteSchedule(@ModelAttribute(name = "schedule") final Long scheduleId, @AuthenticationPrincipal final User user) {
        scheduleService.deleteSchedule(scheduleId, user.getId());
        return APIResponse.create();
    }
}