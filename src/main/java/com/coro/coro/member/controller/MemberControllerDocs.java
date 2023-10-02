package com.coro.coro.member.controller;

import com.coro.coro.common.response.APIResponse;
import com.coro.coro.member.domain.User;
import com.coro.coro.member.dto.request.MemberLoginRequest;
import com.coro.coro.member.dto.request.MemberModifyRequest;
import com.coro.coro.member.dto.request.MemberRegisterRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

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

    @Operation(summary = "로그인")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그인 성공"),
            @ApiResponse(responseCode = "400", description = "회원 검증 실패")
    })
    @Parameters(value = {
            @Parameter(name = "email", description = "이메일", example = "asdf@asdf.com", required = true),
            @Parameter(name = "password", description = "비밀번호", example = "asdf1234!@", required = true)
    })
    APIResponse login(@RequestBody final MemberLoginRequest requestMember);

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
            @Parameter(name = "introduction", description = "회원 소개"),
            @Parameter(name = "profileImage", description = "프로필 이미지")
    })
    APIResponse update(@PathVariable("id") Long memberId, @RequestPart(required = false, name = "profileImage") final MultipartFile multipartFile, @RequestPart(name = "member", required = false) final MemberModifyRequest requestMember) throws IOException;

    @Operation(summary = "내 모임")
    APIResponse getMoim(@AuthenticationPrincipal final User user) throws IOException;
}
