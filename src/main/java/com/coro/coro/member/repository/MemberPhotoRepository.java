package com.coro.coro.member.repository;

import com.coro.coro.member.domain.MemberPhoto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberPhotoRepository extends JpaRepository<MemberPhoto, Long> {
}
