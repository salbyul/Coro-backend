package com.coro.coro.moim.repository;

import com.coro.coro.moim.domain.Moim;
import com.coro.coro.moim.domain.MoimTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MoimTagRepository extends JpaRepository<MoimTag, Long> {

    @Modifying
    @Query("delete from MoimTag m where m.moim = :moim")
    void deleteAllByMoim(@Param("moim") final Moim moim);
}
