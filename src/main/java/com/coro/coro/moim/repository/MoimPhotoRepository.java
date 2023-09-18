package com.coro.coro.moim.repository;

import com.coro.coro.moim.domain.MoimPhoto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MoimPhotoRepository extends JpaRepository<MoimPhoto, Long> {

    @Modifying
    @Query("delete from MoimPhoto mp where mp.moimId = :moimId")
    void deleteById(@Param("moimId") Long moimId);
}
