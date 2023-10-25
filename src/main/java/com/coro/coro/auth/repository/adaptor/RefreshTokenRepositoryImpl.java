package com.coro.coro.auth.repository.adaptor;

import com.coro.coro.auth.domain.RefreshToken;
import com.coro.coro.auth.repository.RefreshTokenJpaRepository;
import com.coro.coro.auth.service.port.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class RefreshTokenRepositoryImpl implements RefreshTokenRepository {

    private final RefreshTokenJpaRepository refreshTokenJpaRepository;

    @Override
    public void save(final RefreshToken refreshToken) {
        refreshTokenJpaRepository.save(refreshToken);
    }

    @Override
    public Optional<RefreshToken> findById(final String id) {
        return refreshTokenJpaRepository.findById(id);
    }

    @Override
    public void delete(final RefreshToken refreshToken) {
        refreshTokenJpaRepository.delete(refreshToken);
    }
}
