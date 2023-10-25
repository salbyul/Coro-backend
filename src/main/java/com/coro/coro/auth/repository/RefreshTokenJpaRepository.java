package com.coro.coro.auth.repository;

import com.coro.coro.auth.domain.RefreshToken;
import org.springframework.data.repository.CrudRepository;

public interface RefreshTokenJpaRepository extends CrudRepository<RefreshToken, String> {
}
