package com.coro.coro.member.repository.adaptor;

import com.coro.coro.member.domain.Member;
import com.coro.coro.member.repository.MemberJpaRepository;
import com.coro.coro.member.repository.port.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class MemberRepositoryImpl implements MemberRepository {

    private final MemberJpaRepository memberJpaRepository;

    public Long save(final Member member) {
        return memberJpaRepository.save(member).getId();
    }

    public Optional<Member> findById(final Long id) {
        return memberJpaRepository.findById(id);
    }

    public List<Member> findByEmailOrNickname(final String email, final String nickname) {
        return memberJpaRepository.findByEmailOrNickname(email, nickname);
    }

    public Optional<Member> findByNickname(final String nickname) {
        return memberJpaRepository.findByNickname(nickname);
    }

    public Optional<Member> findByEmail(final String email) {
        return memberJpaRepository.findByEmail(email);
    }
}
