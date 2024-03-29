package com.coro.coro.schedule.repository;

import com.coro.coro.schedule.domain.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ScheduleJpaRepository extends JpaRepository<Schedule, Long> {

    @Query("SELECT s FROM Schedule s WHERE s.moim.id = :moimId AND s.theDay BETWEEN :start AND :end")
    List<Schedule> findAllByMoimIdAndTheDayBetween(@Param("moimId") final Long moimId, @Param("start") final LocalDate start, @Param("end") final LocalDate end);

    @Query("SELECT s FROM Schedule s WHERE s.moim.id = :moimId AND s.theDay = :date")
    List<Schedule> findByMoimIdAndDate(@Param("moimId") final Long moimId, @Param("date") final LocalDate date);

    @Modifying
    @Query("DELETE FROM Schedule s WHERE s.id = :scheduleId")
    void deleteById(@Param("scheduleId") final Long scheduleId);
}
