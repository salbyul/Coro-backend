package com.coro.coro.schedule.controller;

import com.coro.coro.common.response.APIResponse;
import com.coro.coro.member.domain.Member;
import com.coro.coro.member.dto.request.MemberRegisterRequest;
import com.coro.coro.member.service.User;
import com.coro.coro.mock.FakeContainer;
import com.coro.coro.moim.dto.request.MoimRegisterRequest;
import com.coro.coro.schedule.domain.Schedule;
import com.coro.coro.schedule.dto.request.ScheduleRegisterRequest;
import com.coro.coro.schedule.exception.ScheduleException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.LocalDate;

import static com.coro.coro.common.response.error.ErrorType.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertAll;

class ScheduleControllerTest {

    @Test
    @DisplayName("일정 등록")
    void register() throws IOException {
        FakeContainer container = new FakeContainer();

        Long leaderId = container.memberService.register(new MemberRegisterRequest("asdf@asdf.com", "asdf1234!@", "닉네임"));

        Long moimId = container.moimService.register(
                new MoimRegisterRequest("모임", "모임 설명", "mixed", true),
                null,
                null,
                null,
                leaderId
        );

        Member member = container.memberRepository.findById(leaderId)
                .orElseThrow(() -> new ScheduleException(MEMBER_NOT_FOUND));

        User user = User.mappingUserDetails(member);

        LocalDate date = LocalDate.now();
        APIResponse response = container.scheduleController.register(moimId, new ScheduleRegisterRequest("일정 제목", "일정 내용", date), user);
        Long scheduleId = (Long) response.getBody().get("scheduleId");

        Schedule schedule = container.scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new ScheduleException(SCHEDULE_NOT_FOUND));

        assertAll(
                () -> assertThat(schedule.getMoim().getId()).isEqualTo(moimId),
                () -> assertThat(schedule.getTitle()).isEqualTo("일정 제목"),
                () -> assertThat(schedule.getContent()).isEqualTo("일정 내용"),
                () -> assertThat(schedule.getTheDay()).isEqualTo(date)
        );
    }
}