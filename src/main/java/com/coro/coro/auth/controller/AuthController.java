package com.coro.coro.auth.controller;

import com.coro.coro.auth.annotation.TokenSet;
import com.coro.coro.auth.dto.request.TokenSetRequest;
import com.coro.coro.auth.dto.response.TokenResponse;
import com.coro.coro.auth.service.AuthService;
import com.coro.coro.common.response.APIResponse;
import com.coro.coro.member.dto.request.MemberLoginRequest;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@Builder
@RequestMapping("/api/auth")
public class AuthController implements AuthControllerDocs{

    private final AuthService authService;

    @PostMapping("/login")
    @Override
    public APIResponse login(@RequestBody final MemberLoginRequest requestMember) {
        TokenResponse token = authService.login(requestMember);
        return APIResponse.create()
                .addObject("token", token);
    }

    @Override
    @PostMapping("/new")
    public APIResponse issueNewTokenResponse(@TokenSet final TokenSetRequest tokenSetRequest) {
        TokenResponse tokenResponse = authService.issueNewTokenSet(tokenSetRequest);
        return APIResponse.create()
                .addObject("token", tokenResponse);
    }

    @Override
    @DeleteMapping("/logout")
    public APIResponse logout(@TokenSet final TokenSetRequest tokenSetRequest) {
        authService.logout(tokenSetRequest);
        return APIResponse.create();
    }
}
