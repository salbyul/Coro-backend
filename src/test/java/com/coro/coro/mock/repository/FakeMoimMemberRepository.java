package com.coro.coro.mock.repository;

import com.coro.coro.moim.domain.MoimMember;
import com.coro.coro.moim.repository.port.MoimMemberRepository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class FakeMoimMemberRepository implements MoimMemberRepository {

    private final DataSet dataSet;

    public FakeMoimMemberRepository(final DataSet dataSet) {
        this.dataSet = dataSet;
    }

    @Override
    public Long save(final MoimMember moimMember) {
        if (Objects.isNull(moimMember.getId())) {
            MoimMember toBeSaved = MoimMember.builder()
                    .id(dataSet.moimMemberSequence++)
                    .moim(moimMember.getMoim())
                    .member(moimMember.getMember())
                    .role(moimMember.getRole())
                    .build();
            toBeSaved.prePersist();
            dataSet.moimMemberData.put(toBeSaved.getId(), toBeSaved);
            return toBeSaved.getId();
        }
        moimMember.preUpdate();
        dataSet.moimMemberData.put(moimMember.getId(), moimMember);
        return moimMember.getId();
    }

    @Override
    public Optional<MoimMember> findByMoimIdAndMemberId(final Long moimId, final Long memberId) {
        return dataSet.moimMemberData.values().stream()
                .filter(moimMember ->
                        moimMember.getMoim().getId().equals(moimId) &&
                        moimMember.getMember().getId().equals(memberId))
                .findFirst();
    }

    @Override
    public List<MoimMember> findAllByMoimId(final Long moimId) {
        return dataSet.moimMemberData.values().stream()
                .filter(moimMember -> moimMember.getMoim().getId().equals(moimId))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<MoimMember> findById(final Long id) {
        return dataSet.moimMemberData.values().stream()
                .filter(moimMember -> moimMember.getId().equals(id))
                .findFirst();
    }

    @Override
    public void deleteById(final Long id) {
        dataSet.moimMemberData.remove(id);
    }
}
