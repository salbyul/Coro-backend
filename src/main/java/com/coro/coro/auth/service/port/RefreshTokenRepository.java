package com.coro.coro.auth.service.port;

import com.coro.coro.auth.domain.RefreshToken;

import java.util.Optional;

public interface RefreshTokenRepository {

    void save(final RefreshToken refreshToken);

    Optional<RefreshToken> findById(final String id);

    void delete(final RefreshToken refreshToken);

    void deleteById(final String refreshToken);
}
