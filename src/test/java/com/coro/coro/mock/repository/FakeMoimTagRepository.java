package com.coro.coro.mock.repository;

import com.coro.coro.moim.domain.MoimTag;
import com.coro.coro.moim.repository.port.MoimTagRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class FakeMoimTagRepository implements MoimTagRepository {

    private final DataSet dataSet;

    public FakeMoimTagRepository(final DataSet dataSet) {
        this.dataSet = dataSet;
    }

    @Override
    public void saveAll(final List<MoimTag> tagList) {
        for (MoimTag moimTag : tagList) {
            if (Objects.nonNull(moimTag.getId())) {
                dataSet.moimTagData.put(moimTag.getId(), moimTag);
                continue;
            }
            dataSet.moimTagData.put(dataSet.moimTagSequence++, moimTag);
        }
    }

    @Override
    public void deleteAllByMoimId(final Long moimId) {
        Map<Long, MoimTag> copiedData = new HashMap<>(dataSet.moimTagData);
        for (Long tagId : copiedData.keySet()) {
            if (dataSet.moimTagData.get(tagId).getMoim().getId().equals(moimId)) {
                dataSet.moimTagData.remove(tagId);
            }
        }
    }
}
