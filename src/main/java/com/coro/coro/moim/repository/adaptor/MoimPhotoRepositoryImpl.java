package com.coro.coro.moim.repository.adaptor;

import com.coro.coro.moim.domain.MoimPhoto;
import com.coro.coro.moim.repository.MoimPhotoJpaRepository;
import com.coro.coro.moim.repository.port.MoimPhotoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MoimPhotoRepositoryImpl implements MoimPhotoRepository {

    private final MoimPhotoJpaRepository moimPhotoJpaRepository;

    @Override
    public void deleteById(final Long id) {
        moimPhotoJpaRepository.deleteById(id);
    }

    @Override
    public Long save(final MoimPhoto moimPhoto) {
        return moimPhotoJpaRepository.save(moimPhoto).getId();
    }

    @Override
    public List<MoimPhoto> findAllByIds(final List<Long> moimIdList) {
        return moimPhotoJpaRepository.findAllByIds(moimIdList);
    }

    @Override
    public Optional<MoimPhoto> findOptionalById(final Long id) {
        return moimPhotoJpaRepository.findById(id);
    }
}
