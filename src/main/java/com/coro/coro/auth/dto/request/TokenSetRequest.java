package com.coro.coro.auth.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TokenSetRequest {

    private String refreshToken;
}