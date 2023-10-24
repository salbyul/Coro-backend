package com.coro.coro.schedule.validator;

import com.coro.coro.schedule.domain.Schedule;
import com.coro.coro.schedule.exception.ScheduleException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static com.coro.coro.common.response.error.ErrorType.*;
import static org.assertj.core.api.Assertions.*;

class ScheduleValidatorTest {

    @Test
    @DisplayName("제목이 null 값인 경우")
    void validateNullTitle() {
        Schedule schedule = Schedule.builder()
                .moim(null)
                .title(null)
                .content("컨텐츠")
                .theDay(LocalDate.now())
                .build();

        assertThatThrownBy(() ->
                ScheduleValidator.validateSchedule(schedule, LocalDate.now())
        )
                .isInstanceOf(ScheduleException.class)
                .hasMessage(SCHEDULE_NOT_VALID_TITLE.getMessage());
    }

    @Test
    @DisplayName("제목이 빈 문자열인 경우")
    void validateEmptyTitle() {
        Schedule schedule = Schedule.builder()
                .moim(null)
                .title("")
                .content("컨텐츠")
                .theDay(LocalDate.now())
                .build();

        assertThatThrownBy(() ->
                ScheduleValidator.validateSchedule(schedule, LocalDate.now())
        )
                .isInstanceOf(ScheduleException.class)
                .hasMessage(SCHEDULE_NOT_VALID_TITLE.getMessage());
    }

    @Test
    @DisplayName("제목이 100글자를 넘는 경우")
    void validateTitleGreaterThan100Length() {
        Schedule schedule = Schedule.builder()
                .moim(null)
                .title("a".repeat(101))
                .content("컨텐츠")
                .theDay(LocalDate.now())
                .build();

        assertThatThrownBy(() ->
                ScheduleValidator.validateSchedule(schedule, LocalDate.now())
        )
                .isInstanceOf(ScheduleException.class)
                .hasMessage(SCHEDULE_NOT_VALID_TITLE.getMessage());
    }

    @Test
    @DisplayName("내용이 1000글자가 넘는 경우")
    void validateContentGreaterThan1000Length() {
        Schedule schedule = Schedule.builder()
                .moim(null)
                .title("asdf")
                .content("a".repeat(1001))
                .theDay(LocalDate.now())
                .build();

        assertThatThrownBy(() ->
            ScheduleValidator.validateSchedule(schedule, LocalDate.now())
        )
                .isInstanceOf(ScheduleException.class)
                .hasMessage(SCHEDULE_NOT_VALID_CONTENT.getMessage());
    }

    @Test
    @DisplayName("일정 날짜가 오늘 이전일 경우")
    void validateTheDay() {
        Schedule schedule = Schedule.builder()
                .moim(null)
                .title("asdf")
                .content("a")
                .theDay(LocalDate.of(2000, 12, 12))
                .build();

        LocalDate before = LocalDate.of(2023, 12, 12);

        assertThatThrownBy(() ->
            ScheduleValidator.validateSchedule(schedule, before)
        )
                .isInstanceOf(ScheduleException.class)
                .hasMessage(SCHEDULE_NOT_VALID_THE_DAY.getMessage());
    }
}