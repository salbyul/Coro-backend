package com.coro.coro.moim.service;

import com.coro.coro.application.domain.ApplicationQuestion;
import com.coro.coro.application.dto.request.ApplicationQuestionRegisterRequest;
import com.coro.coro.application.service.port.ApplicationQuestionRepository;
import com.coro.coro.application.validator.ApplicationQuestionValidator;
import com.coro.coro.common.service.port.FileTransferor;
import com.coro.coro.common.utils.FileSaveUtils;
import com.coro.coro.member.domain.Member;
import com.coro.coro.member.domain.MemberRole;
import com.coro.coro.member.service.port.MemberRepository;
import com.coro.coro.common.service.port.DateTimeHolder;
import com.coro.coro.common.service.port.UUIDHolder;
import com.coro.coro.moim.domain.*;
import com.coro.coro.moim.dto.request.*;
import com.coro.coro.moim.dto.response.MoimDetailResponse;
import com.coro.coro.moim.dto.response.MoimMemberResponse;
import com.coro.coro.moim.dto.response.MoimModificationResponse;
import com.coro.coro.moim.dto.response.MoimSearchResponse;
import com.coro.coro.moim.exception.MoimException;
import com.coro.coro.moim.service.port.MoimMemberRepository;
import com.coro.coro.moim.service.port.MoimPhotoRepository;
import com.coro.coro.moim.service.port.MoimRepository;
import com.coro.coro.moim.service.port.MoimTagRepository;
import com.coro.coro.moim.validator.MoimTagValidator;
import com.coro.coro.moim.validator.MoimValidator;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.coro.coro.common.response.error.ErrorType.*;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Builder
@Service
public class MoimService {

    private static final String NAME = "name";
    private static final String TAG = "tag";

    private final MoimRepository moimRepository;
    private final MoimTagRepository moimTagRepository;
    private final MoimPhotoRepository moimPhotoRepository;
    private final MoimMemberRepository moimMemberRepository;
    private final MoimValidator moimValidator;

    private final MemberRepository memberRepository;

    private final ApplicationQuestionRepository applicationQuestionRepository;

    private final UUIDHolder uuidHolder;
    private final DateTimeHolder dateTimeHolder;
    private final FileTransferor fileTransferor;

    private static String path;

    @Value("${moim.image.dir}")
    public void setPath(final String path) {
        MoimService.path = path;
    }

    public Page<Moim> search(final MoimSearchRequest moimSearchRequest, final Pageable pageable) {
        if (moimSearchRequest.getOption().equals(NAME)) {
            return moimRepository.findPageByName(moimSearchRequest.getValue(), pageable);
        } else if (moimSearchRequest.getOption().equals(TAG)) {
            return moimRepository.findPageByTag(moimSearchRequest.getValue(), pageable);
        }
        return new PageImpl<>(new ArrayList<>());
    }

    @Transactional
    public Long register(final MoimRegisterRequest requestMoim,
                         final MoimTagRequest requestMoimTag,
                         final List<ApplicationQuestionRegisterRequest> requestQuestions,
                         final MultipartFile multipartFile,
                         final Long memberId) throws IOException {
        Member member = getMemberById(memberId);

        Long savedMoimId = saveMoim(requestMoim, member);
        Moim moim = getMoimById(savedMoimId);

        saveTags(requestMoimTag, moim);
        saveQuestions(requestQuestions, moim);

        if (multipartFile != null && !multipartFile.isEmpty()) {
            updateImage(moim, multipartFile);
        }

        MoimMember moimMember = MoimMember.builder()
                .moim(moim)
                .member(member)
                .role(MemberRole.LEADER)
                .build();
        moimMemberRepository.save(moimMember);

        return moim.getId();
    }

    private Member getMemberById(final Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new MoimException(MEMBER_NOT_FOUND));
    }

    private Long saveMoim(final MoimRegisterRequest requestMoim, final Member member) {
        Moim moim = Moim.builder()
                .leader(member)
                .name(requestMoim.getName())
                .introduction(requestMoim.getIntroduction())
                .visible(requestMoim.getVisible())
                .type(MoimType.getType(requestMoim.getType()))
                .build();

        moimValidator.validateMoim(moim);
        moimValidator.validateDuplicateName(moim.getName());
        return moimRepository.save(moim);
    }

    private Moim getMoimById(final Long moimId) {
        return moimRepository.findById(moimId)
                .orElseThrow(() -> new MoimException(MOIM_NOT_FOUND));
    }

    private void saveTags(final MoimTagRequest requestMoimTag, final Moim moim) {
        List<MoimTag> tagList = requestMoimTag.getTagList()
                .stream()
                .map(tag ->
                    MoimTag.builder()
                            .name(tag)
                            .moim(moim)
                            .build()
                )
                .collect(Collectors.toList());
        MoimTagValidator.validateTag(tagList);
        moim.changeMoimTagListTo(tagList);
        moimTagRepository.saveAll(tagList);
    }

    @Transactional
    public void update(final Long moimId,
                       final MoimModificationRequest requestMoim,
                       final MoimTagRequest requestTag,
                       final MultipartFile multipartFile,
                       final List<ApplicationQuestionRegisterRequest> requestQuestions,
                       final Long managerId) throws IOException {
        Moim moim = getMoimById(moimId);

        MoimMember moimMember = getMoimMemberByMoimIdAndMemberId(moimId, managerId);
        if (!moimMember.canManage()) {
            throw new MoimException(MOIM_FORBIDDEN);
        }

        if (!requestMoim.getName().equals(moim.getName())) {
            moimValidator.validateDuplicateName(requestMoim.getName());
        }
        moim.update(requestMoim);
        moimValidator.validateMoim(moim);

        moimTagRepository.deleteAllByMoimId(moim.getId());
        saveTags(requestTag, moim);

        applicationQuestionRepository.deleteAllByMoimId(moim.getId());
        saveQuestions(requestQuestions, moim);

        if (multipartFile != null && !multipartFile.isEmpty()) {
            updateImage(moim, multipartFile);
        }
        if (requestMoim.getIsDeletedPhoto()) {
            moimPhotoRepository.deleteById(moim.getId());
        }
    }

    private void saveQuestions(final List<ApplicationQuestionRegisterRequest> requestQuestions, final Moim moim) {
        if (requestQuestions.isEmpty()) {
            return;
        }

        List<ApplicationQuestion> applicationQuestionList = requestQuestions.stream()
                .map(requestQuestion ->
                    ApplicationQuestion.builder()
                            .order(requestQuestion.getOrder())
                            .moim(moim)
                            .content(requestQuestion.getContent())
                            .build()
                )
                .collect(Collectors.toList());
        ApplicationQuestionValidator.validateApplicationQuestion(applicationQuestionList);
        moim.changeQuestionListTo(applicationQuestionList);
        applicationQuestionRepository.saveAll(applicationQuestionList);
    }

    @Transactional
    public void updateImage(final Moim moim, final MultipartFile multipartFile) throws IOException {
        moimPhotoRepository.deleteById(moim.getId());

        validateImageFile(multipartFile);

        String generatedFileName = FileSaveUtils.generateFileName(multipartFile, dateTimeHolder, uuidHolder);
        fileTransferor.saveFile(multipartFile, path, generatedFileName);
        moimPhotoRepository.save(
                MoimPhoto.builder()
                        .id(moim.getId())
                        .moim(moim)
                        .originalName(multipartFile.getOriginalFilename())
                        .name(generatedFileName)
                        .contentType(multipartFile.getContentType())
                        .build()
                );
    }

    @SuppressWarnings("ConstantConditions")
    public void validateImageFile(final MultipartFile multipartFile) {
        if (!multipartFile.getOriginalFilename().contains(".") || !multipartFile.getContentType().contains("image")) {
            throw new MoimException(MOIM_PHOTO_NOT_VALID);
        }
    }

    public List<MoimSearchResponse> getSummaryMoim(final List<Moim> moimList) throws IOException {
        if (moimList.isEmpty()) {
            return new ArrayList<>();
        }

        List<Long> moimIds = moimList.stream()
                .map(Moim::getId)
                .collect(Collectors.toList());
        List<MoimPhoto> moimPhotoList = moimPhotoRepository.findAllByIds(moimIds);
        return assembleSearchResponseList(moimList, moimPhotoList);
    }

    private List<MoimSearchResponse> assembleSearchResponseList(List<Moim> moimList, List<MoimPhoto> moimPhotoList) throws IOException {
        List<MoimSearchResponse> result = new ArrayList<>();

        for (Moim moim : moimList) {
            boolean isAdded = false;
            for (MoimPhoto photo : moimPhotoList) {
                if (moim.getId().equals(photo.getId())) {
                    byte[] photoBytes = fileTransferor.getFile(photo.getName(), path);
                    result.add(new MoimSearchResponse(moim, photo, photoBytes));
                    moimPhotoList.remove(photo);
                    isAdded = true;
                    break;
                }
            }
            if (!isAdded) {
                result.add(new MoimSearchResponse(moim, null, null));
            }
        }
        return result;
    }

    public MoimDetailResponse getDetail(final Long moimId, final Long memberId) throws IOException {
        Moim moim = getMoimById(moimId);

        Optional<MoimMember> optionalMoimMember = moimMemberRepository.findByMoimIdAndMemberId(moimId, memberId);

        MoimDetailResponse response = optionalMoimMember
                .map(moimMember -> MoimDetailResponse.generateInstance(moim, moimMember))
                .orElseGet(() -> MoimDetailResponse.generateInstance(moim, false, false));

        Optional<MoimPhoto> optionalMoimPhoto = moimPhotoRepository.findById(moimId);
        if (optionalMoimPhoto.isPresent()) {
            MoimPhoto moimPhoto = optionalMoimPhoto.get();
            response.setPhoto(moimPhoto, fileTransferor.getFile(moimPhoto.getName(), path));
        }
        return response;
    }

    public MoimModificationResponse getDetailForModification(final Long moimId, final Long loggedInMemberId) throws IOException {
        Moim moim = getMoimById(moimId);

        MoimMember moimMember = getMoimMemberByMoimIdAndMemberId(moimId, loggedInMemberId);
        if (!moimMember.canManage()) {
            throw new MoimException(MOIM_FORBIDDEN);
        }

        List<ApplicationQuestion> applicationQuestionList = applicationQuestionRepository.findAllByMoimId(moimId);
        MoimModificationResponse result = MoimModificationResponse.generate(moim, applicationQuestionList);

        Optional<MoimPhoto> optionalMoimPhoto = moimPhotoRepository.findById(moimId);
        if (optionalMoimPhoto.isPresent()) {
            MoimPhoto moimPhoto = optionalMoimPhoto.get();
            result.setPhoto(moimPhoto, fileTransferor.getFile(moimPhoto.getName(), path));
        }
        return result;
    }

    private MoimMember getMoimMemberByMoimIdAndMemberId(final Long moimId, final Long memberId) {
        return moimMemberRepository.findByMoimIdAndMemberId(moimId, memberId)
                .orElseThrow(() -> new MoimException(MOIM_MEMBER_NOT_FOUND));
    }

    public List<Moim> getMoimListByMemberId(final Long memberId) {
        return moimRepository.findAllByMemberId(memberId);
    }

    public List<MoimMemberResponse> getMoimMemberList(final Long moimId) {
        getMoimById(moimId);
        List<MoimMember> moimMemberList = moimMemberRepository.findAllByMoimId(moimId);
        return moimMemberList.stream()
                .map(MoimMemberResponse::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public void modifyMoimMember(final Long moimId, final List<MoimMemberModificationRequest> requestMoimMember, final Long loggedInMemberId) {
        MoimMember moimMember = getMoimMemberByMoimIdAndMemberId(moimId, loggedInMemberId);
        if (!moimMember.canManage()) {
            throw new MoimException(MOIM_FORBIDDEN);
        }

        List<MoimMember> moimMemberList = moimMemberRepository.findAllByMoimId(moimId);
        validateModificationMoimMember(requestMoimMember);
        updateMoimMember(moimMemberList, requestMoimMember);
    }

    private void validateModificationMoimMember(final List<MoimMemberModificationRequest> requestMoimMember) {
        long leaderCounts = requestMoimMember.stream()
                .filter(moimMember -> moimMember.getRole().isLeader())
                .count();
        if (leaderCounts != 1) {
            throw new MoimException(MOIM_MEMBER_NOT_VALID);
        }
    }

    private void updateMoimMember(final List<MoimMember> moimMemberList, final List<MoimMemberModificationRequest> requestMoimMemberList) {
        if (moimMemberList.size() != requestMoimMemberList.size()) {
            throw new MoimException(MOIM_MEMBER_NOT_VALID);
        }
        List<MoimMember> requests = new ArrayList<>(List.copyOf(moimMemberList));
        for (MoimMember moimMember : moimMemberList) {
            for (MoimMemberModificationRequest moimMemberRequest : requestMoimMemberList) {
                if (moimMember.getId().equals(moimMemberRequest.getId())) {
                    moimMember.update(moimMemberRequest);
                    requests.remove(moimMember);
                    break;
                }
            }
        }
        if (!requests.isEmpty()) {
            throw new MoimException(MOIM_MEMBER_NOT_VALID);
        }
    }

    public MemberRole getMemberRole(final Long memberId, final Long moimId) {
        return moimMemberRepository.findByMoimIdAndMemberId(moimId, memberId)
                .map(MoimMember::getRole)
                .orElseThrow(() -> new MoimException(MOIM_MEMBER_NOT_FOUND));
    }

    @Transactional
    public void deport(final Long moimId, final Long moimMemberId, final Long loggedInMemberId) {
        getMoimById(moimId);

        MoimMember loggedInMoimMember = getMoimMemberByMoimIdAndMemberId(moimId, loggedInMemberId);
        if (!loggedInMoimMember.canManage()) {
            throw new MoimException(MOIM_MEMBER_FORBIDDN);
        }
        MoimMember moimMember = getMoimMemberById(moimMemberId);
        if (moimMember.getRole().isLeader()) {
            throw new MoimException(MOIM_FORBIDDEN);
        }

        moimMemberRepository.deleteById(moimMember.getId());
    }

    private MoimMember getMoimMemberById(final Long moimMemberId) {
        return moimMemberRepository.findById(moimMemberId)
                .orElseThrow(() -> new MoimException(MOIM_MEMBER_NOT_FOUND));
    }
}