package com.coro.coro.member.service;

import com.coro.coro.member.domain.Member;
import com.coro.coro.member.domain.MemberState;
import lombok.Builder;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

@Getter
@Builder
public class User implements UserDetails {

    private Long id;
    private String email;
    private String password;
    private String nickname;
    private String introduction;
    private MemberState state;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.nickname;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return this.state.isSuspended();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return this.state.isActive();
    }

    public static User mappingUserDetails(final Member member) {
        return User.builder()
                .id(member.getId())
                .email(member.getEmail())
                .password(member.getPassword())
                .nickname(member.getNickname())
                .introduction(member.getIntroduction())
                .state(member.getState())
                .build();
    }
}
