package com.coro.coro.schedule.repository;

import com.coro.coro.schedule.domain.Schedule;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ScheduleRepository {

    Long save(final Schedule schedule);

    Optional<Schedule> findById(final Long id);

    List<Schedule> findAllByMoimIdAndTheDayBetween(@Param("moimId") final Long moimId, @Param("start") final LocalDate start, @Param("end") final LocalDate end);

    List<Schedule> findByMoimIdAndDate(@Param("moimId") final Long moimId, @Param("date") final LocalDate date);
}
