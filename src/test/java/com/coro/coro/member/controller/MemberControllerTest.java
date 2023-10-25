package com.coro.coro.member.controller;

import com.coro.coro.application.domain.ApplicationStatus;
import com.coro.coro.application.dto.request.ApplicationRequest;
import com.coro.coro.application.dto.response.ApplicationResponse;
import com.coro.coro.common.response.APIResponse;
import com.coro.coro.member.domain.Member;
import com.coro.coro.member.domain.MemberPhoto;
import com.coro.coro.member.dto.request.MemberModificationRequest;
import com.coro.coro.member.dto.request.MemberRegisterRequest;
import com.coro.coro.member.exception.MemberException;
import com.coro.coro.member.service.User;
import com.coro.coro.mock.FakeContainer;
import com.coro.coro.moim.domain.Moim;
import com.coro.coro.moim.domain.MoimPhoto;
import com.coro.coro.moim.dto.request.MoimRegisterRequest;
import com.coro.coro.moim.dto.request.MoimTagRequest;
import com.coro.coro.moim.dto.response.MoimSearchResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.util.List;

import static com.coro.coro.common.response.error.ErrorType.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertAll;

class MemberControllerTest {

    @Test
    @DisplayName("회원가입 성공")
    void register() {
        FakeContainer container = new FakeContainer();

//        회원가입
        MemberRegisterRequest request = new MemberRegisterRequest("asdf@asdf.com", "asdf1234!@", "닉네임");
        APIResponse joinResponse = container.memberController.register(request);
        Long savedId = (Long) joinResponse.getBody().get("savedId");

        Member member = container.memberRepository.findById(savedId)
                .orElseThrow(() -> new MemberException(MEMBER_NOT_FOUND));

        boolean isMatchedPassword = container.passwordEncoder.matches("asdf1234!@", member.getPassword());

        assertAll(
                () -> assertThat(member.getId()).isEqualTo(savedId),
                () -> assertThat(member.getEmail()).isEqualTo("asdf@asdf.com"),
                () -> assertThat(member.getNickname()).isEqualTo("닉네임"),
                () -> assertThat(isMatchedPassword).isTrue()
        );
    }

    @Test
    @DisplayName("유저 정보 수정 성공")
    void update() throws IOException {
        FakeContainer container = new FakeContainer();

//        회원가입
        MemberRegisterRequest request = new MemberRegisterRequest("asdf@asdf.com", "asdf1234!@", "닉네임");
        APIResponse joinResponse = container.memberController.register(request);
        Long savedId = (Long) joinResponse.getBody().get("savedId");

        container.memberController.update(savedId, null, new MemberModificationRequest("asdf1234!@", "qwer1234!@", "바뀐 자기소개입니다."));

        Member member = container.memberRepository.findById(savedId)
                .orElseThrow(() -> new MemberException(MEMBER_NOT_FOUND));

        boolean isMatchedPassword = container.passwordEncoder.matches("qwer1234!@", member.getPassword());

        assertAll(
                () -> assertThat(member.getId()).isEqualTo(savedId),
                () -> assertThat(member.getEmail()).isEqualTo("asdf@asdf.com"),
                () -> assertThat(isMatchedPassword).isTrue(),
                () -> assertThat(member.getNickname()).isEqualTo("닉네임"),
                () -> assertThat(member.getIntroduction()).isEqualTo("바뀐 자기소개입니다.")
        );
    }

    @Test
    @DisplayName("유저 사진 변경 성공")
    void updateMemberPhoto() throws IOException {
        FakeContainer container = new FakeContainer();

//        회원가입
        MemberRegisterRequest request = new MemberRegisterRequest("asdf@asdf.com", "asdf1234!@", "닉네임");
        APIResponse joinResponse = container.memberController.register(request);
        Long savedId = (Long) joinResponse.getBody().get("savedId");

//        유저 사진 변경
        MockMultipartFile multipartFile = new MockMultipartFile("photo.jpeg", "photo.jpeg", "image/jpeg", new byte[0]);
        container.memberController.update(savedId, multipartFile, null);

        MemberPhoto memberPhoto = container.memberPhotoRepository.findById(savedId)
                .orElseThrow(() -> new MemberException(MEMBER_PHOTO_NOT_FOUND));

        Member member = container.memberRepository.findById(savedId)
                .orElseThrow(() -> new MemberException(MEMBER_NOT_FOUND));

        String expectedName = container.uuidHolder.generateUUID() + "[" + container.dateTimeHolder.now() + "]" + multipartFile.getOriginalFilename().substring(multipartFile.getOriginalFilename().indexOf("."));

        assertAll(
                () -> assertThat(memberPhoto.getMemberId()).isEqualTo(savedId),
                () -> assertThat(memberPhoto.getMember()).isEqualTo(member),
                () -> assertThat(memberPhoto.getOriginalName()).isEqualTo(multipartFile.getOriginalFilename()),
                () -> assertThat(memberPhoto.getName()).isEqualTo(expectedName)
        );
    }

    @Test
    @DisplayName("유저 정보와 사진 변경")
    void updateMemberPhotoAndMemberInformation() throws IOException {
        FakeContainer container = new FakeContainer();

//        회원가입
        MemberRegisterRequest request = new MemberRegisterRequest("asdf@asdf.com", "asdf1234!@", "닉네임");
        APIResponse joinResponse = container.memberController.register(request);
        Long savedId = (Long) joinResponse.getBody().get("savedId");

//        유저 정보와 사진 변경
        MockMultipartFile multipartFile = new MockMultipartFile("photo.jpeg", "photo.jpeg", "image/jpeg", new byte[0]);
        MemberModificationRequest modificationRequest = new MemberModificationRequest("asdf1234!@", "qwer1234!@", "바뀐 자기소개입니다.");
        container.memberController.update(savedId, multipartFile, modificationRequest);

        MemberPhoto memberPhoto = container.memberPhotoRepository.findById(savedId)
                .orElseThrow(() -> new MemberException(MEMBER_PHOTO_NOT_FOUND));

        Member member = container.memberRepository.findById(savedId)
                .orElseThrow(() -> new MemberException(MEMBER_NOT_FOUND));

        boolean isMatchedPassword = container.passwordEncoder.matches("qwer1234!@", member.getPassword());
        String expectedName = container.uuidHolder.generateUUID() + "[" + container.dateTimeHolder.now() + "]" + multipartFile.getOriginalFilename().substring(multipartFile.getOriginalFilename().indexOf("."));

        assertAll(
                () -> assertThat(memberPhoto.getMemberId()).isEqualTo(savedId),
                () -> assertThat(memberPhoto.getMember()).isEqualTo(member),
                () -> assertThat(memberPhoto.getOriginalName()).isEqualTo(multipartFile.getOriginalFilename()),
                () -> assertThat(memberPhoto.getName()).isEqualTo(expectedName),
                () -> assertThat(member.getId()).isEqualTo(savedId),
                () -> assertThat(member.getEmail()).isEqualTo("asdf@asdf.com"),
                () -> assertThat(isMatchedPassword).isTrue(),
                () -> assertThat(member.getNickname()).isEqualTo("닉네임"),
                () -> assertThat(member.getIntroduction()).isEqualTo("바뀐 자기소개입니다.")
        );
    }

    @SuppressWarnings("unchecked")
    @Test
    @DisplayName("유저가 가입한 모든 모임 확인")
    void getMoim() throws IOException {
        FakeContainer container = new FakeContainer();

//        회원가입
        MemberRegisterRequest request = new MemberRegisterRequest("asdf@asdf.com", "asdf1234!@", "닉네임");
        Long savedMemberId = container.memberService.register(request);
        Member member = container.memberRepository.findById(savedMemberId)
                .orElseThrow(() -> new MemberException(MEMBER_NOT_FOUND));

//        모임 생성
        MoimTagRequest tagRequest = new MoimTagRequest(List.of("tag1", "tag2", "tag3"));
        MockMultipartFile multipartFile = new MockMultipartFile("photo.jpeg", "photo.jpeg", "image/jpeg", new byte[1]);
        Long moimId = container.moimService.register(
                new MoimRegisterRequest("모임", "모임 설명", "mixed", true),
                tagRequest,
                null,
                multipartFile,
                member.getId());
        Moim moim = container.moimRepository.findById(moimId)
                .orElseThrow(() -> new MemberException(MOIM_NOT_FOUND));

        MoimPhoto moimPhoto = container.moimPhotoRepository.findById(moimId)
                .orElseThrow(() -> new MemberException(MOIM_PHOTO_NOT_FOUND));

        User user = User.mappingUserDetails(member);

        APIResponse response = container.memberController.getMoim(user);
        List<MoimSearchResponse> list = (List<MoimSearchResponse>) response.getBody().get("list");

        assertAll(
                () -> assertThat(list).size().isEqualTo(1),
                () -> assertThat(list).extracting(MoimSearchResponse::getMoimType).containsExactlyInAnyOrder(moim.getType()),
                () -> assertThat(list).extracting(MoimSearchResponse::getId).containsExactlyInAnyOrder(moimId),
                () -> assertThat(list).extracting(MoimSearchResponse::getIntroduction).containsExactlyInAnyOrder(moim.getIntroduction()),
                () -> assertThat(list).extracting(MoimSearchResponse::getName).containsExactlyInAnyOrder(moim.getName()),
                () -> assertThat(list).extracting(MoimSearchResponse::getTagList).containsExactlyInAnyOrder(List.of("tag1", "tag2", "tag3")),
                () -> assertThat(list).extracting(MoimSearchResponse::getPhotoName).containsExactlyInAnyOrder("photo.jpeg")
        );
    }

    @SuppressWarnings("unchecked")
    @Test
    @DisplayName("유저가 지원한 특정 상태의 모든 지원서 확인(지원 상태)")
    void getWaitApplication() throws IOException {
        FakeContainer container = new FakeContainer();

//        회원가입
        MemberRegisterRequest request = new MemberRegisterRequest("asdf@asdf.com", "asdf1234!@", "닉네임");
        Long savedMemberId = container.memberService.register(request);
        Member member = container.memberRepository.findById(savedMemberId)
                .orElseThrow(() -> new MemberException(MEMBER_NOT_FOUND));

//        모임 생성
        MoimTagRequest tagRequest = new MoimTagRequest(List.of("tag1", "tag2", "tag3"));
        MockMultipartFile multipartFile = new MockMultipartFile("photo.jpeg", "photo.jpeg", "image/jpeg", new byte[1]);
        Long moimId = container.moimService.register(
                new MoimRegisterRequest("모임", "모임 설명", "mixed", true),
                tagRequest,
                null,
                multipartFile,
                member.getId());

//        지원하기 위한 유저 생성
        Long applicantId = container.memberService.register(new MemberRegisterRequest("a@a.com", "asdf1234!@", "닉네임2"));

        Member applicant = container.memberRepository.findById(applicantId)
                .orElseThrow(() -> new MemberException(MEMBER_NOT_FOUND));

        User user = User.mappingUserDetails(applicant);

//        지원서 생성
        Long savedApplicationId = container.applicationService.register(moimId, new ApplicationRequest(List.of()), applicantId);

        APIResponse response = container.memberController.getApplication(moimId, user, "wait");
        List<ApplicationResponse> applicationList = (List<ApplicationResponse>) response.getBody().get("applicationList");

        assertAll(
                () -> assertThat(applicationList).extracting(ApplicationResponse::getApplicantName).containsExactlyInAnyOrder(applicant.getNickname()),
                () -> assertThat(applicationList).extracting(ApplicationResponse::getStatus).containsExactlyInAnyOrder(ApplicationStatus.WAIT),
                () -> assertThat(applicationList).extracting(ApplicationResponse::getId).containsExactlyInAnyOrder(savedApplicationId)
        );
    }

    @SuppressWarnings("unchecked")
    @Test
    @DisplayName("유저가 지원한 모든 지원서 확인(지원 상태)")
    void getApplication() throws IOException {
        FakeContainer container = new FakeContainer();

//        회원가입
        MemberRegisterRequest request = new MemberRegisterRequest("asdf@asdf.com", "asdf1234!@", "닉네임");
        Long savedMemberId = container.memberService.register(request);
        Member member = container.memberRepository.findById(savedMemberId)
                .orElseThrow(() -> new MemberException(MEMBER_NOT_FOUND));

//        모임 생성
        MoimTagRequest tagRequest = new MoimTagRequest(List.of("tag1", "tag2", "tag3"));
        MockMultipartFile multipartFile = new MockMultipartFile("photo.jpeg", "photo.jpeg", "image/jpeg", new byte[1]);
        Long moimId = container.moimService.register(
                new MoimRegisterRequest("모임", "모임 설명", "mixed", true),
                tagRequest,
                null,
                multipartFile,
                member.getId());

//        지원하기 위한 유저 생성
        Long applicantId = container.memberService.register(new MemberRegisterRequest("a@a.com", "asdf1234!@", "닉네임2"));

        Member applicant = container.memberRepository.findById(applicantId)
                .orElseThrow(() -> new MemberException(MEMBER_NOT_FOUND));

        User user = User.mappingUserDetails(applicant);

//        지원서 생성
        Long savedApplicationId = container.applicationService.register(moimId, new ApplicationRequest(List.of()), applicantId);

        APIResponse response = container.memberController.getApplication(moimId, user, "all");
        List<ApplicationResponse> applicationList = (List<ApplicationResponse>) response.getBody().get("applicationList");

        assertAll(
                () -> assertThat(applicationList).extracting(ApplicationResponse::getApplicantName).containsExactlyInAnyOrder(applicant.getNickname()),
                () -> assertThat(applicationList).extracting(ApplicationResponse::getStatus).containsExactlyInAnyOrder(ApplicationStatus.WAIT),
                () -> assertThat(applicationList).extracting(ApplicationResponse::getId).containsExactlyInAnyOrder(savedApplicationId)
        );
    }
}