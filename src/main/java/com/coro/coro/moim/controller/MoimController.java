package com.coro.coro.moim.controller;

import com.coro.coro.application.dto.request.ApplicationQuestionRegisterRequest;
import com.coro.coro.common.response.APIResponse;
import com.coro.coro.member.domain.User;
import com.coro.coro.moim.annotation.Search;
import com.coro.coro.moim.domain.Moim;
import com.coro.coro.moim.dto.request.MoimModifyRequest;
import com.coro.coro.moim.dto.request.MoimRegisterRequest;
import com.coro.coro.moim.dto.request.MoimSearchRequest;
import com.coro.coro.moim.dto.request.MoimTagRequest;
import com.coro.coro.moim.dto.response.MoimSearchResponse;
import com.coro.coro.moim.service.MoimService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/moims")
public class MoimController implements MoimControllerDocs {

    private final MoimService moimService;

    @GetMapping
    @Override
    public APIResponse search(@Search final MoimSearchRequest moimSearchRequest, final Pageable pageable) {
        Page<Moim> result = moimService.search(moimSearchRequest, pageable);

        List<MoimSearchResponse> moimList = result.getContent().stream().map(MoimSearchResponse::new).collect(Collectors.toList());
        return APIResponse.create()
                .addObject("list", moimList)
                .addObject("totalPages", result.getTotalPages())
                .addObject("hasNext", result.hasNext())
                .addObject("hasPrevious", result.hasPrevious())
                .addObject("isFirst", result.isFirst())
                .addObject("isLast", result.isLast());
    }

    @PostMapping
    @Override
    public APIResponse register(@RequestPart(name = "moim") final MoimRegisterRequest requestMoim, @RequestPart(required = false, name = "tagList") final MoimTagRequest requestTag, @RequestPart(required = false, name = "applicationQuestionList") final List<ApplicationQuestionRegisterRequest> requestQuestions, @AuthenticationPrincipal final User user) {
        Long savedId = moimService.register(requestMoim, requestTag, requestQuestions, user.getId());
        return APIResponse.create()
                .addObject("moimId", savedId);
    }

    /*
    모임과 태그, 이미지 따로 수정 가능
     */
    @PutMapping("/{id}")
    @Override
    public APIResponse update(@PathVariable("id") Long moimId, @RequestPart(name = "moim", required = false) final MoimModifyRequest requestMoim, @RequestPart(name = "tagList", required = false) final MoimTagRequest requestTag, @RequestPart(name = "moimImage", required = false) final MultipartFile multipartFile) throws IOException {
        moimService.update(moimId, requestMoim, requestTag, multipartFile);
        return APIResponse.create();
    }
}