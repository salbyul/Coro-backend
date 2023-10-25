package com.coro.coro.auth.controller;

import com.coro.coro.auth.annotation.TokenSet;
import com.coro.coro.auth.dto.request.TokenSetRequest;
import com.coro.coro.common.response.APIResponse;
import com.coro.coro.member.dto.request.MemberLoginRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Auth", description = "인증")
public interface AuthControllerDocs {

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

    @Operation(summary = "새로운 토큰 셋 발급")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "토큰 재발급 성공"),
            @ApiResponse(responseCode = "400", description = "유효하지 않은 토큰")
    })
    @Parameters(value = {
            @Parameter(name = "accessToken", description = "엑세스 토큰 (쿠키)"),
            @Parameter(name = "refreshToken", description = "리프레쉬 토큰 (쿠키)")
    })
    APIResponse newTokenResponse(@TokenSet final TokenSetRequest tokenSetRequest);

    @Operation(summary = "로그아웃")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그아웃 성공"),
    })
    @Parameters(value = {
            @Parameter(name = "accessToken", description = "엑세스 토큰"),
            @Parameter(name = "refreshToken", description = "리프레쉬 토큰")
    })
    APIResponse logout(@TokenSet final TokenSetRequest tokenSetRequest);
}
