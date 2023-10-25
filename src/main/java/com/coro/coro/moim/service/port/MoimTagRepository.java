package com.coro.coro.moim.service.port;

import com.coro.coro.moim.domain.MoimTag;

import java.util.List;

public interface MoimTagRepository {

    void saveAll(final List<MoimTag> tagList);

    void deleteAllByMoimId(final Long moimId);
}
