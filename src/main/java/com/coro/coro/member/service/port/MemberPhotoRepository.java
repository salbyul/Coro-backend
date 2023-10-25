package com.coro.coro.member.service.port;

import com.coro.coro.member.domain.MemberPhoto;

import java.util.Optional;

public interface MemberPhotoRepository {

    Long save(final MemberPhoto memberPhoto);

    Optional<MemberPhoto> findById(final Long id);

    void deleteById(final Long id);
}
