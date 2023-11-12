package com.coro.coro.moim.repository;

import com.coro.coro.moim.domain.MoimTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MoimTagJpaRepository extends JpaRepository<MoimTag, Long> {

    @Modifying
    @Query("DELETE FROM MoimTag m WHERE m.moim.id = :moimId")
    void deleteAllByMoimId(@Param("moimId") final Long moimId);
}
