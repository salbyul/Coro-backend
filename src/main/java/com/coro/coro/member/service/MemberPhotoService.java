package com.coro.coro.member.service;

import com.coro.coro.common.utils.FileSaveUtils;
import com.coro.coro.member.domain.Member;
import com.coro.coro.member.domain.MemberPhoto;
import com.coro.coro.member.exception.MemberException;
import com.coro.coro.member.repository.port.MemberPhotoRepository;
import com.coro.coro.member.repository.port.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static com.coro.coro.common.response.error.ErrorType.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberPhotoService {

    private final MemberRepository memberRepository;
    private final MemberPhotoRepository memberPhotoRepository;

    @Value("${profile.image.dir}")
    private String path;

    @Transactional
    public void changeProfileImage(final Long memberId, final MultipartFile multipartFile) throws IOException {
        Member member = getMemberById(memberId);
        String name = FileSaveUtils.generateFileName(multipartFile);
        FileSaveUtils.transferFile(multipartFile, path, name);

        memberPhotoRepository.deleteById(memberId);

        MemberPhoto memberPhoto = MemberPhoto.builder()
                .memberId(memberId)
                .member(member)
                .originalName(multipartFile.getOriginalFilename())
                .name(name)
                .build();
        memberPhotoRepository.save(memberPhoto);

        member.preUpdate();
    }

    private Member getMemberById(final Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MEMBER_NOT_FOUND));
    }
}
