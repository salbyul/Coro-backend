package com.coro.coro.application.repository;

import com.coro.coro.application.domain.Application;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ApplicationJpaRepository extends JpaRepository<Application, Long> {

    @Query("select a from Application a where a.moim.id = :moimId and a.member.id = :memberId")
    List<Application> findByMemberIdAndMoimId(@Param("memberId") final Long memberId, @Param("moimId") final Long moimId);

    @Query("select a from Application a where a.moim.id = :moimId and a.member.id = :memberId and a.status = UPPER( :status)")
    List<Application> findByMemberIdAndMoimIdAndStatus(@Param("memberId") final Long memberId, @Param("moimId") final Long moimId, @Param("status") final String status);

    @Query("select a from Application a where a.moim.id = :moimId and a.status = UPPER(:status)")
    List<Application> findAllByMoimIdAndStatus(@Param("moimId") final Long moimId, @Param("status") final String status);

    @Query("select a from Application a where a.moim.id = :moimId")
    List<Application> findAllByMoimId(@Param("moimId") final Long moimId);
}
