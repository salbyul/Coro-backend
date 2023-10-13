package com.coro.coro.member.repository.adaptor;

import com.coro.coro.member.domain.MemberPhoto;
import com.coro.coro.member.repository.MemberPhotoJpaRepository;
import com.coro.coro.member.repository.port.MemberPhotoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class MemberPhotoRepositoryImpl implements MemberPhotoRepository {

    private final MemberPhotoJpaRepository memberPhotoJpaRepository;

    @Override
    public Long save(final MemberPhoto memberPhoto) {
        return memberPhotoJpaRepository.save(memberPhoto).getMemberId();
    }

    @Override
    public Optional<MemberPhoto> findById(final Long id) {
        return memberPhotoJpaRepository.findById(id);
    }

    @Override
    public void deleteById(final Long id) {
        memberPhotoJpaRepository.deleteById(id);
    }
}
