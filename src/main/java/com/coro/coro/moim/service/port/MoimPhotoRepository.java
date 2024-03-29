package com.coro.coro.moim.service.port;

import com.coro.coro.moim.domain.MoimPhoto;

import java.util.List;
import java.util.Optional;

public interface MoimPhotoRepository {

    void deleteById(final Long id);

    Long save(final MoimPhoto moimPhoto);

    List<MoimPhoto> findAllByIds(final List<Long> moimIdList);

    Optional<MoimPhoto> findById(final Long id);
}
