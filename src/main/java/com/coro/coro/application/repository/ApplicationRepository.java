package com.coro.coro.application.repository;

import com.coro.coro.application.domain.Application;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ApplicationRepository extends JpaRepository<Application, Long> {

    @Query("select a from Application a where a.moim.id = :moimId and a.member.id = :memberId")
    List<Application> findByMemberAndMoim(@Param("memberId") Long memberId, @Param("moimId") Long moimId);
}
