package com.coro.coro.moim.controller;

import com.coro.coro.common.response.APIResponse;
import com.coro.coro.member.domain.User;
import com.coro.coro.moim.dto.request.MoimModifyRequest;
import com.coro.coro.moim.dto.request.MoimRegisterRequest;
import com.coro.coro.moim.dto.request.MoimTagRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name = "Moim", description = "모임")
public interface MoimControllerDocs {

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
    APIResponse register(@RequestPart final MoimRegisterRequest requestMoim, @RequestPart final MoimTagRequest requestTag, @AuthenticationPrincipal final User user);

    APIResponse update(@RequestPart final MoimModifyRequest requestMoim, @RequestPart final MoimTagRequest requestTag, @RequestPart List<MultipartFile> multipartFileList, @AuthenticationPrincipal final User user);
}
