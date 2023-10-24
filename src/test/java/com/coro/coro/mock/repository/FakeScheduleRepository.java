package com.coro.coro.mock.repository;

import com.coro.coro.schedule.domain.Schedule;
import com.coro.coro.schedule.repository.ScheduleRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class FakeScheduleRepository implements ScheduleRepository {

    private final DataSet dataSet;

    public FakeScheduleRepository(final DataSet dataSet) {
        this.dataSet = dataSet;
    }

    @Override
    public Long save(final Schedule schedule) {
        if (Objects.isNull(schedule.getId())) {
            Schedule toBeSaved = Schedule.builder()
                    .id(dataSet.scheduleSequence++)
                    .moim(schedule.getMoim())
                    .title(schedule.getTitle())
                    .content(schedule.getContent())
                    .theDay(schedule.getTheDay())
                    .build();
            toBeSaved.prePersist();
            dataSet.scheduleData.put(toBeSaved.getId(), toBeSaved);
            return toBeSaved.getId();
        }
        schedule.preUpdate();
        dataSet.scheduleData.put(schedule.getId(), schedule);
        return schedule.getId();
    }

    public Optional<Schedule> findById(final Long id) {
        return Optional.ofNullable(dataSet.scheduleData.get(id));
    }

    @Override
    public List<Schedule> findAllByMoimIdAndTheDayBetween(final Long moimId, final LocalDate start, final LocalDate end) {
        return dataSet.scheduleData.values().stream()
                .filter(schedule -> schedule.getMoim().getId().equals(moimId) &&
                        (schedule.getTheDay().isEqual(start) || schedule.getTheDay().isAfter(start)) &&
                        (schedule.getTheDay().isEqual(end) || schedule.getTheDay().isBefore(end)))
                .collect(Collectors.toList());
    }

    @Override
    public List<Schedule> findByMoimIdAndDate(final Long moimId, final LocalDate date) {
        return dataSet.scheduleData.values().stream()
                .filter(schedule -> schedule.getMoim().getId().equals(moimId) &&
                        schedule.getTheDay().isEqual(date))
                .collect(Collectors.toList());
    }
}