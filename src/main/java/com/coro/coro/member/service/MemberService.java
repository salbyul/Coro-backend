package com.coro.coro.member.service;

import com.coro.coro.member.domain.Member;
import com.coro.coro.member.dto.request.MemberModificationRequest;
import com.coro.coro.member.dto.request.MemberRegisterRequest;
import com.coro.coro.member.dto.response.MemberInformationResponse;
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

        List<Member> memberList = memberRepository.findByEmailOrNickname(member.getEmail(), member.getNickname());
        member.verifyDuplication(memberList);
        member.encryptPassword(passwordEncoder);

        return memberRepository.save(member);
    }

    /* 회원 정보 획득 */
    public MemberInformationResponse getInformation(final Long memberId) {
        Member member = getMemberById(memberId);
        return new MemberInformationResponse(member);
    }

    /* 회원 수정 */
    @Transactional
    public void update(final Long id, final MemberModificationRequest requestMember) {
        Member member = getMemberById(id);
        member.update(requestMember, passwordEncoder);
    }

    private Member getMemberById(final Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new MemberException(MEMBER_NOT_FOUND));
    }
}
