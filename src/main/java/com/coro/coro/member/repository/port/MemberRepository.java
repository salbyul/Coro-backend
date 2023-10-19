package com.coro.coro.member.repository.port;

import com.coro.coro.member.domain.Member;

import java.util.List;
import java.util.Optional;

public interface MemberRepository {

    Long save(final Member member);

    Optional<Member> findById(final Long id);

    List<Member> findByEmailOrNickname(final String email, final String nickname);

    Optional<Member> findByNickname(final String nickname);

    Optional<Member> findByEmail(final String email);
}
