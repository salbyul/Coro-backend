package com.coro.coro.moim.controller;

import com.coro.coro.common.response.APIResponse;
import com.coro.coro.member.domain.User;
import com.coro.coro.moim.dto.request.MoimRegisterRequest;
import com.coro.coro.moim.dto.request.MoimTagRequest;
import com.coro.coro.moim.service.MoimService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/moim")
public class MoimController implements MoimControllerDocs {

    private final MoimService moimService;

    @PostMapping
    @Override
    public APIResponse register(@RequestPart(name = "moim") final MoimRegisterRequest requestMoim, @RequestPart(required = false, name = "tagList") final MoimTagRequest requestTag, @AuthenticationPrincipal final User user) {
        moimService.register(requestMoim, requestTag, user.getId());
        return APIResponse.create();
    }
}