package com.coro.coro.member.repository;

import com.coro.coro.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MemberRepository extends JpaRepository<Member, Long> {

    List<Member> findByEmailOrNickname(final String email, final String nickname);
}
