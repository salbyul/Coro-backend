package com.coro.coro.moim.repository.adaptor;

import com.coro.coro.moim.domain.MoimTag;
import com.coro.coro.moim.repository.MoimTagJpaRepository;
import com.coro.coro.moim.repository.port.MoimTagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class MoimTagRepositoryImpl implements MoimTagRepository {

    private final MoimTagJpaRepository moimTagJpaRepository;

    @Override
    public void saveAll(final List<MoimTag> tagList) {
        moimTagJpaRepository.saveAll(tagList);
    }

    @Override
    public void deleteAllByMoimId(final Long moimId) {
        moimTagJpaRepository.deleteAllByMoimId(moimId);
    }
}
