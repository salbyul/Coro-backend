package com.coro.coro.auth.controller;

import com.coro.coro.auth.annotation.TokenSet;
import com.coro.coro.auth.dto.request.TokenSetRequest;
import com.coro.coro.auth.dto.response.TokenResponse;
import com.coro.coro.auth.service.AuthService;
import com.coro.coro.common.response.APIResponse;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@Builder
@RequestMapping("/api/auth")
public class AuthController implements AuthControllerDocs{

    private final AuthService authService;

    @Override
    @PostMapping("/new")
    public APIResponse newTokenResponse(@TokenSet final TokenSetRequest tokenSetRequest) {
        TokenResponse tokenResponse = authService.issueNewTokenSet(tokenSetRequest);
        return APIResponse.create()
                .addObject("token", tokenResponse);
    }
}
