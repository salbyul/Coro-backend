package com.coro.coro.application.repository.adaptor;

import com.coro.coro.application.domain.Application;
import com.coro.coro.application.repository.ApplicationJpaRepository;
import com.coro.coro.application.service.port.ApplicationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class ApplicationRepositoryImpl implements ApplicationRepository {

    private final ApplicationJpaRepository applicationJpaRepository;

    @Override
    public Long save(final Application application) {
        return applicationJpaRepository.save(application).getId();
    }

    @Override
    public Optional<Application> findById(final Long id) {
        return applicationJpaRepository.findById(id);
    }

    @Override
    public List<Application> findByMemberIdAndMoimId(final Long memberId, final Long moimId) {
        return applicationJpaRepository.findByMemberIdAndMoimId(memberId, moimId);
    }

    @Override
    public List<Application> findByMemberIdAndMoimIdAndStatus(final Long memberId, final Long moimId, final String status) {
        return applicationJpaRepository.findByMemberIdAndMoimIdAndStatus(memberId, moimId, status);
    }

    @Override
    public List<Application> findAllByMoimId(final Long moimId) {
        return applicationJpaRepository.findAllByMoimId(moimId);
    }

    @Override
    public List<Application> findAllByMoimIdAndStatus(final Long moimId, final String status) {
        return applicationJpaRepository.findAllByMoimIdAndStatus(moimId, status);
    }
}
