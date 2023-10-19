package com.coro.coro.moim.repository;

import com.coro.coro.moim.domain.MoimPhoto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MoimPhotoJpaRepository extends JpaRepository<MoimPhoto, Long> {

    @Modifying
    @Query("delete from MoimPhoto mp where mp.id = :moimId")
    void deleteById(@Param("moimId") Long moimId);

    @Query("select mp from MoimPhoto mp where mp.id in :moimIds")
    List<MoimPhoto> findAllByIds(@Param("moimIds") List<Long> moimIds);
}
