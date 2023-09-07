package com.coro.coro.member.controller;

import com.coro.coro.common.domain.jwt.JwtTokenProvider;
import com.coro.coro.common.response.APIResponse;
import com.coro.coro.member.domain.Member;
import com.coro.coro.member.dto.request.MemberLoginRequest;
import com.coro.coro.member.dto.request.MemberRegisterRequest;
import com.coro.coro.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
public class MemberController {

    private final MemberService memberService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public APIResponse register(@RequestBody MemberRegisterRequest requestMember) {
        log.info("member: {}", requestMember);
        memberService.register(requestMember);
        return APIResponse.create();
    }

    @PutMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public APIResponse login(@RequestBody MemberLoginRequest requestMember) {
        String token = memberService.login(requestMember);
        return APIResponse.create()
                .addObject("token", token);
    }
}
