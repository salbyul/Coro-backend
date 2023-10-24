package com.coro.coro.schedule.repository;

import com.coro.coro.schedule.domain.Schedule;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
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

    @Override
    public List<Schedule> findAllByMoimIdAndTheDayBetween(final Long moimId, final LocalDate start, final LocalDate end) {
        return scheduleJpaRepository.findAllByMoimIdAndTheDayBetween(moimId, start, end);
    }

    @Override
    public List<Schedule> findByMoimIdAndDate(final Long moimId, final LocalDate date) {
        return scheduleJpaRepository.findByMoimIdAndDate(moimId, date);
    }

    @Override
    public void deleteById(final Long scheduleId) {
        scheduleJpaRepository.deleteById(scheduleId);
    }
}
