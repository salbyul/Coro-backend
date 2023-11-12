package com.coro.coro.moim.repository;

import com.coro.coro.moim.domain.MoimPhoto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MoimPhotoJpaRepository extends JpaRepository<MoimPhoto, Long> {

    @Modifying
    @Query("DELETE FROM MoimPhoto mp WHERE mp.id = :moimId")
    void deleteById(@Param("moimId") Long moimId);

    @Query("SELECT mp FROM MoimPhoto mp WHERE mp.id IN :moimIds")
    List<MoimPhoto> findAllByIds(@Param("moimIds") List<Long> moimIds);
}
