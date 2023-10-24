package com.coro.coro.schedule.controller;

import com.coro.coro.common.response.APIResponse;
import com.coro.coro.member.service.User;
import com.coro.coro.schedule.dto.request.ScheduleRegisterRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Schedule", description = "일정")
public interface ScheduleControllerDocs {

    @Operation(summary = "일정 등록")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "일정 등록 성공"),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "400", description = "양식에 맞지 않은 일정")
    })
    @Parameters(value = {
            @Parameter(name = "title", description = "일정 제목"),
            @Parameter(name = "content", description = "일정 내용"),
            @Parameter(name = "date", description = "일정 날짜")
    })
    APIResponse register(@ModelAttribute(name = "moim") final Long moimId, @RequestBody final ScheduleRegisterRequest registerRequest, @AuthenticationPrincipal final User user);
}
