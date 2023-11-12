package com.coro.coro.moim.repository;

import com.coro.coro.moim.domain.Moim;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MoimJpaRepository extends JpaRepository<Moim, Long> {

    Optional<Moim> findByName(final String name);

    @Query(value = "SELECT m FROM Moim m " +
            "WHERE m.name LIKE %:name% AND m.visible = TRUE")
    Page<Moim> findAllByName(@Param("name") final String name, Pageable pageable);

    @Query(value = "SELECT m FROM Moim m " +
            "INNER JOIN FETCH MoimTag mt " +
            "ON m.id = mt.moim.id AND mt.name LIKE %:name% " +
            "WHERE m.visible = TRUE")
    Page<Moim> findAllByTag(@Param("name") final String name, Pageable pageable);

    @Query(value = "SELECT m FROM Moim m JOIN FETCH MoimMember mm ON mm.moim.id = m.id AND mm.member.id = :memberId")
    List<Moim> findAllByMemberId(@Param("memberId") final Long memberId);
}