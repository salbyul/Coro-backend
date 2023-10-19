package com.coro.coro.moim.repository.port;

import com.coro.coro.moim.domain.MoimTag;

import java.util.List;

public interface MoimTagRepository {

    void saveAll(final List<MoimTag> tagList);

    void deleteAllByMoimId(final Long moimId);
}
