package com.coro.coro.mock.repository;

import com.coro.coro.member.domain.MemberPhoto;
import com.coro.coro.member.repository.port.MemberPhotoRepository;

import java.util.Objects;
import java.util.Optional;

public class FakeMemberPhotoRepository implements MemberPhotoRepository {

    private final DataSet dataSet;

    public FakeMemberPhotoRepository(final DataSet dataSet) {
        this.dataSet = dataSet;
    }

    @Override
    public Long save(final MemberPhoto memberPhoto) {
        if (Objects.isNull(memberPhoto.getMemberId())) {
            throw new IllegalArgumentException("MemberPhoto must have Id!!!");
        }
        dataSet.memberPhotoData.put(memberPhoto.getMemberId(), memberPhoto);
        return memberPhoto.getMemberId();
    }

    @Override
    public Optional<MemberPhoto> findById(final Long id) {
        return Optional.ofNullable(dataSet.memberPhotoData.get(id));
    }

    @Override
    public void deleteById(final Long id) {
        dataSet.memberPhotoData.remove(id);
    }
}
