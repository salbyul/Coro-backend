package com.coro.coro.application.controller;

import com.coro.coro.application.annotation.StatusValue;
import com.coro.coro.application.domain.ApplicationStatus;
import com.coro.coro.application.dto.request.ApplicationRequest;
import com.coro.coro.common.response.APIResponse;
import com.coro.coro.member.service.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;

@Tag(name = "Application", description = "지원 관련")
public interface ApplicationControllerDocs {

    @Operation(summary = "지원 리스트")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "지원 리스트 획득 성공"),
            @ApiResponse(responseCode = "403", description = "유저 인증 실패")
    })
    @Parameters(value = {
            @Parameter(name = "moim", description = "모임 Id 값", required = true),
            @Parameter(name = "status", description = "획득할 지원 status 값", example = "all, wait accept, refuse")
    })
    APIResponse getApplication(@ModelAttribute(name = "moim") final Long moimId, final String status);

    @Operation(summary = "해당 유저의 지원서")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "403", description = "유저 인증 실패")
    })
    @Parameters(value = {
            @Parameter(name = "moim", description = "모임 Id 값", required = true),
            @Parameter(name = "status", description = "지원서 상태값", example = "wait, accept, refuse")
    })
    APIResponse getApplicationByMember(@ModelAttribute(name = "moim") final Long moimId, final String status, @AuthenticationPrincipal final User user);

    @Operation(summary = "디테일 지원서 획득")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "403", description = "유저 인증 실패"),
            @ApiResponse(responseCode = "400", description = "잘못된 Id 값")
    })
    @Parameter(name = "id", description = "지원서 Id 값")
    APIResponse getDetailedApplication(@PathVariable(name = "id") final Long applicationId);

    @Operation(summary = "지원 질문 리스트")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "지원 질문 리스트 획득 성공"),
            @ApiResponse(responseCode = "403", description = "유저 인증 실패")
    })
    @Parameters(value = {
            @Parameter(name = "id", description = "모임 Id 값")
    })
    APIResponse getQuestionList(@PathVariable("id") Long moimId);

    @Operation(summary = "지원 제출")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "지원 제출 성공"),
            @ApiResponse(responseCode = "403", description = "유저 인증 실패")
    })
    @Parameters(value = {
            @Parameter(name = "applicationRequest", description = "지원 답변 리스트"),
            @Parameter(name = "moimId", description = "모임 Id 값")
    })
    APIResponse submitApplication(@PathVariable("moimId") Long moimId, final ApplicationRequest applicationRequest, @AuthenticationPrincipal final User user);

    @Operation(summary = "지원서 합불합 결정")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "403", description = "유저 인증 실패")
    })
    @Parameters(value = {
            @Parameter(name = "applicationId", description = "지원서 Id 값"),
            @Parameter(name = "value", description = "합불합 값")
    })
    APIResponse decideApplication(@PathVariable(name = "applicationId") final Long applicationId, @StatusValue final ApplicationStatus status, @AuthenticationPrincipal final User user);
}
