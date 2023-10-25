package com.coro.coro.moim.repository.adaptor;

import com.coro.coro.moim.domain.Moim;
import com.coro.coro.moim.repository.MoimJpaRepository;
import com.coro.coro.moim.service.port.MoimRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MoimRepositoryImpl implements MoimRepository {

    private final MoimJpaRepository moimJpaRepository;

    @Override
    public Long save(final Moim moim) {
        return moimJpaRepository.save(moim).getId();
    }

    @Override
    public Optional<Moim> findById(final Long id) {
        return moimJpaRepository.findById(id);
    }

    @Override
    public Page<Moim> findPageByName(final String name, final Pageable pageable) {
        return moimJpaRepository.findAllByName(name, pageable);
    }

    @Override
    public Page<Moim> findPageByTag(final String tag, final Pageable pageable) {
        return moimJpaRepository.findAllByTag(tag, pageable);
    }

    @Override
    public Optional<Moim> findByName(final String name) {
        return moimJpaRepository.findByName(name);
    }

    @Override
    public List<Moim> findAllByMemberId(final Long memberId) {
        return moimJpaRepository.findAllByMemberId(memberId);
    }
}
