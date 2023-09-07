package com.coro.coro.member.service;

import com.coro.coro.common.response.error.ErrorType;
import com.coro.coro.member.domain.Member;
import com.coro.coro.member.domain.User;
import com.coro.coro.member.exception.MemberException;
import com.coro.coro.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserDetailService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(final String userNickname) throws UsernameNotFoundException {
        Member member = memberRepository.findByNickname(userNickname)
                .orElseThrow(() -> new MemberException(ErrorType.MEMBER_NOT_FOUND));
        return User.mappingUserDetails(member);
    }
}
