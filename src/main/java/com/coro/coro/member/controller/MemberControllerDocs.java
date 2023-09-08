package com.coro.coro.member.controller;

import com.coro.coro.common.response.APIResponse;
import com.coro.coro.member.dto.request.MemberLoginRequest;
import com.coro.coro.member.dto.request.MemberModifyRequest;
import com.coro.coro.member.dto.request.MemberRegisterRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "Member", description = "유저")
public interface MemberControllerDocs {

    @Operation(summary = "회원가입")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "회원가입 성공"),
            @ApiResponse(responseCode = "400", description = "회원가입 검증 실패")
    })
    @Parameters(value = {
            @Parameter(name = "email", description = "이메일", example = "asdf@asdf.com"),
            @Parameter(name = "password", description = "비밀번호", example = "asdf1234!@"),
            @Parameter(name = "nickname", description = "닉네임", example = "코로")
    })
    APIResponse register(final @RequestBody MemberRegisterRequest requestMember);

    @Operation(summary = "로그인")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그인 성공"),
            @ApiResponse(responseCode = "400", description = "회원 검증 실패")
    })
    @Parameters(value = {
            @Parameter(name = "email", description = "이메일", example = "asdf@asdf.com"),
            @Parameter(name = "password", description = "비밀번호", example = "asdf1234!@")
    })
    APIResponse login(final @RequestBody MemberLoginRequest requestMember);

    @Operation(summary = "유저 정보 변경")
    APIResponse modify(final @RequestPart(required = false) MultipartFile multipartFile, final @RequestPart MemberModifyRequest requestMember);
}
