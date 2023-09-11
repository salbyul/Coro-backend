package com.coro.coro.member.controller;

import com.coro.coro.common.response.APIResponse;
import com.coro.coro.member.domain.User;
import com.coro.coro.member.dto.request.MemberLoginRequest;
import com.coro.coro.member.dto.request.MemberModifyRequest;
import com.coro.coro.member.dto.request.MemberRegisterRequest;
import com.coro.coro.member.service.MemberPhotoService;
import com.coro.coro.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
public class MemberController implements MemberControllerDocs {

    private final MemberService memberService;
    private final MemberPhotoService memberPhotoService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Override
    public APIResponse register(@RequestBody final MemberRegisterRequest requestMember) {
        log.info("member: {}", requestMember);
        memberService.register(requestMember);
        return APIResponse.create();
    }

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public APIResponse login(@RequestBody final MemberLoginRequest requestMember) {
        String token = memberService.login(requestMember);
        return APIResponse.create()
                .addObject("token", token);
    }

    /*
    이미지, 회원정보 따로 수정 가능
     */
    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    @Override
    public APIResponse update(@RequestPart(required = false, name = "profileImage") final MultipartFile multipartFile, @RequestPart(name = "member", required = false) final MemberModifyRequest requestMember, final @AuthenticationPrincipal User user) throws IOException {
        if (requestMember != null && requestMember.isExist()) {
            memberService.update(user.getId(), requestMember);
        }
        if (multipartFile != null && !multipartFile.isEmpty()) {
            memberPhotoService.changeProfileImage(user.getId(), multipartFile);
        }
        return APIResponse.create();
    }
}
