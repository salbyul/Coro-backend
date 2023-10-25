package com.coro.coro.auth.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TokenSetRequest {

    private String accessToken;
    private String refreshToken;
}
