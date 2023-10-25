package com.coro.coro.auth.controller;

import com.coro.coro.auth.annotation.TokenSet;
import com.coro.coro.auth.dto.request.TokenSetRequest;
import com.coro.coro.common.response.APIResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Auth", description = "인증")
public interface AuthControllerDocs {

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
}
