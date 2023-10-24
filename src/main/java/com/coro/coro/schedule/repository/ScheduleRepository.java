package com.coro.coro.schedule.repository;

import com.coro.coro.schedule.domain.Schedule;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ScheduleRepository {

    Long save(final Schedule schedule);

    Optional<Schedule> findById(final Long id);

    List<Schedule> findAllByMoimIdAndTheDayBetween(final Long moimId, final LocalDate start, final LocalDate end);

    List<Schedule> findByMoimIdAndDate(final Long moimId, final LocalDate date);

    void deleteById(final Long scheduleId);
}
