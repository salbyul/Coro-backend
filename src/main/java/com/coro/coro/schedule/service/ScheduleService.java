package com.coro.coro.schedule.service;

import com.coro.coro.moim.domain.Moim;
import com.coro.coro.moim.domain.MoimMember;
import com.coro.coro.moim.repository.port.MoimMemberRepository;
import com.coro.coro.moim.repository.port.MoimRepository;
import com.coro.coro.schedule.domain.Schedule;
import com.coro.coro.schedule.dto.request.ScheduleRegisterRequest;
import com.coro.coro.schedule.exception.ScheduleException;
import com.coro.coro.schedule.repository.ScheduleRepository;
import com.coro.coro.schedule.validator.ScheduleValidator;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.coro.coro.common.response.error.ErrorType.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Builder
@Transactional(readOnly = true)
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final MoimMemberRepository moimMemberRepository;
    private final MoimRepository moimRepository;

    @Transactional
    public Long register(final ScheduleRegisterRequest registerRequest, final Long moimId, final Long memberId) {
        Moim moim = getMoimById(moimId);
        MoimMember moimMember = getMoimMemberByMoimIdAndMemberId(moimId, memberId);
        validateMoimMember(moimMember);

        Schedule schedule = toSchedule(registerRequest, moim);
        ScheduleValidator.validateSchedule(schedule);
        return scheduleRepository.save(schedule);
    }

    private Moim getMoimById(final Long moimId) {
        return moimRepository.findById(moimId)
                .orElseThrow(() -> new ScheduleException(MOIM_NOT_FOUND));
    }

    private MoimMember getMoimMemberByMoimIdAndMemberId(final Long moimId, final Long memberId) {
        return moimMemberRepository.findByMoimIdAndMemberId(moimId, memberId)
                .orElseThrow(() -> new ScheduleException(MOIM_MEMBER_NOT_FOUND));
    }

    private void validateMoimMember(final MoimMember moimMember) {
        if (!moimMember.canManage()) {
            throw new ScheduleException(MOIM_FORBIDDEN);
        }
    }

    private Schedule toSchedule(final ScheduleRegisterRequest registerRequest, final Moim moim) {
        return Schedule.builder()
                .moim(moim)
                .title(registerRequest.getTitle())
                .content(registerRequest.getContent())
                .theDay(registerRequest.getDate())
                .build();
    }
}
