package com.coro.coro.application.repository;

import com.coro.coro.application.domain.ApplicationAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ApplicationAnswerJpaRepository extends JpaRepository<ApplicationAnswer, Long> {

    @Query("SELECT aa FROM ApplicationAnswer aa WHERE aa.application.id = :applicationId")
    List<ApplicationAnswer> findAllByApplicationId(@Param("applicationId") final Long applicationId);
}
