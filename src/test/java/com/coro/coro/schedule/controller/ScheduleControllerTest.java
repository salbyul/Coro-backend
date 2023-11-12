package com.coro.coro.schedule.controller;

import com.coro.coro.common.response.APIResponse;
import com.coro.coro.member.domain.Member;
import com.coro.coro.member.dto.request.MemberRegisterRequest;
import com.coro.coro.member.service.User;
import com.coro.coro.mock.FakeContainer;
import com.coro.coro.moim.dto.request.MoimRegisterRequest;
import com.coro.coro.moim.dto.request.MoimTagRequest;
import com.coro.coro.schedule.domain.Schedule;
import com.coro.coro.schedule.dto.request.ScheduleRegisterRequest;
import com.coro.coro.schedule.dto.response.ScheduleDTO;
import com.coro.coro.schedule.dto.response.ScheduleResponse;
import com.coro.coro.schedule.exception.ScheduleException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.coro.coro.common.response.error.ErrorType.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertAll;

class ScheduleControllerTest {

    @Test
    @DisplayName("일정 등록")
    void register() throws IOException {
        FakeContainer container = new FakeContainer();

//        회원가입
        Long leaderId = container.memberService.register(new MemberRegisterRequest("asdf@asdf.com", "asdf1234!@", "닉네임"));

//        모임 생성
        Long moimId = container.moimService.register(
                new MoimRegisterRequest("모임", "모임 설명", "mixed", true),
                new MoimTagRequest(),
                new ArrayList<>(),
                null,
                leaderId
        );

//        일정 생성
        Member member = container.memberRepository.findById(leaderId)
                .orElseThrow(() -> new ScheduleException(MEMBER_NOT_FOUND));

        User user = User.mappingUserDetails(member);

        LocalDate date = LocalDate.now();
        APIResponse response = container.scheduleController.register(moimId, new ScheduleRegisterRequest("일정 제목", "일정 내용", date), user);
        Long scheduleId = (Long) response.getBody().get("scheduleId");

//        검증
        Schedule schedule = container.scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new ScheduleException(SCHEDULE_NOT_FOUND));

        assertAll(
                () -> assertThat(schedule.getMoim().getId()).isEqualTo(moimId),
                () -> assertThat(schedule.getTitle()).isEqualTo("일정 제목"),
                () -> assertThat(schedule.getContent()).isEqualTo("일정 내용"),
                () -> assertThat(schedule.getTheDay()).isEqualTo(date)
        );
    }

    @Test
    @DisplayName("월별 일정 획득")
    void getMonthlySchedule() throws IOException {
        FakeContainer container = new FakeContainer();

//        회원가입
        Long leaderId = container.memberService.register(new MemberRegisterRequest("asdf@asdf.com", "asdf1234!@", "닉네임"));

//        모임 생성
        Long moimId = container.moimService.register(
                new MoimRegisterRequest("모임", "모임 설명", "mixed", true),
                new MoimTagRequest(),
                new ArrayList<>(),
                null,
                leaderId
        );

//        일정 생성
        LocalDate scheduleDate1 = LocalDate.of(2024, 1, 1);
        container.scheduleService.register(new ScheduleRegisterRequest("일정1", "일정 내용1", scheduleDate1), moimId, leaderId);
        LocalDate scheduleDate2 = LocalDate.of(2024, 1, 31);
        container.scheduleService.register(new ScheduleRegisterRequest("일정2", "일정 내용2", scheduleDate2), moimId, leaderId);

//        일정 획득
        Member member = container.memberRepository.findById(leaderId)
                .orElseThrow(() -> new ScheduleException(MEMBER_NOT_FOUND));

        User user = User.mappingUserDetails(member);

        APIResponse response = container.scheduleController.getMonthlySchedule(moimId, LocalDate.of(2024, 1, 1), user);
        ScheduleResponse scheduleResponse = (ScheduleResponse) response.getBody().get("schedule");
        List<ScheduleDTO> scheduleDTOList = scheduleResponse.getScheduleDTOList();

//        검증
        assertAll(
                () -> assertThat(scheduleDTOList).size().isEqualTo(2),
                () -> assertThat(scheduleDTOList).extracting(ScheduleDTO::getTitle).containsExactlyInAnyOrder("일정1", "일정2"),
                () -> assertThat(scheduleDTOList).extracting(ScheduleDTO::getContent).containsExactlyInAnyOrder("일정 내용1", "일정 내용2"),
                () -> assertThat(scheduleDTOList).extracting(ScheduleDTO::getTheDay).containsExactlyInAnyOrder(scheduleDate1, scheduleDate2)
        );
    }

    @Test
    @DisplayName("특정 날짜의 일정 획득")
    void getSchedules() throws IOException {
        FakeContainer container = new FakeContainer();

//        회원가입
        Long leaderId = container.memberService.register(new MemberRegisterRequest("asdf@asdf.com", "asdf1234!@", "닉네임"));

//        모임 생성
        Long moimId = container.moimService.register(
                new MoimRegisterRequest("모임", "모임 설명", "mixed", true),
                new MoimTagRequest(),
                new ArrayList<>(),
                null,
                leaderId
        );

//        일정 생성
        container.scheduleService.register(new ScheduleRegisterRequest("일정1", "일정 내용1", LocalDate.of(9999, 10, 10)), moimId, leaderId);

//        일정 획득
        Member member = container.memberRepository.findById(leaderId)
                .orElseThrow(() -> new ScheduleException(MEMBER_NOT_FOUND));

        User user = User.mappingUserDetails(member);

        APIResponse response = container.scheduleController.getSchedules(moimId, LocalDate.of(9999, 10, 10), user);
        ScheduleResponse scheduleResponse = (ScheduleResponse) response.getBody().get("schedule");
        List<ScheduleDTO> scheduleDTOList = scheduleResponse.getScheduleDTOList();

//        검증
        assertAll(
                () -> assertThat(scheduleDTOList).size().isEqualTo(1),
                () -> assertThat(scheduleDTOList).extracting(ScheduleDTO::getTitle).containsExactlyInAnyOrder("일정1"),
                () -> assertThat(scheduleDTOList).extracting(ScheduleDTO::getContent).containsExactlyInAnyOrder("일정 내용1"),
                () -> assertThat(scheduleDTOList).extracting(ScheduleDTO::getTheDay).containsExactlyInAnyOrder(LocalDate.of(9999, 10, 10))
        );
    }

    @Test
    @DisplayName("일정 삭제")
    void deleteSchedule() throws IOException {
        FakeContainer container = new FakeContainer();

//        회원가입
        Long leaderId = container.memberService.register(new MemberRegisterRequest("asdf@asdf.com", "asdf1234!@", "닉네임"));

//        모임 생성
        Long moimId = container.moimService.register(
                new MoimRegisterRequest("모임", "모임 설명", "mixed", true),
                new MoimTagRequest(),
                new ArrayList<>(),
                null,
                leaderId
        );

//        일정 생성
        Long scheduleId = container.scheduleService.register(
                new ScheduleRegisterRequest("일정1", "일정 내용1", LocalDate.of(9999, 10, 10)),
                moimId,
                leaderId
        );

//        일정 삭제
        Member member = container.memberRepository.findById(leaderId)
                .orElseThrow(() -> new ScheduleException(MEMBER_NOT_FOUND));

        User user = User.mappingUserDetails(member);

        container.scheduleController.deleteSchedule(scheduleId, user);

//        검증
        Optional<Schedule> optionalSchedule = container.scheduleRepository.findById(scheduleId);

        assertThat(optionalSchedule.isEmpty()).isTrue();
    }
}