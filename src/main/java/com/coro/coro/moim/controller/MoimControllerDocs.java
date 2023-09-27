package com.coro.coro.moim.controller;

import com.coro.coro.application.dto.request.ApplicationQuestionRegisterRequest;
import com.coro.coro.common.response.APIResponse;
import com.coro.coro.member.domain.User;
import com.coro.coro.moim.annotation.Search;
import com.coro.coro.moim.dto.request.MoimModifyRequest;
import com.coro.coro.moim.dto.request.MoimRegisterRequest;
import com.coro.coro.moim.dto.request.MoimSearchRequest;
import com.coro.coro.moim.dto.request.MoimTagRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Tag(name = "Moim", description = "모임")
public interface MoimControllerDocs {

    @Operation(summary = "모임 검색")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "검색 성공"),
            @ApiResponse(responseCode = "403", description = "유저 인증 실패")
    })
    @Parameters(value = {
            @Parameter(name = "size", description = "검색 사이즈"),
            @Parameter(name = "page", description = "페이지"),
            @Parameter(name = "option", description = "모임명으로 검색인지 태그로 검색인지"),
            @Parameter(name = "value", description = "검색 값")
    })
    APIResponse search(@Search final MoimSearchRequest moimSearchRequest, final Pageable pageable);

    @Operation(summary = "모임 등록")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "모임 등록 성공"),
            @ApiResponse(responseCode = "400", description = "모임 등록 실패"),
            @ApiResponse(responseCode = "403", description = "유저 인증 실패")
    })
    @Parameters(value = {
            @Parameter(name = "name", description = "모임명", example = "공부모임", required = true),
            @Parameter(name = "introduction", description = "모임 소개", example = "우리 모임을 소개합니다!"),
            @Parameter(name = "type", description = "모임 대면, 비대면 타입", example = "mixed faceToFace nonContact"),
            @Parameter(name = "visible", description = "공개 모임인지", example = "true", required = true),
            @Parameter(name = "tagList", description = "모임 태그들", example = "[\"tag1\", \"tag2\", \"tag3\"]")
    })
    APIResponse register(@RequestPart(name = "moim") final MoimRegisterRequest requestMoim,
                         @RequestPart(required = false, name = "tagList") final MoimTagRequest requestTag,
                         @RequestPart(required = false, name = "applicationQuestionList") final List<ApplicationQuestionRegisterRequest> requestQuestions,
                         @AuthenticationPrincipal final User user);

    @Operation(summary = "모임 수정")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "모임 수정 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "403", description = "유저 인증 실패")
    })
    @Parameters(value = {
            @Parameter(name = "id", description = "모임 id값", required = true),
            @Parameter(name = "name", description = "모임명", example = "코로"),
            @Parameter(name = "introduction", description = "모임 설명", example = "모임 설명입니다."),
            @Parameter(name = "type", description = "모임 대면, 비대면 타입", example = "mixed faceToFace nonContact"),
            @Parameter(name = "visible", description = "공개 모임인지", example = "true"),
            @Parameter(name = "tagList", description = "모임 태그들", example = "[\"tag1\", \"tag2\", \"tag3\"]"),
            @Parameter(name = "moimImage", description = "모임 이미지")
    })
    APIResponse update(@PathVariable("id") Long moimId,
                       @RequestPart(name = "moim", required = false) final MoimModifyRequest requestMoim,
                       @RequestPart(name = "tagList", required = false) final MoimTagRequest requestTag,
                       @RequestPart(name = "moimImage", required = false) final MultipartFile multipartFile) throws IOException;
}
