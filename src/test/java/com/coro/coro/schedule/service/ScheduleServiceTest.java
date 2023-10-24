package com.coro.coro.schedule.service;

import com.coro.coro.member.domain.Member;
import com.coro.coro.member.domain.MemberRole;
import com.coro.coro.member.dto.request.MemberRegisterRequest;
import com.coro.coro.mock.FakeContainer;
import com.coro.coro.moim.domain.Moim;
import com.coro.coro.moim.domain.MoimMember;
import com.coro.coro.moim.dto.request.MoimRegisterRequest;
import com.coro.coro.schedule.domain.Schedule;
import com.coro.coro.schedule.dto.request.ScheduleRegisterRequest;
import com.coro.coro.schedule.dto.response.ScheduleDTO;
import com.coro.coro.schedule.exception.ScheduleException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import static com.coro.coro.common.response.error.ErrorType.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertAll;

class ScheduleServiceTest {

    @Test
    @DisplayName("정상적인 일정 등록")
    void register() throws IOException {
        FakeContainer container = new FakeContainer();

//        회원가입
        Long leaderId = container.memberService.register(new MemberRegisterRequest("asdf@asdf.com", "asdf1234!@", "닉네임"));
//        모임 생성
        Long moimId = container.moimService.register(
                new MoimRegisterRequest("모임", "모임 설명", "mixed", true),
                null,
                null,
                null,
                leaderId);

//        일정 등록
        ScheduleRegisterRequest scheduleRegisterRequest = new ScheduleRegisterRequest("제목", "내용", LocalDate.now());
        Long savedScheduleId = container.scheduleService.register(scheduleRegisterRequest, moimId, leaderId);

//        검증
        Schedule schedule = container.scheduleRepository.findById(savedScheduleId)
                .orElseThrow(() -> new ScheduleException(SCHEDULE_NOT_FOUND));

        assertAll(
                () -> assertThat(schedule.getMoim().getId()).isEqualTo(moimId),
                () -> assertThat(schedule.getTitle()).isEqualTo(schedule.getTitle()),
                () -> assertThat(schedule.getContent()).isEqualTo(schedule.getContent()),
                () -> assertThat(schedule.getTheDay()).isEqualTo(schedule.getTheDay())
        );
    }

    @Test
    @DisplayName("일정 등록 실패 - 모임이 없는 경우")
    void registerFailByNotValidMoim() throws IOException {
        FakeContainer container = new FakeContainer();

//        회원가입
        Long leaderId = container.memberService.register(new MemberRegisterRequest("asdf@asdf.com", "asdf1234!@", "닉네임"));

//        모임 생성
        container.moimService.register(
                new MoimRegisterRequest("모임", "모임 설명", "mixed", true),
                null,
                null,
                null,
                leaderId);

//        검증
        ScheduleRegisterRequest scheduleRegisterRequest = new ScheduleRegisterRequest("제목", "내용", LocalDate.now());

        assertThatThrownBy(() ->
            container.scheduleService.register(scheduleRegisterRequest, 99999L, leaderId)
        )
                .isInstanceOf(ScheduleException.class)
                .hasMessage(MOIM_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("일정 등록 실패 - 회원이 아닌 경우")
    void registerFailByNotMoimMember() throws IOException {
        FakeContainer container = new FakeContainer();

//        회원가입
        Long leaderId = container.memberService.register(new MemberRegisterRequest("asdf@asdf.com", "asdf1234!@", "닉네임"));

//        모임에 가입되지 않은 회원가입
        Long memberId = container.memberService.register(new MemberRegisterRequest("a@a.com", "asdf1234!@", "닉네임2"));

//        모임 생성
        Long moimId = container.moimService.register(
                new MoimRegisterRequest("모임", "모임 설명", "mixed", true),
                null,
                null,
                null,
                leaderId);

//        검증
        ScheduleRegisterRequest scheduleRegisterRequest = new ScheduleRegisterRequest("제목", "내용", LocalDate.now());

        assertThatThrownBy(() ->
            container.scheduleService.register(scheduleRegisterRequest, moimId, memberId)
        )
                .isInstanceOf(ScheduleException.class)
                .hasMessage(MOIM_MEMBER_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("일정 등록 실패 - 권한이 없는 경우")
    void registerFailByNotValidMoimMember() throws IOException {
        FakeContainer container = new FakeContainer();

//        회원가입
        Long leaderId = container.memberService.register(new MemberRegisterRequest("asdf@asdf.com", "asdf1234!@", "닉네임"));


//        모임 생성
        Long moimId = container.moimService.register(
                new MoimRegisterRequest("모임", "모임 설명", "mixed", true),
                null,
                null,
                null,
                leaderId);

//        권한이 없는 회원가입
        Long memberId = container.memberService.register(new MemberRegisterRequest("a@a.com", "asdf1234!@", "닉네임2"));

        Moim moim = container.moimRepository.findById(moimId)
                .orElseThrow(() -> new ScheduleException(MOIM_NOT_FOUND));
        Member member = container.memberRepository.findById(memberId)
                .orElseThrow(() -> new ScheduleException(MEMBER_NOT_FOUND));
        MoimMember moimMember = MoimMember.builder()
                .moim(moim)
                .member(member)
                .role(MemberRole.USER)
                .build();
        container.moimMemberRepository.save(moimMember);

//        검증
        ScheduleRegisterRequest scheduleRegisterRequest = new ScheduleRegisterRequest("제목", "내용", LocalDate.now());

        assertThatThrownBy(() ->
            container.scheduleService.register(scheduleRegisterRequest, moimId, memberId)
        )
                .isInstanceOf(ScheduleException.class)
                .hasMessage(MOIM_FORBIDDEN.getMessage());
    }

    @Test
    @DisplayName("월별 일정 획득")
    void getMonthlySchedule() throws IOException {
        FakeContainer container = new FakeContainer();

//        회원가입
        Long memberId = container.memberService.register(new MemberRegisterRequest("asdf@asdf.com", "asdf1234!@", "닉네임"));

//        모임 생성
        Long moimId = container.moimService.register(
                new MoimRegisterRequest("모임", "모임 설명", "mixed", true),
                null,
                null,
                null,
                memberId
        );

//        일정 생성
        container.scheduleService.register(
                new ScheduleRegisterRequest("일정 제목", "일정 내용", LocalDate.of(2023, 12, 1)),
                moimId,
                memberId
        );
        container.scheduleService.register(
                new ScheduleRegisterRequest("일정 제목2", "일정 내용2", LocalDate.of(2023, 12, 31)),
                moimId,
                memberId
        );

//        일정 획득
        List<ScheduleDTO> scheduleDTOList = container.scheduleService.getMonthlySchedule(memberId, moimId, LocalDate.of(2023, 12, 12));

//        검증
        assertAll(
                () -> assertThat(scheduleDTOList).extracting(ScheduleDTO::getTitle).containsExactlyInAnyOrder("일정 제목", "일정 제목2"),
                () -> assertThat(scheduleDTOList).extracting(ScheduleDTO::getContent).containsExactlyInAnyOrder("일정 내용", "일정 내용2"),
                () -> assertThat((scheduleDTOList)).extracting(ScheduleDTO::getTheDay).containsExactlyInAnyOrder(LocalDate.of(2023, 12, 1), LocalDate.of(2023, 12, 31))
        );
    }
}