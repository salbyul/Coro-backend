package com.coro.coro.moim.controller;

import com.coro.coro.application.dto.request.ApplicationQuestionRegisterRequest;
import com.coro.coro.common.response.APIResponse;
import com.coro.coro.member.domain.MemberRole;
import com.coro.coro.member.service.User;
import com.coro.coro.moim.annotation.Search;
import com.coro.coro.moim.domain.Moim;
import com.coro.coro.moim.dto.request.*;
import com.coro.coro.moim.dto.response.MoimDetailResponse;
import com.coro.coro.moim.dto.response.MoimMemberResponse;
import com.coro.coro.moim.dto.response.MoimModificationResponse;
import com.coro.coro.moim.dto.response.MoimSearchResponse;
import com.coro.coro.moim.service.MoimService;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@Builder
@RequestMapping("/api/moims")
public class MoimController implements MoimControllerDocs {

    private final MoimService moimService;

    @GetMapping("/{id}")
    @Override
    public APIResponse detail(@PathVariable("id") final Long moimId, @AuthenticationPrincipal final User user) throws IOException {
        MoimDetailResponse detail = moimService.getDetail(moimId, user.getId());
        return APIResponse.create()
                .addObject("moim", detail);
    }

    @GetMapping("/search")
    @Override
    public APIResponse search(@Search final MoimSearchRequest moimSearchRequest, final Pageable pageable) throws IOException {
        Page<Moim> result = moimService.search(moimSearchRequest, pageable);
        List<MoimSearchResponse> moimList = moimService.getSummaryMoim(result.getContent());

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
    public APIResponse register(@RequestPart(name = "moim") final MoimRegisterRequest requestMoim,
                                @RequestPart(required = false, name = "tagList") final MoimTagRequest requestTag,
                                @RequestPart(required = false, name = "applicationQuestionList") final List<ApplicationQuestionRegisterRequest> requestQuestions,
                                @RequestPart(name = "photo", required = false) final MultipartFile multipartFile,
                                @AuthenticationPrincipal final User user) throws IOException {
        Long savedId = moimService.register(requestMoim, requestTag, requestQuestions, multipartFile, user.getId());
        return APIResponse.create()
                .addObject("moimId", savedId);
    }

    @PutMapping("/{id}")
    @Override
    public APIResponse update(@PathVariable("id") Long moimId,
                              @RequestPart(name = "moim") final MoimModificationRequest requestMoim,
                              @RequestPart(name = "tagList") final MoimTagRequest requestTag,
                              @RequestPart(name = "applicationQuestionList") final List<ApplicationQuestionRegisterRequest> requestQuestions,
                              @RequestPart(name = "photo", required = false) final MultipartFile multipartFile,
                              @AuthenticationPrincipal final User user) throws IOException {
        moimService.update(moimId, requestMoim, requestTag, multipartFile, requestQuestions, user.getId());
        return APIResponse.create();
    }

    @GetMapping("/modification/{id}")
    @Override
    public APIResponse getMoimForModification(@PathVariable("id") final Long moimId, @AuthenticationPrincipal User user) throws IOException {
        MoimModificationResponse detail = moimService.getDetailForModification(moimId, user.getId());
        return APIResponse.create()
                .addObject("detail", detail);
    }

    @GetMapping("/{moimId}/members")
    @Override
    public APIResponse getMoimMember(@PathVariable("moimId") final Long moimId, @AuthenticationPrincipal final User user) {
        List<MoimMemberResponse> moimMemberResponseList = moimService.getMoimMemberList(moimId);
        MemberRole memberRole = moimService.getMemberRole(user.getId(), moimId);
        return APIResponse.create()
                .addObject("moimMemberList", moimMemberResponseList)
                .addObject("role", memberRole);
    }

    @PutMapping("/{moimId}/members")
    @Override
    public APIResponse changeMoimMember(@PathVariable("moimId") final Long moimId,
                                        @RequestBody final List<MoimMemberModificationRequest> requestMoimMember,
                                        @AuthenticationPrincipal final User user) {
        moimService.modifyMoimMember(moimId, requestMoimMember, user.getId());
        return APIResponse.create();
    }

    @DeleteMapping("/{moimId}/members")
    @Override
    public APIResponse deportMember(@PathVariable(name = "moimId") final Long moimId, @ModelAttribute(name = "moimMember") final Long moimMemberId, @AuthenticationPrincipal final User user) {
        moimService.deport(moimId, moimMemberId, user.getId());
        return APIResponse.create();
    }
}