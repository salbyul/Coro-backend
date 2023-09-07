package com.coro.coro.member.service;

import com.coro.coro.common.domain.jwt.JwtProvider;
import com.coro.coro.common.response.error.ErrorType;
import com.coro.coro.member.domain.Member;
import com.coro.coro.member.dto.request.MemberLoginRequest;
import com.coro.coro.member.dto.request.MemberRegisterRequest;
import com.coro.coro.member.exception.MemberException;
import com.coro.coro.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.List;

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

    /* 로그인 */
    public String login(final MemberLoginRequest requestMember) {
        Member member = memberRepository.findByEmail(requestMember.getEmail())
                .orElseThrow(() -> new MemberException(ErrorType.MEMBER_NOT_FOUND));
        if (!passwordEncoder.matches(requestMember.getPassword(), member.getPassword())) {
            throw new MemberException(ErrorType.MEMBER_NOT_FOUND);
        }
        return tokenProvider.generateAccessToken(member.getNickname());
    }

    //    TODO 지우기
    @PostConstruct
    public void postConstruct() {
        MemberRegisterRequest requestMember = new MemberRegisterRequest("asdf@asdf.com", "asdf1234!@", "닉네임");
        this.register(requestMember);
    }
}
