package com.coro.coro.member.service;

import com.coro.coro.member.domain.Member;
import com.coro.coro.member.dto.request.MemberRegisterRequest;
import com.coro.coro.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    /* 회원가입 */
    @Transactional
    public void register(final MemberRegisterRequest requestMember) {
        Member member =
                Member.builder()
                        .email(requestMember.getEmail())
                        .password(requestMember.getPassword())
                        .nickname(requestMember.getNickname())
                        .build();

        List<Member> foundMembers = memberRepository.findByEmailOrNickname(member.getEmail(), member.getNickname());
        member.verifyDuplication(foundMembers);
        member.encryptPassword(passwordEncoder);

        memberRepository.save(member);
    }
}
