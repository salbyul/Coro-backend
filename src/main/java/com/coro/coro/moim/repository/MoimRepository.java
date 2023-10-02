package com.coro.coro.moim.repository;

import com.coro.coro.moim.domain.Moim;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MoimRepository extends JpaRepository<Moim, Long> {

    Optional<Moim> findByName(final String name);

    @Query(value = "select m from Moim m " +
            "where m.name like %:name% and m.visible = true")
    Page<Moim> findAllByName(@Param("name") final String name, Pageable pageable);

    @Query(value = "select m from Moim m " +
            "inner join fetch MoimTag mt " +
            "on m.id = mt.moim.id and mt.name like %:name% " +
            "where m.visible = true")
    Page<Moim> findAllByTag(@Param("name") final String name, Pageable pageable);

    @Query(value = "select m from Moim m join fetch MoimMember mm on mm.moim.id = m.id and mm.member.id = :memberId")
    List<Moim> findAllByMemberId(@Param("memberId") final Long memberId);
}