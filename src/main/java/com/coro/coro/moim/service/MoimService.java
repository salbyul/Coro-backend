package com.coro.coro.moim.service;

import com.coro.coro.application.domain.ApplicationQuestion;
import com.coro.coro.application.dto.request.ApplicationQuestionRegisterRequest;
import com.coro.coro.application.repository.ApplicationQuestionRepository;
import com.coro.coro.application.validator.ApplicationQuestionValidator;
import com.coro.coro.auth.exception.AuthException;
import com.coro.coro.member.domain.Member;
import com.coro.coro.member.repository.MemberRepository;
import com.coro.coro.moim.domain.Moim;
import com.coro.coro.moim.domain.MoimPhoto;
import com.coro.coro.moim.domain.MoimTag;
import com.coro.coro.moim.domain.MoimType;
import com.coro.coro.moim.dto.request.MoimModifyRequest;
import com.coro.coro.moim.dto.request.MoimRegisterRequest;
import com.coro.coro.moim.dto.request.MoimSearchRequest;
import com.coro.coro.moim.dto.request.MoimTagRequest;
import com.coro.coro.moim.exception.MoimException;
import com.coro.coro.moim.repository.MoimPhotoRepository;
import com.coro.coro.moim.repository.MoimRepository;
import com.coro.coro.moim.repository.MoimTagRepository;
import com.coro.coro.moim.validator.MoimTagValidator;
import com.coro.coro.moim.validator.MoimValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.coro.coro.common.response.error.ErrorType.*;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class MoimService {

    private static final String NAME = "name";
    private static final String TAG = "tag";
    private final MoimRepository moimRepository;

    private final MoimTagRepository moimTagRepository;
    private final MemberRepository memberRepository;
    private final MoimPhotoRepository moimPhotoRepository;
    private final ApplicationQuestionRepository applicationQuestionRepository;

    @Value("${moim.image.dir}")
    private String path;

    public Page<Moim> search(final MoimSearchRequest moimSearchRequest, final Pageable pageable) {
        Page<Moim> moimPage = null;
        if (moimSearchRequest.getOption().equals(NAME)) {
            moimPage = moimRepository.findAllByName(moimSearchRequest.getValue(), pageable);
        } else if (moimSearchRequest.getOption().equals(TAG)) {
            moimPage = moimRepository.findAllByTag(moimSearchRequest.getValue(), pageable);
        }
        if (moimPage == null) {
            return null;
        }
        return moimPage;
    }

    @Transactional
    public Long register(final MoimRegisterRequest requestMoim, final MoimTagRequest requestMoimTag, final List<ApplicationQuestionRegisterRequest> requestQuestions, final Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new AuthException(AUTH_ERROR));

        Moim moim = saveMoim(requestMoim, member);
        saveTag(requestMoimTag, moim);
        saveQuestions(requestQuestions, moim);
        return moim.getId();
    }

    private Moim saveMoim(final MoimRegisterRequest requestMoim, final Member member) {
        Moim moim = Moim.builder()
                .leader(member)
                .name(requestMoim.getName())
                .introduction(requestMoim.getIntroduction())
                .visible(requestMoim.getVisible())
                .type(MoimType.getType(requestMoim.getType()))
                .build();

        MoimValidator.validateMoim(moim);
        validateDuplicateName(moim);

        moimRepository.save(moim);
        return moim;
    }

    private void validateDuplicateName(final Moim moim) {
        Optional<Moim> optionalMoim = moimRepository.findByName(moim.getName());
        if (optionalMoim.isPresent()) {
            throw new MoimException(MOIM_NAME_DUPLICATE);
        }
    }

    private void saveTag(final MoimTagRequest requestMoimTag, final Moim moim) {
        if (requestMoimTag == null) {
            return;
        }
        List<MoimTag> tagList = requestMoimTag.getTagList()
                .stream()
                .map(tag -> MoimTag.generateMoimTag(tag, moim))
                .collect(Collectors.toList());

        MoimTagValidator.validateTag(tagList);
        moimTagRepository.saveAll(tagList);
    }

    private void saveQuestions(final List<ApplicationQuestionRegisterRequest> requestQuestions, final Moim moim) {
        if (requestQuestions == null || requestQuestions.size() == 0) {
            return;
        }

        List<ApplicationQuestion> applicationQuestionList = requestQuestions.stream()
                .map(requestQuestion -> ApplicationQuestion.generateApplicationQuestion(moim, requestQuestion))
                .collect(Collectors.toList());

        ApplicationQuestionValidator.validateApplicationQuestion(applicationQuestionList);

        applicationQuestionRepository.deleteAllByMoimId(moim.getId());
        applicationQuestionRepository.saveAll(applicationQuestionList);
    }

    @Transactional
    public void update(final Long moimId, final MoimModifyRequest requestMoim, final MoimTagRequest requestTag, final MultipartFile multipartFile) throws IOException {
        Moim moim = moimRepository.findById(moimId)
                .orElseThrow(() -> new MoimException(MOIM_NOT_FOUND));

        if (requestMoim != null && requestTag != null) {
            updateMoim(requestMoim, requestTag, moim);
        }
        if (multipartFile != null) {
            updateImage(moim, multipartFile);
        }
    }

    private void updateMoim(final MoimModifyRequest requestMoim, final MoimTagRequest requestTag, final Moim moim) {
        if (!requestMoim.getName().equals(moim.getName())) {
            validateDuplicateName(requestMoim);
        }
        moim.changeTo(requestMoim);
        MoimValidator.validateMoim(moim);

        moimTagRepository.deleteAllByMoim(moim);
        saveTag(requestTag, moim);

        moim.preUpdate();
    }

    private void validateDuplicateName(final MoimModifyRequest requestMoim) {
        boolean isDuplicate = moimRepository.findByName(requestMoim.getName()).isPresent();
        if (isDuplicate) {
            throw new MoimException(MOIM_NAME_DUPLICATE);
        }
    }

    @Transactional
    public void updateImage(final Moim moim, final MultipartFile multipartFile) throws IOException {
        moimPhotoRepository.deleteById(moim.getId());

        String name = generateFileName(multipartFile);
        transferFile(multipartFile, path, name);
        moimPhotoRepository.save(new MoimPhoto(moim, multipartFile.getOriginalFilename(), name));
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