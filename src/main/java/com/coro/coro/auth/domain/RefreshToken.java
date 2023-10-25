package com.coro.coro.auth.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Getter
@RedisHash(value = "refreshToken", timeToLive = 60L * 60 * 24 * 7)
@AllArgsConstructor
@Builder
public class RefreshToken {

    @Id
    private String refreshToken;
    private String nickname;
}
