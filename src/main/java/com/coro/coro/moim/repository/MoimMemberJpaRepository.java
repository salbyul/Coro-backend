package com.coro.coro.moim.repository;

import com.coro.coro.moim.domain.MoimMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MoimMemberJpaRepository extends JpaRepository<MoimMember, Long> {

    @Query("select mm from MoimMember mm where mm.moim.id = :moimId")
    List<MoimMember> findAllByMoimId(@Param("moimId") final Long moimId);

    @Query("select mm from MoimMember mm where mm.moim.id = :moimId and mm.member.id = :memberId")
    Optional<MoimMember> findByMoimIdAndMemberId(@Param("moimId") final Long moimId, @Param("memberId") final Long memberId);
}
