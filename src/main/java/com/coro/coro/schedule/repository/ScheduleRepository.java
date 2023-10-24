package com.coro.coro.schedule.repository;

import com.coro.coro.schedule.domain.Schedule;

import java.util.Optional;

public interface ScheduleRepository {

    Long save(final Schedule schedule);

    Optional<Schedule> findById(final Long id);
}
