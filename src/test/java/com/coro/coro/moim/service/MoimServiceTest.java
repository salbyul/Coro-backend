package com.coro.coro.moim.service;

import com.coro.coro.application.domain.ApplicationQuestion;
import com.coro.coro.application.domain.ApplicationStatus;
import com.coro.coro.application.dto.request.ApplicationQuestionRegisterRequest;
import com.coro.coro.application.dto.request.ApplicationRequest;
import com.coro.coro.application.dto.response.ApplicationQuestionResponse;
import com.coro.coro.member.domain.Member;
import com.coro.coro.member.domain.MemberRole;
import com.coro.coro.member.dto.request.MemberRegisterRequest;
import com.coro.coro.mock.FakeContainer;
import com.coro.coro.moim.domain.*;
import com.coro.coro.moim.dto.request.*;
import com.coro.coro.moim.dto.response.MoimDetailResponse;
import com.coro.coro.moim.dto.response.MoimMemberResponse;
import com.coro.coro.moim.dto.response.MoimModificationResponse;
import com.coro.coro.moim.dto.response.MoimSearchResponse;
import com.coro.coro.moim.exception.MoimException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.coro.coro.common.response.error.ErrorType.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertAll;

class MoimServiceTest {

    @Test
    @DisplayName("[모임 생성] 정상적인 모임 생성")
    void register() throws IOException {
        FakeContainer container = new FakeContainer();

//        회원가입
        Long savedMemberId = container.memberService.register(new MemberRegisterRequest("asdf@asdf.com", "asdf1234!@", "닉네임"));

//        모임 생성
        MoimRegisterRequest requestMoim = new MoimRegisterRequest("모임", "", "mixed", true);
        Long moimId = container.moimService.register(requestMoim, null, null, null, savedMemberId);

//        검증
        Moim moim = container.moimRepository.findById(moimId).orElseThrow(() -> new MoimException(MOIM_NOT_FOUND));

        assertAll(
                () -> assertThat(moim.getIntroduction()).isEqualTo("우리 모임을 소개해주세요."),
                () -> assertThat(moim.getType()).isEqualTo(MoimType.MIXED),
                () -> assertThat(moim.getVisible()).isTrue()
        );
    }

    @Test
    @DisplayName("[모임 생성] 이름 중복의 경우")
    void registerFailByDuplicateName() throws IOException {
        FakeContainer container = new FakeContainer();

//        회원가입
        Long savedMemberId = container.memberService.register(new MemberRegisterRequest("asdf@asdf.com", "asdf1234!@", "닉네임"));

//        모임 생성
        MoimRegisterRequest requestMoim = new MoimRegisterRequest("모임", "", "mixed", true);
        container.moimService.register(requestMoim, null, null, null, savedMemberId);

//        중복된 이름의 모임 생성
        MoimRegisterRequest duplicatedMoim = new MoimRegisterRequest("모임", "", "mixed", true);

        assertThatThrownBy(() ->
                container.moimService.register(duplicatedMoim, new MoimTagRequest(), null, null, savedMemberId)
        )
                .isInstanceOf(MoimException.class)
                .hasMessage(MOIM_NAME_DUPLICATE.getMessage());
    }

    @Test
    @DisplayName("[모임 생성] 태그 값이 비어있을 경우")
    void registerFailByEmptyTag() {
        FakeContainer container = new FakeContainer();

//        회원가입
        Long savedMemberId = container.memberService.register(new MemberRegisterRequest("asdf@asdf.com", "asdf1234!@", "닉네임"));

//        모임 생성
        MoimTagRequest requestMoimTag = new MoimTagRequest(List.of("tag1", "tag2", ""));
        MoimRegisterRequest requestMoim = new MoimRegisterRequest("모임", "", "mixed", true);

        assertThatThrownBy(() -> container.moimService.register(requestMoim, requestMoimTag, null, null, savedMemberId))
                .isInstanceOf(MoimException.class)
                .hasMessage(MOIM_TAG_NULL.getMessage());
    }

    @Test
    @DisplayName("[모임 생성] 태그 값이 중복될 경우")
    void registerFailByDuplicateTag() {
        FakeContainer container = new FakeContainer();

//        회원가입
        Long savedMemberId = container.memberService.register(new MemberRegisterRequest("asdf@asdf.com", "asdf1234!@", "닉네임"));

//        모임 생성
        MoimRegisterRequest requestMoim = new MoimRegisterRequest("모임", "", "mixed", true);
        MoimTagRequest requestMoimTag = new MoimTagRequest(List.of("tag1", "tag1"));

        assertThatThrownBy(() -> container.moimService.register(requestMoim, requestMoimTag, null, null, savedMemberId))
                .isInstanceOf(MoimException.class)
                .hasMessage(MOIM_TAG_DUPLICATE.getMessage());
    }

    @ParameterizedTest
    @DisplayName("[모임 생성] 태그 값이 유효하지 않을 경우")
    @ValueSource(strings = {"tag12345678", "!@#", "-_a"})
    void registerFailByNotValidTag(final String input) {
        FakeContainer container = new FakeContainer();

//        회원가입
        Long savedMemberId = container.memberService.register(new MemberRegisterRequest("asdf@asdf.com", "asdf1234!@", "닉네임"));

//        모임 생성
        MoimRegisterRequest requestMoim = new MoimRegisterRequest("모임", "", "mixed", true);
        MoimTagRequest requestMoimTag = new MoimTagRequest(List.of(input));

        assertThatThrownBy(() -> container.moimService.register(requestMoim, requestMoimTag, null, null, savedMemberId))
                .isInstanceOf(MoimException.class)
                .hasMessage(MOIM_TAG_NOT_VALID.getMessage());
    }

    @Test
    @DisplayName("[모임 수정] 정상적인 모임 수정")
    void update() throws IOException {
        FakeContainer container = new FakeContainer();

//        회원가입
        Long savedMemberId = container.memberService.register(new MemberRegisterRequest("asdf@asdf.com", "asdf1234!@", "닉네임"));

//        모임 생성
        MoimTagRequest requestMoimTag = new MoimTagRequest(List.of("tag1", "tag2", "tag3"));
        MoimRegisterRequest requestMoim = new MoimRegisterRequest("모임", "", "mixed", true);
        List<ApplicationQuestionRegisterRequest> requestQuestions = List.of(
                new ApplicationQuestionRegisterRequest("question1", 1),
                new ApplicationQuestionRegisterRequest("question2", 2)
        );
        Long savedMoimId = container.moimService.register(requestMoim, requestMoimTag, requestQuestions, null, savedMemberId);

//        모임 수정
        MoimTagRequest changedTagList = new MoimTagRequest(List.of("변경된태그1", "변경된태그2", "변경된태그3"));
        List<ApplicationQuestionRegisterRequest> changedQuestionList = List.of(
                new ApplicationQuestionRegisterRequest("changed question1", 1),
                new ApplicationQuestionRegisterRequest("changed question2", 2)
        );
        MockMultipartFile mockMultipartFile = new MockMultipartFile("photo.jpeg", "photo.jpeg", "image/jpeg", new byte[1]);

        container.moimService.update(
                savedMoimId,
                new MoimModificationRequest("모임", "수정되었습니다.", "faceToFace", false, false),
                changedTagList,
                mockMultipartFile,
                changedQuestionList,
                savedMemberId
        );

//        검증
        Moim moim = container.moimRepository.findById(savedMoimId)
                .orElseThrow(() -> new MoimException(MOIM_NOT_FOUND));
        MoimPhoto moimPhoto = container.moimPhotoRepository.findById(savedMoimId)
                .orElseThrow(() -> new MoimException(MOIM_PHOTO_NOT_FOUND));

        assertAll(
                () -> assertThat(moim.getName()).isEqualTo("모임"),
                () -> assertThat(moim.getIntroduction()).isEqualTo("수정되었습니다."),
                () -> assertThat(moim.getType()).isEqualTo(MoimType.FACE_TO_FACE),
                () -> assertThat(moim.getVisible()).isFalse(),
                () -> assertThat(moim.getTagList()).extracting(MoimTag::getName).containsExactlyInAnyOrder("변경된태그1", "변경된태그2", "변경된태그3"),
                () -> assertThat(moim.getQuestionList()).extracting(ApplicationQuestion::getContent).containsExactlyInAnyOrder("changed question1", "changed question2"),
                () -> assertThat(moimPhoto.getOriginalName()).isEqualTo("photo.jpeg")
        );
    }

    @Test
    @DisplayName("[모임 수정] 존재하지 않는 모임")
    void updateFailByNotExistId() {
        FakeContainer container = new FakeContainer();

//        회원가입
        Long savedMemberId = container.memberService.register(new MemberRegisterRequest("asdf@asdf.com", "asdf1234!@", "닉네임"));

//        검증
        assertThatThrownBy(() -> container.moimService.update(
                99999L,
                new MoimModificationRequest("수정", "소개 수정", "mixed", true, false),
                null,
                null,
                null,
                savedMemberId)
        )
                .isInstanceOf(MoimException.class)
                .hasMessage(MOIM_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("[모임 수정] 이름 중복의 경우")
    void updateFailByDuplicateName() throws IOException {
        FakeContainer container = new FakeContainer();

//        회원가입
        Long savedMemberId = container.memberService.register(new MemberRegisterRequest("asdf@asdf.com", "asdf1234!@", "닉네임"));

//        모임 생성
        MoimRegisterRequest requestMoim = new MoimRegisterRequest("모임 예", "모임 소개", "mixed", true);
        Long moimId = container.moimService.register(requestMoim, null, null, null, savedMemberId);

        MoimRegisterRequest target = new MoimRegisterRequest("모임", "", "mixed", true);
        container.moimService.register(target, null, null, null, savedMemberId);

//        검증
        assertThatThrownBy(() -> container.moimService.update(
                moimId,
                new MoimModificationRequest("모임", "모임 소개", "mixed", true, false),
                new MoimTagRequest(),
                null,
                null,
                savedMemberId)
        )
                .isInstanceOf(MoimException.class)
                .hasMessage(MOIM_NAME_DUPLICATE.getMessage());
    }

    @Test
    @DisplayName("[모임 수정] 이미지 파일이 아닌 경우")
    void updateFailByNotImageFile() throws IOException {
        FakeContainer container = new FakeContainer();

//        회원가입
        Long savedMemberId = container.memberService.register(new MemberRegisterRequest("asdf@asdf.com", "asdf1234!@", "닉네임"));

//        모임 생성
        MoimRegisterRequest requestMoim = new MoimRegisterRequest("모임", "모임 설명", "mixed", true);
        Long moimId = container.moimService.register(requestMoim, null, null, null, savedMemberId);

//        검증
        MoimModificationRequest moimModificationRequest = new MoimModificationRequest("모임", "모임 소개", "mixed", true, false);
        assertThatThrownBy(() -> container.moimService.update(
                moimId,
                moimModificationRequest,
                null,
                new MockMultipartFile("spring.js", "spring.js", "text/javascript", new byte[1]),
                null,
                savedMemberId)
        )
                .isInstanceOf(MoimException.class)
                .hasMessage(MOIM_PHOTO_NOT_VALID.getMessage());

        assertThatThrownBy(() -> container.moimService.update(
                moimId,
                moimModificationRequest,
                null,
                new MockMultipartFile("spring", "spring", "image/jpeg", new byte[1]),
                null,
                savedMemberId)
        )
                .isInstanceOf(MoimException.class)
                .hasMessage(MOIM_PHOTO_NOT_VALID.getMessage());
    }

    @Test
    @DisplayName("모임 디테일 정보 획득 성공")
    void getDetail() throws IOException {
        FakeContainer container = new FakeContainer();

//        회원가입
        Long savedMemberId = container.memberService.register(new MemberRegisterRequest("asdf@asdf.com", "asdf1234!@", "닉네임"));

//        모임 생성
        MoimTagRequest requestMoimTag = new MoimTagRequest(List.of("tag1", "tag2", "tag3"));
        MoimRegisterRequest requestMoim = new MoimRegisterRequest("모임", "모임 설명", "faceToFace", true);
        Long savedMoimId = container.moimService.register(requestMoim, requestMoimTag, null, null, savedMemberId);

//        디테일 정보 가져오기
        MoimDetailResponse detail = container.moimService.getDetail(savedMoimId, savedMemberId);

//        검증
        assertAll(
                () -> assertThat(detail.getName()).isEqualTo("모임"),
                () -> assertThat(detail.getIntroduction()).isEqualTo("모임 설명"),
                () -> assertThat(detail.isJoined()).isTrue(),
                () -> assertThat(detail.isCanManage()).isTrue(),
                () -> assertThat(detail.getTagList()).size().isEqualTo(3),
                () -> assertThat(detail.getTagList()).containsExactlyInAnyOrder("tag1", "tag2", "tag3")
        );
    }

    @Test
    @DisplayName("모임 디테일 정보 획득실패 - 올바르지 않는 모임 Id값")
    void getDetailFailByNotValidMoimId() throws IOException {
        FakeContainer container = new FakeContainer();

//        회원가입
        Long savedMemberId = container.memberService.register(new MemberRegisterRequest("asdf@asdf.com", "asdf1234!@", "닉네임"));

//        모임 생성
        MoimTagRequest requestMoimTag = new MoimTagRequest(List.of("tag1", "tag2", "tag3"));
        MoimRegisterRequest requestMoim = new MoimRegisterRequest("모임", "모임 설명", "faceToFace", true);
        container.moimService.register(requestMoim, requestMoimTag, null, null, savedMemberId);

//        검증
        assertThatThrownBy(() -> container.moimService.getDetail(19999999L, 1L))
                .isInstanceOf(MoimException.class)
                .hasMessage(MOIM_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("모임 수정을 위한 데이터 획득 성공")
    void getDetailForModification() throws IOException {
        FakeContainer container = new FakeContainer();

//        회원가입
        Long savedMemberId = container.memberService.register(new MemberRegisterRequest("asdf@asdf.com", "asdf1234!@", "닉네임"));

//        모임 생성
        MoimTagRequest requestMoimTag = new MoimTagRequest(List.of("tag1", "tag2", "tag3"));
        List<ApplicationQuestionRegisterRequest> requestQuestions = List.of(
                new ApplicationQuestionRegisterRequest("질문1", 1),
                new ApplicationQuestionRegisterRequest("질문2", 2)
        );
        MoimRegisterRequest requestMoim = new MoimRegisterRequest("모임", "모임 설명", "mixed", true);
        Long savedMoimId = container.moimService.register(requestMoim, requestMoimTag, requestQuestions, null, savedMemberId);

//        모임 수정을 위한 데이터 가져오기
        MoimModificationResponse result = container.moimService.getDetailForModification(savedMoimId, savedMemberId);

//        검증
        assertAll(
                () -> assertThat(result.getName()).isEqualTo("모임"),
                () -> assertThat(result.getIntroduction()).isEqualTo("모임 설명"),
                () -> assertThat(result.isVisible()).isTrue(),
                () -> assertThat(result.getType()).isEqualTo(MoimType.MIXED.toString()),
                () -> assertThat(result.getTagList()).size().isEqualTo(3),
                () -> assertThat(result.getTagList()).containsExactlyInAnyOrder("tag1", "tag2", "tag3"),
                () -> assertThat(result.getApplicationQuestionList()).size().isEqualTo(2),
                () -> assertThat(result.getApplicationQuestionList()).extracting(ApplicationQuestionResponse::getContent).containsExactlyInAnyOrder("질문1", "질문2")
        );
    }

    @Test
    @DisplayName("모임 수정을 위한 데이터 획득 실패 - 올바르지 않는 모임 Id 값")
    void getDetailForModificationFailByNotValidMoimId() throws IOException {
        FakeContainer container = new FakeContainer();

//        회원가입
        Long savedMemberId = container.memberService.register(new MemberRegisterRequest("asdf@asdf.com", "asdf1234!@", "닉네임"));

//        모임 생성
        MoimRegisterRequest requestMoim = new MoimRegisterRequest("모임", "모임 설명", "faceToFace", true);
        container.moimService.register(requestMoim, null, null, null, savedMemberId);

//        검증
        assertThatThrownBy(() -> container.moimService.getDetailForModification(9999999L, savedMemberId))
                .isInstanceOf(MoimException.class)
                .hasMessage(MOIM_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("모임 수정을 위한 데이터 획득 실패 - 권한이 없는 회원의 경우")
    void getDetailForModificationFailByForbiddenMember() throws IOException {
        FakeContainer container = new FakeContainer();

//        회원가입
        Long leader = container.memberService.register(new MemberRegisterRequest("asdf@asdf.com", "asdf1234!@", "닉네임"));

//        모임 생성
        MoimRegisterRequest requestMoim = new MoimRegisterRequest("모임", "모임 설명", "faceToFace", true);
        Long moimId = container.moimService.register(requestMoim, null, null, null, leader);

//        새로운 회원의 모임 가입
        Long savedMemberId = container.memberService.register(new MemberRegisterRequest("a@a.com", "asdf1234!@", "닉네임2"));
        Moim moim = container.moimRepository.findById(moimId)
                .orElseThrow(() -> new MoimException(MOIM_NOT_FOUND));
        Member member = container.memberRepository.findById(savedMemberId)
                .orElseThrow(() -> new MoimException(MEMBER_NOT_FOUND));
        MoimMember moimMember = MoimMember.builder()
                .member(member)
                .moim(moim)
                .role(MemberRole.USER)
                .build();
        container.moimMemberRepository.save(moimMember);

//        검증
        assertThatThrownBy(() -> container.moimService.getDetailForModification(moimId, savedMemberId))
                .isInstanceOf(MoimException.class)
                .hasMessage(MOIM_FORBIDDEN.getMessage());
    }

    @Test
    @DisplayName("가입된 모든 모임 획득 성공")
    void getMoimListByMemberId() throws IOException {
        FakeContainer container = new FakeContainer();

//        회원가입
        Long savedMemberId = container.memberService.register(new MemberRegisterRequest("asdf@asdf.com", "asdf1234!@", "닉네임"));

//        모임 생성
        MoimRegisterRequest requestMoim = new MoimRegisterRequest("모임", "모임 설명", "mixed", true);
        container.moimService.register(requestMoim, null, null, null, savedMemberId);

        container.moimService.register(
                new MoimRegisterRequest("모임2", "모임 설명2", "faceToFace", true),
                null,
                null,
                null,
                savedMemberId
        );

//        가입된 모든 모임 가져오기
        List<Moim> moimList = container.moimService.getMoimListByMemberId(savedMemberId);

//        검증
        assertAll(
                () -> assertThat(moimList).size().isEqualTo(2),
                () -> assertThat(moimList).extracting(Moim::getName)
                        .containsExactlyInAnyOrder("모임", "모임2"),
                () -> assertThat(moimList).extracting(Moim::getIntroduction)
                        .containsExactlyInAnyOrder("모임 설명", "모임 설명2"),
                () -> assertThat(moimList).extracting(Moim::getVisible)
                        .containsExactlyInAnyOrder(true, true),
                () -> assertThat(moimList).extracting(Moim::getType)
                        .containsExactlyInAnyOrder(MoimType.MIXED, MoimType.FACE_TO_FACE),
                () -> assertThat(moimList).extracting(Moim::getState)
                        .containsExactlyInAnyOrder(MoimState.ACTIVE, MoimState.ACTIVE)
        );
    }

    @Test
    @DisplayName("모든 모임 멤버 획득 성공")
    void getMoimMemberList() throws IOException {
        FakeContainer container = new FakeContainer();

//        회원가입
        Long savedMemberId = container.memberService.register(new MemberRegisterRequest("asdf@asdf.com", "asdf1234!@", "닉네임"));
        Long savedMemberId2 = container.memberService.register(new MemberRegisterRequest("a@a.com", "asdf1234!@", "닉네임2"));

//        모임 생성
        MoimRegisterRequest requestMoim = new MoimRegisterRequest("모임", "모임 설명", "mixed", true);
        Long savedMoimId = container.moimService.register(requestMoim, null, null, null, savedMemberId);

//        모임 지원
        Long applicationId = container.applicationService.register(savedMoimId, new ApplicationRequest(new ArrayList<>()), savedMemberId2);
        container.applicationService.decideApplication(savedMemberId, applicationId, ApplicationStatus.ACCEPT);

//        특정 모임의 모든 회원 가져오기
        List<MoimMemberResponse> moimMemberList = container.moimService.getMoimMemberList(savedMoimId);

//        검증
        assertAll(
                () -> assertThat(moimMemberList).size().isEqualTo(2),
                () -> assertThat(moimMemberList).extracting(MoimMemberResponse::getMemberName).containsExactlyInAnyOrder("닉네임", "닉네임2"),
                () -> assertThat(moimMemberList).extracting(MoimMemberResponse::getRole).containsExactlyInAnyOrder(MemberRole.LEADER, MemberRole.USER)
        );
    }

    @Test
    @DisplayName("모임 멤버 수정 성공")
    void modifyMoimMember() throws IOException {
        FakeContainer container = new FakeContainer();

//        회원가입
        Long savedMemberId = container.memberService.register(new MemberRegisterRequest("asdf@asdf.com", "asdf1234!@", "닉네임"));
        Long savedMemberId2 = container.memberService.register(new MemberRegisterRequest("a@a.com", "asdf1234!@", "닉네임2"));

//        모임 생성
        MoimRegisterRequest requestMoim = new MoimRegisterRequest("모임", "모임 설명", "mixed", true);
        Long savedMoimId = container.moimService.register(requestMoim, null, null, null, savedMemberId);

//        모임 지원
        Long applicationId = container.applicationService.register(savedMoimId, new ApplicationRequest(new ArrayList<>()), savedMemberId2);
        container.applicationService.decideApplication(savedMemberId, applicationId, ApplicationStatus.ACCEPT);

//        모임 멤버 수정
        MoimMember moimMember1 = container.moimMemberRepository.findByMoimIdAndMemberId(savedMoimId, savedMemberId)
                .orElseThrow(() -> new MoimException(MOIM_MEMBER_NOT_FOUND));
        MoimMember moimMember2 = container.moimMemberRepository.findByMoimIdAndMemberId(savedMoimId, savedMemberId2)
                .orElseThrow(() -> new MoimException(MOIM_MEMBER_NOT_FOUND));

        container.moimService.modifyMoimMember(
                savedMoimId,
                List.of(
                        new MoimMemberModificationRequest(moimMember1.getId(), "닉네임", MemberRole.MANAGER),
                        new MoimMemberModificationRequest(moimMember2.getId(), "닉네임2", MemberRole.LEADER)),
                savedMemberId
        );

//        검증
        assertAll(
                () -> assertThat(moimMember1.getMember().getNickname()).isEqualTo("닉네임"),
                () -> assertThat(moimMember1.getMoim().getName()).isEqualTo("모임"),
                () -> assertThat(moimMember1.getRole()).isEqualTo(MemberRole.MANAGER),
                () -> assertThat(moimMember2.getMember().getNickname()).isEqualTo("닉네임2"),
                () -> assertThat(moimMember2.getMoim().getName()).isEqualTo("모임"),
                () -> assertThat(moimMember2.getRole()).isEqualTo(MemberRole.LEADER)
        );
    }

    @Test
    @DisplayName("모임 멤버 수정 실패 - 리더 중복")
    void modifyMoimMemberFailByDuplicateLeader() throws IOException {
        FakeContainer container = new FakeContainer();

//        회원가입
        Long leaderId = container.memberService.register(new MemberRegisterRequest("asdf@asdf.com", "asdf1234!@", "닉네임"));
        Long savedMemberId = container.memberService.register(new MemberRegisterRequest("a@a.com", "asdf1234!@", "닉네임2"));

//        모임 생성
        MoimRegisterRequest requestMoim = new MoimRegisterRequest("모임", "모임 설명", "mixed", true);
        Long savedMoimId = container.moimService.register(requestMoim, null, null, null, leaderId);

//        모임 지원
        Long applicationId = container.applicationService.register(savedMoimId, new ApplicationRequest(new ArrayList<>()), savedMemberId);
        container.applicationService.decideApplication(leaderId, applicationId, ApplicationStatus.ACCEPT);

//        검증
        MoimMember leaderMoimMember = container.moimMemberRepository.findByMoimIdAndMemberId(savedMoimId, leaderId)
                .orElseThrow(() -> new MoimException(MOIM_MEMBER_NOT_FOUND));
        MoimMember moimMember = container.moimMemberRepository.findByMoimIdAndMemberId(savedMoimId, savedMemberId)
                .orElseThrow(() -> new MoimException(MOIM_MEMBER_NOT_FOUND));

        List<MoimMemberModificationRequest> moimMemberModificationRequestList = List.of(new MoimMemberModificationRequest(leaderMoimMember.getId(),
                        "닉네임",
                        MemberRole.LEADER),
                new MoimMemberModificationRequest(moimMember.getId(),
                        "닉네임2",
                        MemberRole.LEADER)
        );
        assertThatThrownBy(() ->
                container.moimService.modifyMoimMember(savedMoimId, moimMemberModificationRequestList, leaderId)
        )
                .isInstanceOf(MoimException.class)
                .hasMessage(MOIM_MEMBER_NOT_VALID.getMessage());
    }

    @Test
    @DisplayName("모임 멤버 수정 실패 - 리더가 존재하지 않을 경우")
    void modifyMoimMemberFailByNoLeader() throws IOException {
        FakeContainer container = new FakeContainer();

//        회원가입
        Long leaderId = container.memberService.register(new MemberRegisterRequest("asdf@asdf.com", "asdf1234!@", "닉네임"));
        Long savedMemberId = container.memberService.register(new MemberRegisterRequest("a@a.com", "asdf1234!@", "닉네임2"));

//        모임 생성
        MoimRegisterRequest requestMoim = new MoimRegisterRequest("모임", "모임 설명", "mixed", true);
        Long savedMoimId = container.moimService.register(requestMoim, null, null, null, leaderId);

//        모임 지원
        Long applicationId = container.applicationService.register(savedMoimId, new ApplicationRequest(new ArrayList<>()), savedMemberId);
        container.applicationService.decideApplication(leaderId, applicationId, ApplicationStatus.ACCEPT);

//        검증
        MoimMember leaderMoimMember = container.moimMemberRepository.findByMoimIdAndMemberId(savedMoimId, leaderId)
                .orElseThrow(() -> new MoimException(MOIM_MEMBER_NOT_FOUND));
        MoimMember moimMember = container.moimMemberRepository.findByMoimIdAndMemberId(savedMoimId, savedMemberId)
                .orElseThrow(() -> new MoimException(MOIM_MEMBER_NOT_FOUND));

        List<MoimMemberModificationRequest> moimMemberModificationRequestList = List.of(
                new MoimMemberModificationRequest(leaderMoimMember.getId(), "닉네임", MemberRole.MANAGER),
                new MoimMemberModificationRequest(moimMember.getId(), "닉네임2", MemberRole.MANAGER)
        );
        assertThatThrownBy(() ->
                container.moimService.modifyMoimMember(
                        savedMoimId,
                        moimMemberModificationRequestList, leaderId)
        )
                .isInstanceOf(MoimException.class)
                .hasMessage(MOIM_MEMBER_NOT_VALID.getMessage());
    }

    @Test
    @DisplayName("모임 멤버 수정 실패 - 모든 모임 멤버가 아닌 경우")
    void modifyMoimMemberFailByNotAllMoimMember() throws IOException {
        FakeContainer container = new FakeContainer();

//        회원가입
        Long leaderId = container.memberService.register(new MemberRegisterRequest("asdf@asdf.com", "asdf1234!@", "닉네임"));
        Long savedMemberId = container.memberService.register(new MemberRegisterRequest("a@a.com", "asdf1234!@", "닉네임2"));

//        모임 생성
        MoimRegisterRequest requestMoim = new MoimRegisterRequest("모임", "모임 설명", "mixed", true);
        Long savedMoimId = container.moimService.register(requestMoim, null, null, null, leaderId);

//        모임 지원
        Long applicationId = container.applicationService.register(savedMoimId, new ApplicationRequest(new ArrayList<>()), savedMemberId);
        container.applicationService.decideApplication(leaderId, applicationId, ApplicationStatus.ACCEPT);

//        검증
        container.moimMemberRepository.findByMoimIdAndMemberId(savedMoimId, leaderId)
                .orElseThrow(() -> new MoimException(MOIM_MEMBER_NOT_FOUND));
        MoimMember moimMember = container.moimMemberRepository.findByMoimIdAndMemberId(savedMoimId, savedMemberId)
                .orElseThrow(() -> new MoimException(MOIM_MEMBER_NOT_FOUND));

        List<MoimMemberModificationRequest> moimMemberModificationRequestList = List.of(
                new MoimMemberModificationRequest(moimMember.getId(), "닉네임2", MemberRole.LEADER)
        );

        assertThatThrownBy(() ->
                container.moimService.modifyMoimMember(savedMoimId, moimMemberModificationRequestList, leaderId)
        )
                .isInstanceOf(MoimException.class)
                .hasMessage(MOIM_MEMBER_NOT_VALID.getMessage());
    }

    @Test
    @DisplayName("모임 멤버 수정 실패 - 올바르지 않은 모임 멤버의 경우")
    void modifyMoimMemberFailByNotValidMember() throws IOException {
        FakeContainer container = new FakeContainer();

//        회원가입
        Long leaderId = container.memberService.register(new MemberRegisterRequest("asdf@asdf.com", "asdf1234!@", "닉네임"));
        Long savedMemberId = container.memberService.register(new MemberRegisterRequest("a@a.com", "asdf1234!@", "닉네임2"));

//        모임 생성
        MoimRegisterRequest requestMoim = new MoimRegisterRequest("모임", "모임 설명", "mixed", true);
        Long savedMoimId = container.moimService.register(requestMoim, null, null, null, leaderId);

//        모임 지원
        Long applicationId = container.applicationService.register(savedMoimId, new ApplicationRequest(new ArrayList<>()), savedMemberId);
        container.applicationService.decideApplication(leaderId, applicationId, ApplicationStatus.ACCEPT);

//        검증
        container.moimMemberRepository.findByMoimIdAndMemberId(savedMoimId, leaderId)
                .orElseThrow(() -> new MoimException(MOIM_MEMBER_NOT_FOUND));
        MoimMember moimMember = container.moimMemberRepository.findByMoimIdAndMemberId(savedMoimId, savedMemberId)
                .orElseThrow(() -> new MoimException(MOIM_MEMBER_NOT_FOUND));

        List<MoimMemberModificationRequest> moimMemberModificationRequestList = List.of(
                new MoimMemberModificationRequest(123L, "unknown", MemberRole.MANAGER),
                new MoimMemberModificationRequest(moimMember.getId(), "닉네임2", MemberRole.LEADER)
        );

        assertThatThrownBy(() ->
                container.moimService.modifyMoimMember(savedMoimId, moimMemberModificationRequestList, leaderId)
        )
                .isInstanceOf(MoimException.class)
                .hasMessage(MOIM_MEMBER_NOT_VALID.getMessage());
    }

    @Test
    @DisplayName("모임 멤버 수정 실패 - 올바르지 않은 모임 멤버의 경우2")
    void modifyMoimMemberFailByNotValidMoimMember() throws IOException {
        FakeContainer container = new FakeContainer();

//        회원가입
        Long leaderId = container.memberService.register(new MemberRegisterRequest("asdf@asdf.com", "asdf1234!@", "닉네임"));
        Long savedMemberId = container.memberService.register(new MemberRegisterRequest("a@a.com", "asdf1234!@", "닉네임2"));

//        모임 생성
        MoimRegisterRequest requestMoim = new MoimRegisterRequest("모임", "모임 설명", "mixed", true);
        Long savedMoimId = container.moimService.register(requestMoim, null, null, null, leaderId);

//        모임 지원
        Long applicationId = container.applicationService.register(savedMoimId, new ApplicationRequest(new ArrayList<>()), savedMemberId);
        container.applicationService.decideApplication(leaderId, applicationId, ApplicationStatus.ACCEPT);

//        검증
        MoimMember leaderMoimMember = container.moimMemberRepository.findByMoimIdAndMemberId(savedMoimId, leaderId)
                .orElseThrow(() -> new MoimException(MOIM_MEMBER_NOT_FOUND));
        container.moimMemberRepository.findByMoimIdAndMemberId(savedMoimId, savedMemberId)
                .orElseThrow(() -> new MoimException(MOIM_MEMBER_NOT_FOUND));

        List<MoimMemberModificationRequest> moimMemberModificationRequestList = List.of(
                new MoimMemberModificationRequest(leaderMoimMember.getId(), "회원2", MemberRole.LEADER),
                new MoimMemberModificationRequest(99999L, "asdf", MemberRole.USER)
        );

        assertThatThrownBy(() ->
                container.moimService.modifyMoimMember(savedMoimId, moimMemberModificationRequestList, leaderId)
        )
                .isInstanceOf(MoimException.class)
                .hasMessage(MOIM_MEMBER_NOT_VALID.getMessage());
    }

    @Test
    @DisplayName("모임 멤버 수정 실패 - 관리할 수 없는 역할인 경우")
    void modifyMoimMemberFailByCanNotManage() throws IOException {
        FakeContainer container = new FakeContainer();

//        회원가입
        Long leaderId = container.memberService.register(new MemberRegisterRequest("asdf@asdf.com", "asdf1234!@", "닉네임"));
        Long savedMemberId = container.memberService.register(new MemberRegisterRequest("a@a.com", "asdf1234!@", "닉네임2"));

//        모임 생성
        MoimRegisterRequest requestMoim = new MoimRegisterRequest("모임", "모임 설명", "mixed", true);
        Long savedMoimId = container.moimService.register(requestMoim, null, null, null, leaderId);

//        모임 지원
        Long applicationId = container.applicationService.register(savedMoimId, new ApplicationRequest(new ArrayList<>()), savedMemberId);
        container.applicationService.decideApplication(leaderId, applicationId, ApplicationStatus.ACCEPT);

//        검증
        container.moimMemberRepository.findByMoimIdAndMemberId(savedMoimId, leaderId)
                .orElseThrow(() -> new MoimException(MOIM_MEMBER_NOT_FOUND));
        container.moimMemberRepository.findByMoimIdAndMemberId(savedMoimId, savedMemberId)
                .orElseThrow(() -> new MoimException(MOIM_MEMBER_NOT_FOUND));

        List<MoimMemberModificationRequest> moimMemberModificationRequestList = List.of(
                new MoimMemberModificationRequest(leaderId, "닉네임", MemberRole.USER),
                new MoimMemberModificationRequest(savedMemberId, "닉네임2", MemberRole.LEADER)
        );

        assertThatThrownBy(() ->
                container.moimService.modifyMoimMember(savedMoimId, moimMemberModificationRequestList, savedMemberId)
        )
                .isInstanceOf(MoimException.class)
                .hasMessage(MOIM_FORBIDDEN.getMessage());
    }

    @Test
    @DisplayName("멤버의 등급 획득 성공")
    void getMemberRole() throws IOException {
        FakeContainer container = new FakeContainer();

//        회원가입
        Long leaderId = container.memberService.register(new MemberRegisterRequest("asdf@asdf.com", "asdf1234!@", "닉네임"));

//        모임 생성
        MoimRegisterRequest requestMoim = new MoimRegisterRequest("모임", "모임 설명", "mixed", true);
        Long savedMoimId = container.moimService.register(requestMoim, null, null, null, leaderId);

//        회원 등급 가져오기
        MemberRole memberRole = container.moimService.getMemberRole(leaderId, savedMoimId);

//        검증
        assertThat(memberRole.isLeader()).isTrue();
        assertThat(memberRole.isNotLeader()).isFalse();
    }

    @Test
    @DisplayName("멤버의 등급 획득 실패 - 올바르지 않은 회원 Id")
    void getMemberRoleFailByNotValidMemberId() throws IOException {
        FakeContainer container = new FakeContainer();

//        회원가입
        Long leaderId = container.memberService.register(new MemberRegisterRequest("asdf@asdf.com", "asdf1234!@", "닉네임"));

//        모임 생성
        MoimRegisterRequest requestMoim = new MoimRegisterRequest("모임", "모임 설명", "mixed", true);
        Long savedMoimId = container.moimService.register(requestMoim, null, null, null, leaderId);

//        검증
        assertThatThrownBy(() ->
                container.moimService.getMemberRole(999999L, savedMoimId)
        )
                .isInstanceOf(MoimException.class)
                .hasMessage(MOIM_MEMBER_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("멤버의 등급 획득 실패 - 올바르지 않은 모임 Id")
    void getMemberRoleFailByNotValidMoimId() throws IOException {
        FakeContainer container = new FakeContainer();

//        회원가입
        Long leaderId = container.memberService.register(new MemberRegisterRequest("asdf@asdf.com", "asdf1234!@", "닉네임"));

//        모임 생성
        MoimRegisterRequest requestMoim = new MoimRegisterRequest("모임", "모임 설명", "mixed", true);
        container.moimService.register(requestMoim, null, null, null, leaderId);

//        검증
        assertThatThrownBy(() ->
                container.moimService.getMemberRole(leaderId, 9999999L)
        )
                .isInstanceOf(MoimException.class)
                .hasMessage(MOIM_MEMBER_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("정상적인 회원 추방")
    void deport() throws IOException {
        FakeContainer container = new FakeContainer();

//        회원가입
        Long leaderId = container.memberService.register(new MemberRegisterRequest("asdf@asdf.com", "asdf1234!@", "닉네임"));
        Long savedMemberId = container.memberService.register(new MemberRegisterRequest("a@a.com", "asdf1234!@", "닉네임2"));

//        모임 생성
        MoimRegisterRequest requestMoim = new MoimRegisterRequest("모임", "모임 설명", "mixed", true);
        Long savedMoimId = container.moimService.register(requestMoim, null, null, null, leaderId);

//        모임 지원
        Long applicationId = container.applicationService.register(savedMoimId, new ApplicationRequest(new ArrayList<>()), savedMemberId);
        container.applicationService.decideApplication(leaderId, applicationId, ApplicationStatus.ACCEPT);

        MoimMember moimMember = container.moimMemberRepository.findByMoimIdAndMemberId(savedMoimId, savedMemberId)
                .orElseThrow(() -> new MoimException(MOIM_MEMBER_NOT_FOUND));

//        회원 추방
        container.moimService.deport(savedMoimId, moimMember.getId(), leaderId);

//        검증
        List<MoimMember> moimMemberList = container.moimMemberRepository.findAllByMoimId(savedMoimId);

        assertThat(moimMemberList).size().isEqualTo(1);
        assertThat(moimMemberList)
                .extracting(MoimMember::getMember)
                .extracting(Member::getNickname)
                .containsExactlyInAnyOrder("닉네임");
    }

    @Test
    @DisplayName("회원 추방 실패 - 올바르지 않은 모임 Id")
    void deportFailByNotValidMoimId() throws IOException {
        FakeContainer container = new FakeContainer();

//        회원가입
        Long leader = container.memberService.register(new MemberRegisterRequest("asdf@asdf.com", "asdf1234!@", "닉네임"));
        Long savedMemberId = container.memberService.register(new MemberRegisterRequest("a@a.com", "asdf1234!@", "닉네임2"));

//        모임 생성
        MoimRegisterRequest requestMoim = new MoimRegisterRequest("모임", "모임 설명", "mixed", true);
        Long savedMoimId = container.moimService.register(requestMoim, null, null, null, leader);

//        모임 지원
        Long applicationId = container.applicationService.register(savedMoimId, new ApplicationRequest(new ArrayList<>()), savedMemberId);
        container.applicationService.decideApplication(leader, applicationId, ApplicationStatus.ACCEPT);

//        검증
        MoimMember moimMember = container.moimMemberRepository.findByMoimIdAndMemberId(savedMoimId, savedMemberId)
                .orElseThrow(() -> new MoimException(MOIM_MEMBER_NOT_FOUND));

        assertThatThrownBy(() ->
                container.moimService.deport(999999L, moimMember.getId(), leader)
        )
                .isInstanceOf(MoimException.class)
                .hasMessage(MOIM_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("회원 추방 실패 - 권한이 없을 경우")
    void deportFailByForbidden() throws IOException {
        FakeContainer container = new FakeContainer();

//        회원가입
        Long leaderId = container.memberService.register(new MemberRegisterRequest("asdf@asdf.com", "asdf1234!@", "닉네임"));
        Long savedMemberId = container.memberService.register(new MemberRegisterRequest("a@a.com", "asdf1234!@", "닉네임2"));

//        모임 생성
        MoimRegisterRequest requestMoim = new MoimRegisterRequest("모임", "모임 설명", "mixed", true);
        Long savedMoimId = container.moimService.register(requestMoim, null, null, null, leaderId);

//        모임 지원
        Long applicationId = container.applicationService.register(savedMoimId, new ApplicationRequest(new ArrayList<>()), savedMemberId);
        container.applicationService.decideApplication(leaderId, applicationId, ApplicationStatus.ACCEPT);

//        검증
        MoimMember moimMember = container.moimMemberRepository.findByMoimIdAndMemberId(savedMoimId, savedMemberId)
                .orElseThrow(() -> new MoimException(MOIM_MEMBER_NOT_FOUND));

        assertThatThrownBy(() ->
                container.moimService.deport(savedMoimId, moimMember.getId(), savedMemberId)
        )
                .isInstanceOf(MoimException.class)
                .hasMessage(MOIM_FORBIDDEN.getMessage());
    }

    @Test
    @DisplayName("회원 추방 실패 - 리더 추방의 경우")
    void deportFailByDeportLeader() throws IOException {
        FakeContainer container = new FakeContainer();

//        회원가입
        Long leaderId = container.memberService.register(new MemberRegisterRequest("asdf@asdf.com", "asdf1234!@", "닉네임"));
        Long savedMemberId = container.memberService.register(new MemberRegisterRequest("a@a.com", "asdf1234!@", "닉네임2"));

//        모임 생성
        MoimRegisterRequest requestMoim = new MoimRegisterRequest("모임", "모임 설명", "mixed", true);
        Long savedMoimId = container.moimService.register(requestMoim, null, null, null, leaderId);

//        모임 지원
        Long applicationId = container.applicationService.register(savedMoimId, new ApplicationRequest(new ArrayList<>()), savedMemberId);
        container.applicationService.decideApplication(leaderId, applicationId, ApplicationStatus.ACCEPT);

//        검증
        MoimMember moimMember = container.moimMemberRepository.findByMoimIdAndMemberId(savedMoimId, leaderId)
                .orElseThrow(() -> new MoimException(MOIM_MEMBER_NOT_FOUND));

        assertThatThrownBy(() ->
                container.moimService.deport(savedMoimId, moimMember.getId(), leaderId)
        )
                .isInstanceOf(MoimException.class)
                .hasMessage(MOIM_FORBIDDEN.getMessage());
    }

    @Test
    @DisplayName("모임의 간략한 정보 획득")
    void getSummaryMoim() throws IOException {
        FakeContainer container = new FakeContainer();

//        회원가입
        Long leaderId = container.memberService.register(new MemberRegisterRequest("asdf@asdf.com", "asdf1234!@", "닉네임"));

//        모임 생성
        MoimTagRequest moimTagRequest = new MoimTagRequest(List.of("tag1", "tag2", "tag3"));
        MoimTagRequest moimTagRequest2 = new MoimTagRequest(List.of("태그1", "태그2", "태그3"));
        MockMultipartFile mockMultipartFile = new MockMultipartFile("photo.jpeg", "photo.jpeg", "image/jpeg", new byte[1]);
        MoimRegisterRequest requestMoim = new MoimRegisterRequest("모임", "모임 설명", "mixed", true);
        container.moimService.register(requestMoim, moimTagRequest, null, mockMultipartFile, leaderId);
        MoimRegisterRequest requestMoim2 = new MoimRegisterRequest("모임2", "모임 설명2", "faceToFace", true);
        container.moimService.register(requestMoim2, moimTagRequest2, null, null, leaderId);

        Page<Moim> page = container.moimService.search(new MoimSearchRequest("name", "모임"), PageRequest.of(0, 10));
        List<Moim> moimList = page.getContent();
        List<MoimSearchResponse> summaryMoimList = container.moimService.getSummaryMoim(moimList);

        assertAll(
                () -> assertThat(summaryMoimList).extracting(MoimSearchResponse::getName).containsExactlyInAnyOrder("모임", "모임2"),
                () -> assertThat(summaryMoimList).extracting(MoimSearchResponse::getIntroduction).containsExactlyInAnyOrder("모임 설명", "모임 설명2"),
                () -> assertThat(summaryMoimList).extracting(MoimSearchResponse::getMoimType).containsExactlyInAnyOrder(MoimType.MIXED, MoimType.FACE_TO_FACE),
                () -> assertThat(summaryMoimList).extracting(MoimSearchResponse::getPhotoName).containsExactlyInAnyOrder(null, "photo.jpeg"),
                () -> assertThat(summaryMoimList).extracting(MoimSearchResponse::getTagList).containsExactlyInAnyOrder(List.of("tag1", "tag2", "tag3"), List.of("태그1", "태그2", "태그3"))
        );
    }
}