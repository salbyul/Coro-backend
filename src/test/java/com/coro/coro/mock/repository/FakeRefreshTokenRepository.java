package com.coro.coro.mock.repository;

import com.coro.coro.auth.domain.RefreshToken;
import com.coro.coro.auth.service.port.RefreshTokenRepository;

import java.util.Optional;

public class FakeRefreshTokenRepository implements RefreshTokenRepository {

    private final DataSet dataSet;
    private final static String REFRESH_TOKEN = "refreshToken";

    public FakeRefreshTokenRepository(final DataSet dataSet) {
        this.dataSet = dataSet;
    }

    @Override
    public void save(final RefreshToken refreshToken) {
        dataSet.refreshTokenData.put(REFRESH_TOKEN + ":" + refreshToken.getRefreshToken(), refreshToken);
    }

    @Override
    public Optional<RefreshToken> findById(final String id) {
        return Optional.ofNullable(dataSet.refreshTokenData.get(REFRESH_TOKEN + ":" + id));
    }

    @Override
    public void delete(final RefreshToken refreshToken) {
        Optional<RefreshToken> optionalRefreshToken = dataSet.refreshTokenData.values().stream()
                .filter(token -> token.getRefreshToken().equals(refreshToken.getRefreshToken()))
                .findAny();
        optionalRefreshToken.ifPresent(token -> dataSet.refreshTokenData.remove(token.getRefreshToken()));
    }
}
