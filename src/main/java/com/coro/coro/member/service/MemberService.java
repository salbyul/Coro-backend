package com.coro.coro.member.service;

import com.coro.coro.auth.domain.RefreshToken;
import com.coro.coro.auth.dto.response.TokenResponse;
import com.coro.coro.auth.jwt.JwtProvider;
import com.coro.coro.auth.service.port.RefreshTokenRepository;
import com.coro.coro.common.service.port.UUIDHolder;
import com.coro.coro.member.domain.Member;
import com.coro.coro.member.dto.request.MemberLoginRequest;
import com.coro.coro.member.dto.request.MemberModificationRequest;
import com.coro.coro.member.dto.request.MemberRegisterRequest;
import com.coro.coro.member.exception.MemberException;
import com.coro.coro.member.service.port.MemberRepository;
import com.coro.coro.member.validator.MemberValidator;
import lombok.Builder;
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
@Builder
public class MemberService {

    private final JwtProvider tokenProvider;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final UUIDHolder uuidHolder;
    private final RefreshTokenRepository refreshTokenRepository;

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

        List<Member> memberList = memberRepository.findByEmailOrNickname(member.getEmail(), member.getNickname());
        member.verifyDuplication(memberList);
        member.encryptPassword(passwordEncoder);

        return memberRepository.save(member);
    }

    /* 로그인 */
    public TokenResponse login(final MemberLoginRequest requestMember) {
        Member member = getMemberByEmail(requestMember);
        boolean isRightPassword = member.isRightPassword(requestMember.getPassword(), passwordEncoder);
        if (!isRightPassword) {
            throw new MemberException(MEMBER_NOT_VALID_PASSWORD);
        }

        String accessToken = tokenProvider.generateAccessToken(member.getNickname());
        String refreshToken = uuidHolder.generateUUID();
        RefreshToken token = RefreshToken.builder()
                .nickname(member.getNickname())
                .refreshToken(refreshToken)
                .build();
        refreshTokenRepository.save(token);
        return new TokenResponse(accessToken, refreshToken);
    }

    private Member getMemberByEmail(final MemberLoginRequest requestMember) {
        return memberRepository.findByEmail(requestMember.getEmail())
                .orElseThrow(() -> new MemberException(MEMBER_NOT_VALID_EMAIL));
    }

    /* 회원 수정 */
    @Transactional
    public void update(final Long id, final MemberModificationRequest requestMember) {
        Member member = getMemberById(id);
        boolean isRightPassword = member.isRightPassword(requestMember.getOriginalPassword(), passwordEncoder);
        if (!isRightPassword) {
            throw new MemberException(MEMBER_NOT_VALID_PASSWORD);
        }

        member.update(requestMember, passwordEncoder);
    }

    private Member getMemberById(final Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new MemberException(MEMBER_NOT_FOUND));
    }
}
