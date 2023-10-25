package com.coro.coro.moim.repository.adaptor;

import com.coro.coro.moim.domain.MoimMember;
import com.coro.coro.moim.repository.MoimMemberJpaRepository;
import com.coro.coro.moim.service.port.MoimMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MoimMemberRepositoryImpl implements MoimMemberRepository {

    private final MoimMemberJpaRepository moimMemberJpaRepository;

    @Override
    public Long save(final MoimMember moimMember) {
        return moimMemberJpaRepository.save(moimMember).getId();
    }

    @Override
    public Optional<MoimMember> findByMoimIdAndMemberId(final Long moimId, final Long memberId) {
        return moimMemberJpaRepository.findByMoimIdAndMemberId(moimId, memberId);
    }

    @Override
    public List<MoimMember> findAllByMoimId(final Long moimId) {
        return moimMemberJpaRepository.findAllByMoimId(moimId);
    }

    @Override
    public Optional<MoimMember> findById(final Long id) {
        return moimMemberJpaRepository.findById(id);
    }

    @Override
    public void deleteById(final Long id) {
        moimMemberJpaRepository.deleteById(id);
    }
}
