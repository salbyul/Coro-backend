package com.coro.coro.member.repository;

import com.coro.coro.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    List<Member> findByEmailOrNickname(final String email, final String nickname);

    Optional<Member> findByNickname(final String userNickname);

    Optional<Member> findByEmail(final String email);
}
