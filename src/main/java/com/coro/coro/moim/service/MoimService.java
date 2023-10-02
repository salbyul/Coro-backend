package com.coro.coro.moim.service;

import com.coro.coro.application.domain.ApplicationQuestion;
import com.coro.coro.application.dto.request.ApplicationQuestionRegisterRequest;
import com.coro.coro.application.repository.ApplicationQuestionRepository;
import com.coro.coro.application.validator.ApplicationQuestionValidator;
import com.coro.coro.auth.exception.AuthException;
import com.coro.coro.common.utils.FileSaveUtils;
import com.coro.coro.member.domain.Member;
import com.coro.coro.member.repository.MemberRepository;
import com.coro.coro.moim.domain.*;
import com.coro.coro.moim.dto.request.MoimModificationRequest;
import com.coro.coro.moim.dto.request.MoimRegisterRequest;
import com.coro.coro.moim.dto.request.MoimSearchRequest;
import com.coro.coro.moim.dto.request.MoimTagRequest;
import com.coro.coro.moim.dto.response.MoimDetailResponse;
import com.coro.coro.moim.dto.response.MoimModificationResponse;
import com.coro.coro.moim.dto.response.MoimSearchResponse;
import com.coro.coro.moim.exception.MoimException;
import com.coro.coro.moim.repository.MoimMemberRepository;
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
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
    private final MoimMemberRepository moimMemberRepository;

    @Value("${moim.image.dir}")
    private String path;

    public Page<Moim> search(final MoimSearchRequest moimSearchRequest, final Pageable pageable) {
        Page<Moim> moimPage = null;
        if (moimSearchRequest.getOption().equals(NAME)) {
            moimPage = moimRepository.findAllByName(moimSearchRequest.getValue(), pageable);
        } else if (moimSearchRequest.getOption().equals(TAG)) {
            moimPage = moimRepository.findAllByTag(moimSearchRequest.getValue(), pageable);
        }
        return moimPage;
    }

    @Transactional
    public Long register(final MoimRegisterRequest requestMoim,
                         final MoimTagRequest requestMoimTag,
                         final List<ApplicationQuestionRegisterRequest> requestQuestions,
                         final MultipartFile multipartFile,
                         final Long memberId) throws IOException {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new AuthException(AUTH_ERROR));

        Moim moim = saveMoim(requestMoim, member);
        saveTag(requestMoimTag, moim);
        saveQuestions(requestQuestions, moim);
        if (multipartFile != null && !multipartFile.isEmpty()) {
            updateImage(moim, multipartFile);
        }

        MoimMember moimMember = MoimMember.generate(moim, member);
        moimMemberRepository.save(moimMember);

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
    public void update(final Long moimId,
                       final MoimModificationRequest requestMoim,
                       final MoimTagRequest requestTag,
                       final MultipartFile multipartFile,
                       final List<ApplicationQuestionRegisterRequest> requestQuestions,
                       final Long memberId) throws IOException {
        Moim moim = moimRepository.findById(moimId)
                .orElseThrow(() -> new MoimException(MOIM_NOT_FOUND));

        moimMemberRepository.findByMoimIdAndMemberId(moimId, memberId)
                .orElseThrow(() -> new MoimException(MOIM_FORBIDDEN));

        updateMoim(requestMoim, requestTag, moim);
        saveQuestions(requestQuestions, moim);

        if (multipartFile != null) {
            updateImage(moim, multipartFile);
        }
        if (requestMoim.getIsDeletedPhoto()) {
            moimPhotoRepository.deleteById(moim.getId());
        }
    }

    private void updateMoim(final MoimModificationRequest requestMoim, final MoimTagRequest requestTag, final Moim moim) {
        moim.changeTo(requestMoim);
        MoimValidator.validateMoim(moim);

        moimTagRepository.deleteAllByMoim(moim);
        saveTag(requestTag, moim);
    }

    @Transactional
    public void updateImage(final Moim moim, final MultipartFile multipartFile) throws IOException {
        moimPhotoRepository.deleteById(moim.getId());

        String name = FileSaveUtils.generateFileName(multipartFile);
        FileSaveUtils.transferFile(multipartFile, path, name);
        moimPhotoRepository.save(new MoimPhoto(moim, multipartFile.getOriginalFilename(), name));
    }

    public List<MoimSearchResponse> getSummaryMoim(final List<Moim> moimList) throws IOException {
        List<Long> moimIds = moimList.stream()
                .map(Moim::getId)
                .collect(Collectors.toList());

        List<MoimPhoto> photos = moimPhotoRepository.findAllByIds(moimIds);

        List<MoimSearchResponse> result = new ArrayList<>();

        for (Moim moim : moimList) {
            boolean isAdded = false;
            for (MoimPhoto photo : photos) {
                if (moim.getId().equals(photo.getId())) {
                    byte[] photoBytes = getPhoto(photo.getName());
                    result.add(new MoimSearchResponse(moim, photo.getOriginalName(), photoBytes));
                    photos.remove(photo);
                    isAdded =true;
                    break;
                }
            }
            if (!isAdded) {
                result.add(new MoimSearchResponse(moim, null, null));
            }
        }
        return result;
    }

    public MoimSearchResponse getSummaryMoim(final Moim moim) throws IOException {
        Optional<MoimPhoto> optionalMoimPhoto = moimPhotoRepository.findById(moim.getId());
        if (optionalMoimPhoto.isPresent()) {
            MoimPhoto moimPhoto = optionalMoimPhoto.get();
            return new MoimSearchResponse(moim, moimPhoto.getOriginalName(), getPhoto(moimPhoto.getName()));
        }
        return new MoimSearchResponse(moim, null, null);
    }

    private byte[] getPhoto(final String name) throws IOException {
        return Files.readAllBytes(new File(path + name).toPath());
    }

    public MoimDetailResponse getDetail(final Long moimId, final Long memberId) throws IOException {
        Moim moim = moimRepository.findById(moimId)
                .orElseThrow(() -> new MoimException(MOIM_NOT_FOUND));

        MoimDetailResponse result;
        Optional<MoimMember> optionalMoimMember = moimMemberRepository.findByMoimIdAndMemberId(moimId, memberId);

        result = optionalMoimMember
                .map(moimMember -> MoimDetailResponse.generateInstance(moim, moimMember))
                .orElseGet(() -> MoimDetailResponse.generateInstance(moim, false, false));

        Optional<MoimPhoto> optionalMoimPhoto = moimPhotoRepository.findById(moimId);
        if (optionalMoimPhoto.isPresent()) {
            MoimPhoto moimPhoto = optionalMoimPhoto.get();
            result.setPhoto(moimPhoto.getOriginalName(), getPhoto(moimPhoto.getName()));
        }
        return result;
    }

    public MoimModificationResponse getDetailForModification(final Long moimId, final Long memberId) throws IOException {
        Moim moim = moimRepository.findById(moimId)
                .orElseThrow(() -> new MoimException(MOIM_NOT_FOUND));

        Optional<MoimMember> optionalMoimMember = moimMemberRepository.findByMoimIdAndMemberId(moimId, memberId);

        if (optionalMoimMember.isEmpty()) {
            throw new MoimException(MOIM_NOT_FOUND);
        }

        List<ApplicationQuestion> applicationQuestionList = applicationQuestionRepository.findAllByMoimId(moimId);

        MoimModificationResponse result = optionalMoimMember
                .filter(MoimMember::canManage)
                .map(moimMember -> MoimModificationResponse.generate(moim, applicationQuestionList))
                .orElseThrow(() -> new MoimException(MOIM_FORBIDDEN));

        Optional<MoimPhoto> optionalMoimPhoto = moimPhotoRepository.findById(moimId);
        if (optionalMoimPhoto.isPresent()) {
            MoimPhoto moimPhoto = optionalMoimPhoto.get();
            result.setPhoto(moimPhoto.getOriginalName(), getPhoto(moimPhoto.getName()));
        }
        return result;
    }

    public List<Moim> getMoimListByMemberId(final Long memberId) {
        return moimRepository.findAllByMemberId(memberId);
    }
}