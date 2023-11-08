package com.coro.coro.application.service.port;

import com.coro.coro.application.domain.Application;

import java.util.List;
import java.util.Optional;

public interface ApplicationRepository {

    Long save(final Application application);

    Optional<Application> findById(final Long id);

    List<Application> findByMemberIdAndMoimId(final Long memberId, final Long moimId);

    List<Application> findByMemberIdAndMoimIdAndStatus(final Long memberId, final Long moimId, final String status);

    List<Application> findAllByMoimId(final Long moimId);

    List<Application> findAllByMoimIdAndStatus(final Long moimId, final String status);
}
