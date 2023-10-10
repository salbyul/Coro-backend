package com.coro.coro.member.service;

import com.coro.coro.common.domain.jwt.JwtProvider;
import com.coro.coro.member.domain.Member;
import com.coro.coro.member.dto.request.MemberLoginRequest;
import com.coro.coro.member.dto.request.MemberModificationRequest;
import com.coro.coro.member.dto.request.MemberRegisterRequest;
import com.coro.coro.member.exception.MemberException;
import com.coro.coro.member.repository.MemberRepository;
import com.coro.coro.member.validator.MemberValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.coro.coro.common.response.error.ErrorType.*;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class MemberService {

    private final JwtProvider tokenProvider;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    /* 회원가입 */
    @Transactional
    public Long register(final MemberRegisterRequest requestMember) {
        Member member =
                Member.builder()
                        .email(requestMember.getEmail())
                        .password(requestMember.getPassword())
                        .nickname(requestMember.getNickname())
                        .build();
        MemberValidator.validateRegistration(member);

        List<Member> foundMembers = memberRepository.findByEmailOrNickname(member.getEmail(), member.getNickname());
        member.verifyDuplication(foundMembers);
        member.encryptPassword(passwordEncoder);

        memberRepository.save(member);
        return member.getId();
    }

    /* 로그인 */
    public String login(final MemberLoginRequest requestMember) {
        Member member = memberRepository.findByEmail(requestMember.getEmail())
                .orElseThrow(() -> new MemberException(MEMBER_NOT_FOUND));
        comparePassword(requestMember.getPassword(), member.getPassword());

        return tokenProvider.generateAccessToken(member.getNickname());
    }

    private void comparePassword(final String password, final String target) {
        if (!passwordEncoder.matches(password, target)) {
            throw new MemberException(MEMBER_PASSWORD_NOT_VALID);
        }
    }

    /* 회원 수정 */
    @Transactional
    public void update(final Long id, final MemberModificationRequest requestMember) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new MemberException(MEMBER_NOT_FOUND));
        comparePassword(requestMember.getOriginalPassword(), member.getPassword());

        member.changeTo(requestMember);
        member.encryptPassword(passwordEncoder);
    }
}
