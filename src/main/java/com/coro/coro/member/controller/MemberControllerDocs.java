package com.coro.coro.member.controller;

import com.coro.coro.common.response.APIResponse;
import com.coro.coro.member.service.User;
import com.coro.coro.member.dto.request.MemberModificationRequest;
import com.coro.coro.member.dto.request.MemberRegisterRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.IOException;

@Tag(name = "Member", description = "유저")
public interface MemberControllerDocs {

    @Operation(summary = "회원가입")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "회원가입 성공"),
            @ApiResponse(responseCode = "400", description = "회원가입 검증 실패")
    })
    @Parameters(value = {
            @Parameter(name = "email", description = "이메일", example = "asdf@asdf.com", required = true),
            @Parameter(name = "password", description = "비밀번호", example = "asdf1234!@", required = true),
            @Parameter(name = "nickname", description = "닉네임", example = "코로", required = true)
    })
    APIResponse register(@RequestBody final MemberRegisterRequest requestMember);

    @Operation(summary = "유저 정보 획득")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "정보 획득 성공"),
            @ApiResponse(responseCode = "403", description = "유저 인증 실패")
    })
    APIResponse getInformation(@AuthenticationPrincipal final User user);

    @Operation(summary = "유저 정보 변경")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "정보 변경 성공"),
            @ApiResponse(responseCode = "403", description = "회원 인증 실패"),
            @ApiResponse(responseCode = "400", description = "수정 데이터 검증 실패")
    })
    @Parameters(value = {
            @Parameter(name = "id", description = "회원 id", required = true),
            @Parameter(name = "originalPassword", description = "기존 비밀번호"),
            @Parameter(name = "newPassword", description = "새로운 비밀번호"),
    })
    APIResponse updatePassword(@RequestBody final MemberModificationRequest requestMember, @AuthenticationPrincipal final User user) throws IOException;

    @Operation(summary = "내 모임")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "403", description = "회원 인증 실패")
    })
    APIResponse getMoimJoinedList(@AuthenticationPrincipal final User user) throws IOException;

    @Operation(summary = "지원 리스트")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "지원 리스트 획득 성공"),
            @ApiResponse(responseCode = "403", description = "유저 인증 실패")
    })
    @Parameters(value = {
            @Parameter(name = "moim", description = "모임 Id 값", required = true),
            @Parameter(name = "status", description = "획득할 지원 status 값", example = "all, wait accept, refuse")
    })
    APIResponse getApplications(@ModelAttribute(name = "moim") final Long moimId, @AuthenticationPrincipal final User user, final String status);
}
