package com.coro.coro.auth.service;

import com.coro.coro.auth.domain.RefreshToken;
import com.coro.coro.auth.dto.request.TokenSetRequest;
import com.coro.coro.auth.dto.response.TokenResponse;
import com.coro.coro.auth.exception.AuthException;
import com.coro.coro.auth.jwt.JwtProvider;
import com.coro.coro.auth.service.port.RefreshTokenRepository;
import com.coro.coro.common.service.port.UUIDHolder;
import com.coro.coro.member.domain.Member;
import com.coro.coro.member.dto.request.MemberLoginRequest;
import com.coro.coro.member.exception.MemberException;
import com.coro.coro.member.service.port.MemberRepository;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static com.coro.coro.common.response.error.ErrorType.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Builder
public class AuthService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final MemberRepository memberRepository;
    private final JwtProvider jwtProvider;
    private final UUIDHolder uuidHolder;
    private final PasswordEncoder passwordEncoder;

    /* 로그인 */
    public TokenResponse login(final MemberLoginRequest requestMember) {
        Member member = getMemberByEmail(requestMember);
        boolean isRightPassword = member.isRightPassword(requestMember.getPassword(), passwordEncoder);
        if (!isRightPassword) {
            throw new MemberException(MEMBER_NOT_VALID_PASSWORD);
        }

        String accessToken = jwtProvider.generateAccessToken(member.getNickname());
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

    public TokenResponse issueNewTokenSet(final TokenSetRequest tokenSetRequest) {
        RefreshToken refreshToken = getRefreshTokenById(tokenSetRequest.getRefreshToken());

        String newRefreshToken = uuidHolder.generateUUID();
        String newAccessToken = jwtProvider.generateAccessToken(refreshToken.getNickname());

        RefreshToken refreshTokenObject = RefreshToken.builder()
                .nickname(refreshToken.getNickname())
                .refreshToken(newRefreshToken)
                .build();
        refreshTokenRepository.delete(refreshToken);
        refreshTokenRepository.save(refreshTokenObject);
        return new TokenResponse(newAccessToken, newRefreshToken);
    }

    private RefreshToken getRefreshTokenById(final String refreshToken) {
        return refreshTokenRepository.findById(refreshToken)
                .orElseThrow(() -> new AuthException(AUTH_TOKEN_NOT_FOUND));
    }
}
