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

    /**
     * 일정 등록
     * @param moimId 등록할 일정의 모임 Id 값
     * @param registerRequest 등록할 일정의 데이터
     * @param user 로그인한 유저
     * @return 등록한 일정의 Id 값
     */
    @PostMapping
    @Override
    public APIResponse register(@ModelAttribute(name = "moim") final Long moimId,
                                @RequestBody final ScheduleRegisterRequest registerRequest,
                                @AuthenticationPrincipal final User user) {
        Long scheduleId = scheduleService.register(registerRequest, moimId, user.getId());
        log.info("schdule: {}", registerRequest);
        return APIResponse.create()
                .addObject("scheduleId", scheduleId);
    }

    /**
     * 월별 일정 획득
     * @param moimId 획득할 일정들의 모임 Id 값
     * @param date 획득할 일정의 달
     * @param user 로그인한 유저
     * @return 월별 일정들
     */
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

    /**
     * 특정 날짜의 일정 획득
     * @param moimId 획득할 일정들의 모임 Id 값
     * @param date 획득할 일정의 날짜
     * @param user 로그인한 유저
     * @return 해당 날짜의 일정들
     */
    @GetMapping
    @Override
    public APIResponse getSchedules(@ModelAttribute(name = "moim") final Long moimId,
                                   @Date final LocalDate date,
                                   @AuthenticationPrincipal final User user) {
        List<ScheduleDTO> scheduleDTOList = scheduleService.getSchedules(user.getId(), moimId, date);
        ScheduleResponse scheduleResponse = new ScheduleResponse(scheduleDTOList);
        return APIResponse.create()
                .addObject("schedule", scheduleResponse);
    }

    /**
     * 일정 삭제
     * @param scheduleId 삭제할 일정의 Id 값
     * @param user 로그인한 유저
     * @return 반환값 없음
     */
    @DeleteMapping
    @Override
    public APIResponse deleteSchedule(@ModelAttribute(name = "schedule") final Long scheduleId, @AuthenticationPrincipal final User user) {
        scheduleService.deleteSchedule(scheduleId, user.getId());
        return APIResponse.create();
    }
}