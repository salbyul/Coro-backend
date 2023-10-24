package com.coro.coro.schedule.repository;

import com.coro.coro.schedule.domain.Schedule;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ScheduleRepositoryImpl implements ScheduleRepository {

    private final ScheduleJpaRepository scheduleJpaRepository;

    @Override
    public Long save(final Schedule schedule) {
        return scheduleJpaRepository.save(schedule).getId();
    }

    @Override
    public Optional<Schedule> findById(final Long id) {
        return scheduleJpaRepository.findById(id);
    }
}
