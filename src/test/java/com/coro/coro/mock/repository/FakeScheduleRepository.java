package com.coro.coro.mock.repository;

import com.coro.coro.schedule.domain.Schedule;
import com.coro.coro.schedule.repository.ScheduleRepository;

import java.util.Objects;
import java.util.Optional;

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
}