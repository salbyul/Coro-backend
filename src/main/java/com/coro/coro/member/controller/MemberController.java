package com.coro.coro.member.controller;

import com.coro.coro.common.response.APIResponse;
import com.coro.coro.member.domain.User;
import com.coro.coro.member.dto.request.MemberLoginRequest;
import com.coro.coro.member.dto.request.MemberModifyRequest;
import com.coro.coro.member.dto.request.MemberRegisterRequest;
import com.coro.coro.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
public class MemberController implements MemberControllerDocs{

    private final MemberService memberService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Override
    public APIResponse register(final @RequestBody MemberRegisterRequest requestMember) {
        log.info("member: {}", requestMember);
        memberService.register(requestMember);
        return APIResponse.create();
    }

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public APIResponse login(final @RequestBody MemberLoginRequest requestMember) {
        String token = memberService.login(requestMember);
        return APIResponse.create()
                .addObject("token", token);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    @Override
    public APIResponse update(final @RequestPart(required = false) MultipartFile multipartFile, final @RequestPart(name = "member") MemberModifyRequest requestMember, final @AuthenticationPrincipal User user) {
        memberService.update(user.getId(), requestMember, multipartFile);
        return APIResponse.create();
    }
}
