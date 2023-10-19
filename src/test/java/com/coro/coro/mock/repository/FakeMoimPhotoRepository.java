package com.coro.coro.mock.repository;

import com.coro.coro.moim.domain.MoimPhoto;
import com.coro.coro.moim.repository.port.MoimPhotoRepository;

import java.util.*;

public class FakeMoimPhotoRepository implements MoimPhotoRepository {

    private final DataSet dataSet;

    public FakeMoimPhotoRepository(final DataSet dataSet) {
        this.dataSet = dataSet;
    }

    @Override
    public void deleteById(final Long id) {
        dataSet.moimPhotoData.remove(id);
    }

    @Override
    public Long save(final MoimPhoto moimPhoto) {
        if (Objects.isNull(moimPhoto.getId())) {
            throw new IllegalArgumentException("MoimPhoto must have Id!!!");
        }
        dataSet.moimPhotoData.put(moimPhoto.getId(), moimPhoto);
        return moimPhoto.getId();

    }

    @Override
    public List<MoimPhoto> findAllByIds(final List<Long> moimIdList) {
        List<Long> copiedIdList = new ArrayList<>(moimIdList);
        List<MoimPhoto> result = new ArrayList<>();
        Map<Long, MoimPhoto> moimPhotoData = dataSet.moimPhotoData;

        while (copiedIdList.size() > 0) {
            Long key = copiedIdList.get(0);
            MoimPhoto moimPhoto = moimPhotoData.get(key);
            if (Objects.nonNull(moimPhoto)) {
                result.add(moimPhoto);
            }
            copiedIdList.remove(key);
        }
        return result;
    }

    @Override
    public Optional<MoimPhoto> findById(final Long id) {
        return dataSet.moimPhotoData.values().stream()
                .filter(moimPhoto -> moimPhoto.getId().equals(id))
                .findFirst();
    }
}
