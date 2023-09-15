package com.coro.coro.moim.controller;

import com.coro.coro.common.response.APIResponse;
import com.coro.coro.member.domain.User;
import com.coro.coro.moim.dto.request.MoimModifyRequest;
import com.coro.coro.moim.dto.request.MoimRegisterRequest;
import com.coro.coro.moim.dto.request.MoimTagRequest;
import com.coro.coro.moim.service.MoimService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/moims")
public class MoimController implements MoimControllerDocs {

    private final MoimService moimService;

    @PostMapping
    @Override
    public APIResponse register(@RequestPart(name = "moim") final MoimRegisterRequest requestMoim, @RequestPart(required = false, name = "tagList") final MoimTagRequest requestTag, @AuthenticationPrincipal final User user) {
        moimService.register(requestMoim, requestTag, user.getId());
        return APIResponse.create();
    }

    /*
    모임과 태그, 이미지 따로 수정 가능
     */
    @PutMapping
    @Override
    public APIResponse update(@RequestPart(name = "moim", required = false) final MoimModifyRequest requestMoim, @RequestPart(name = "tagList", required = false) final MoimTagRequest requestTag, @RequestPart(name = "moimImage", required = false) final MultipartFile multipartFile, @AuthenticationPrincipal final User user) {
        if (requestMoim != null && requestTag != null) {
            moimService.update(requestMoim, requestTag);
        }
        log.info("file name: {}", multipartFile.getOriginalFilename());
        return APIResponse.create();
    }
}