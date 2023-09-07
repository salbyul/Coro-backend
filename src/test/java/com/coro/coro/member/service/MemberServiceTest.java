package com.coro.coro.member.service;

import com.coro.coro.member.dto.request.MemberLoginRequest;
import com.coro.coro.member.dto.request.MemberRegisterRequest;
import com.coro.coro.member.exception.MemberException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
class MemberServiceTest {

    @Autowired
    MemberService memberService;
    @PersistenceContext
    EntityManager em;

    @BeforeEach
    void setUp() {
        memberService.register(new MemberRegisterRequest("asdf@asdf.com", "asdf1234!@", "닉네임"));
    }

    @Test
    @DisplayName("[로그인] 정상적인 로그인의 경우")
    void login() {
        memberService.login(new MemberLoginRequest("asdf@asdf.com", "asdf1234!@"));
    }

    @Test
    @DisplayName("[로그인] 틀린 이메일의 경우")
    void loginFailByEmail() {
        assertThatThrownBy(() -> memberService.login(new MemberLoginRequest("asdf@asd.com", "asdf1234!@")))
                .isInstanceOf(MemberException.class);
    }

    @Test
    @DisplayName("[로그인] 틀린 비밀번호의 경우")
    void loginFailByPassword() {
        assertThatThrownBy(() -> memberService.login(new MemberLoginRequest("asdf@asdf.com", "asdf1234!#")))
                .isInstanceOf(MemberException.class);
    }
}