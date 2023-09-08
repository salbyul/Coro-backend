package com.coro.coro.member.controller;

import com.coro.coro.common.response.APIResponse;
import com.coro.coro.member.dto.request.MemberLoginRequest;
import com.coro.coro.member.dto.request.MemberModifyRequest;
import com.coro.coro.member.dto.request.MemberRegisterRequest;
import com.coro.coro.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityManager;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
public class MemberController implements MemberControllerDocs {

    private final MemberService memberService;
    private final EntityManager em;

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
    public APIResponse modify(final @RequestPart(required = false) MultipartFile multipartFile, final @RequestPart MemberModifyRequest requestMember) {
        return APIResponse.create();
    }
}
