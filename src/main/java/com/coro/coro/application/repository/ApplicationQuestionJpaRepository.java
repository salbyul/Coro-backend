package com.coro.coro.application.repository;

import com.coro.coro.application.domain.ApplicationQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ApplicationQuestionJpaRepository extends JpaRepository<ApplicationQuestion, Long> {

    @Modifying
    @Query("delete from ApplicationQuestion aq where aq.moim.id = :moimId")
    void deleteAllByMoimId(@Param("moimId") final Long moimId);

    @Query("select aq from ApplicationQuestion aq where aq.moim.id = :moimId")
    List<ApplicationQuestion> findAllByMoimId(@Param("moimId") final Long moimId);
}
