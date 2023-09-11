package com.coro.coro.member.service;

import com.coro.coro.common.response.error.ErrorType;
import com.coro.coro.member.domain.Member;
import com.coro.coro.member.domain.MemberPhoto;
import com.coro.coro.member.exception.MemberException;
import com.coro.coro.member.repository.MemberPhotoRepository;
import com.coro.coro.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;

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
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(ErrorType.MEMBER_NOT_FOUND));
        String name = generateFileName(multipartFile);
        transferFile(multipartFile, path, name);
        MemberPhoto memberPhoto = memberPhotoRepository.findById(memberId)
                .orElse(new MemberPhoto(member, multipartFile.getOriginalFilename(), name));

        memberPhotoRepository.save(memberPhoto);
        member.preUpdate();
    }

    private String generateFileName(final MultipartFile multipartFile) {
        String uuid = UUID.randomUUID().toString();
        String now = LocalDateTime.now().toString();
        String extra = extractExtra(multipartFile.getOriginalFilename());
        return uuid + "[" + now + "]" + extra;
    }

    private String extractExtra(final String originalName) {
        log.info("file name: {}", originalName);
        return originalName.substring(originalName.indexOf("."));
    }

    private void transferFile(final MultipartFile multipartFile, final String path, final String name) throws IOException {
        multipartFile.transferTo(new File(path, name));
    }
}
