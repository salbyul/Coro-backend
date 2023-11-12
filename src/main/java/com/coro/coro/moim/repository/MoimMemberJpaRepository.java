package com.coro.coro.moim.repository;

import com.coro.coro.moim.domain.MoimMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MoimMemberJpaRepository extends JpaRepository<MoimMember, Long> {

    @Query("SELECT mm FROM MoimMember mm WHERE mm.moim.id = :moimId")
    List<MoimMember> findAllByMoimId(@Param("moimId") final Long moimId);

    @Query("SELECT mm FROM MoimMember mm WHERE mm.moim.id = :moimId AND mm.member.id = :memberId")
    Optional<MoimMember> findByMoimIdAndMemberId(@Param("moimId") final Long moimId, @Param("memberId") final Long memberId);
}
