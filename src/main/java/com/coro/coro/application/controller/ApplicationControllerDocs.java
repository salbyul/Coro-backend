package com.coro.coro.application.controller;

import com.coro.coro.common.response.APIResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PathVariable;

@Tag(name = "Application", description = "지원 관련")
public interface ApplicationControllerDocs {

    @Operation(summary = "지원 질문 리스트")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "지원 질문 리스트 획득 성공"),
            @ApiResponse(responseCode = "403", description = "유저 인증 실패")
    })
    @Parameters(value = {
            @Parameter(name = "id", description = "모임 Id 값")
    })
    APIResponse getQuestionList(@PathVariable("id") Long moimId);
}
