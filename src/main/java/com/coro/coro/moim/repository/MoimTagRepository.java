package com.coro.coro.moim.repository;

import com.coro.coro.moim.domain.MoimTag;
import com.coro.coro.moim.domain.MoimTagId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MoimTagRepository extends JpaRepository<MoimTag, MoimTagId> {

    @Modifying(clearAutomatically = true)
    @Query("delete from MoimTag m where m.id.moimId = :moimId")
    void deleteAllByMoimId(@Param("moimId") final Long moimId);
}
