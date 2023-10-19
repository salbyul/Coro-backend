package com.coro.coro.moim.repository.port;

import com.coro.coro.moim.domain.Moim;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface MoimRepository {

    Long save(final Moim moim);

    Optional<Moim> findById(final Long id);

    Page<Moim> findPageByName(final String name, final Pageable pageable);

    Page<Moim> findPageByTag(final String tag, final Pageable pageable);

    Optional<Moim> findByName(final String name);

    //    해당 멤버가 가입한 모든 모임 가져오기
    List<Moim> findAllByMemberId(final Long memberId);
}
