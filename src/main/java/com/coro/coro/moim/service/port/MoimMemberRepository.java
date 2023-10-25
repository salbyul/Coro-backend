package com.coro.coro.moim.service.port;

import com.coro.coro.moim.domain.MoimMember;

import java.util.List;
import java.util.Optional;

public interface MoimMemberRepository {

    Long save(final MoimMember moimMember);

    Optional<MoimMember> findByMoimIdAndMemberId(final Long moimId, final Long memberId);

    List<MoimMember> findAllByMoimId(final Long moimId);

    Optional<MoimMember> findById(final Long id);

    void deleteById(final Long id);
}
