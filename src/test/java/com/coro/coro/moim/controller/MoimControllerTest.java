package com.coro.coro.moim.controller;

import com.coro.coro.application.domain.ApplicationQuestion;
import com.coro.coro.application.dto.request.ApplicationQuestionRegisterRequest;
import com.coro.coro.application.dto.response.ApplicationQuestionResponse;
import com.coro.coro.common.response.APIResponse;
import com.coro.coro.member.domain.Member;
import com.coro.coro.member.domain.MemberRole;
import com.coro.coro.member.dto.request.MemberRegisterRequest;
import com.coro.coro.member.service.User;
import com.coro.coro.mock.FakeContainer;
import com.coro.coro.moim.domain.Moim;
import com.coro.coro.moim.domain.MoimMember;
import com.coro.coro.moim.domain.MoimTag;
import com.coro.coro.moim.domain.MoimType;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.coro.coro.common.response.error.ErrorType.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertAll;

class MoimControllerTest {

    @Test
    @DisplayName("모임 디테일 정보 획득")
    void detail() throws IOException {
        FakeContainer container = new FakeContainer();

//        회원가입
        Long memberIdSaved = container.memberService.register(new MemberRegisterRequest("asdf@asdf.com", "asdf1234!@", "닉네임"));

//        모임 생성
        MoimRegisterRequest requestMoim = new MoimRegisterRequest("모임", "모임소개", "mixed", true);
        MoimTagRequest moimTagRequest = new MoimTagRequest(List.of("tag1", "tag2", "tag3"));
        MockMultipartFile multipartFile = new MockMultipartFile("photo.jpeg", "photo.jpeg", "image/jpeg", new byte[1]);
        Long moimIdSaved = container.moimService.register(requestMoim, moimTagRequest, null, multipartFile, memberIdSaved);

        Member member = container.memberRepository.findById(memberIdSaved)
                .orElseThrow(() -> new MoimException(MEMBER_NOT_FOUND));

//        디테일 정보 획득
        User user = User.mappingUserDetails(member);

        APIResponse response = container.moimController.detail(moimIdSaved, user);

//        검증
        MoimDetailResponse moimDetail = (MoimDetailResponse) response.getBody().get("moim");

        assertAll(
                () -> assertThat(moimDetail.getName()).isEqualTo("모임"),
                () -> assertThat(moimDetail.getIntroduction()).isEqualTo("모임소개"),
                () -> assertThat(moimDetail.getTagList()).containsExactlyInAnyOrder("tag1", "tag2", "tag3"),
                () -> assertThat(moimDetail.isJoined()).isTrue(),
                () -> assertThat(moimDetail.isCanManage()).isTrue(),
                () -> assertThat(moimDetail.getPhotoName()).isEqualTo("photo.jpeg")
        );
    }

    @Test
    @DisplayName("모임 디테일 정보 획득 실패 - 존재하지 않는 모임 Id")
    void detailFailByNotValidMoimId() {
        FakeContainer container = new FakeContainer();

//        회원가입
        Long memberIdSaved = container.memberService.register(new MemberRegisterRequest("asdf@asdf.com", "asdf1234!@", "닉네임"));

        Member member = container.memberRepository.findById(memberIdSaved)
                .orElseThrow(() -> new MoimException(MEMBER_NOT_FOUND));

//        검증
        User user = User.mappingUserDetails(member);

        assertThatThrownBy(() ->
                container.moimController.detail(1L, user)
        )
                .isInstanceOf(MoimException.class)
                .hasMessage(MOIM_NOT_FOUND.getMessage());
    }

    @SuppressWarnings("unchecked")
    @ParameterizedTest
    @DisplayName("모임 검색 - 이름으로 검색")
    @ValueSource(ints = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9})
    void search(final int index) throws IOException {
        FakeContainer container = new FakeContainer();

//        회원가입
        Long memberIdSaved = container.memberService.register(new MemberRegisterRequest("asdf@asdf.com", "asdf1234!@", "닉네임"));

//        모임 반복 생성
        MoimRegisterRequest requestMoim = new MoimRegisterRequest("모임", "모임소개", "mixed", true);
        MoimTagRequest moimTagRequest = new MoimTagRequest(List.of("tag"));
        MockMultipartFile multipartFile = new MockMultipartFile("photo.jpeg", "photo.jpeg", "image/jpeg", new byte[1]);
        generateMoims(container, 100, requestMoim, moimTagRequest, multipartFile, memberIdSaved);

//        검색
        APIResponse response = container.moimController.search(new MoimSearchRequest("name", "모임"), PageRequest.of(index, 10));
        Map<String, Object> body = response.getBody();

//        검증
        int totalPages = (int) body.get("totalPages");
        List<MoimSearchResponse> list = (List<MoimSearchResponse>) body.get("list");

        assertAll(
                () -> assertThat(totalPages).isEqualTo(10),
                () -> assertThat(list).extracting(MoimSearchResponse::getName).contains("모임" + (index * 10 + 1), "모임" + ((index + 1) * 10)),
                () -> assertThat(list).extracting(MoimSearchResponse::getIntroduction).contains("모임소개" + (index * 10 + 1), "모임소개" + ((index + 1) * 10)),
                () -> assertThat(list).extracting(MoimSearchResponse::getMoimType).contains(MoimType.MIXED, MoimType.FACE_TO_FACE),
                () -> assertThat(list).extracting(MoimSearchResponse::getTagList).contains(List.of("tag" + (index * 10 + 1)), List.of("tag" + ((index + 1) * 10))),
                () -> assertThat(list).extracting(MoimSearchResponse::getPhotoName).contains((index * 10 + 1) + "photo.jpeg", ((index + 1) * 10) + "photo.jpeg")
        );
    }

    @SuppressWarnings("SameParameterValue")
    private void generateMoims(final FakeContainer container, final int counts, final MoimRegisterRequest originalRequestMoim, final MoimTagRequest originalRequestMoimTag, final MockMultipartFile originalMultipartFile, final Long memberId) throws IOException {
        for (int i = 1; i <= counts; i++) {
            String type;
            if (i % 2 == 0) {
                type = "mixed";
            } else {
                type = "faceToFace";
            }
            MoimRegisterRequest moimRegisterRequest = new MoimRegisterRequest(originalRequestMoim.getName() + i, originalRequestMoim.getIntroduction() + i, type, true);
            MockMultipartFile mockMultipartFile = new MockMultipartFile(i + originalMultipartFile.getName(), i + originalMultipartFile.getOriginalFilename(), originalMultipartFile.getContentType(), new byte[1]);
            List<String> tagList = new ArrayList<>();
            MoimTagRequest moimTagRequest = new MoimTagRequest(tagList);
            for (String tag : originalRequestMoimTag.getTagList()) {
                tagList.add(tag + i);
            }
            container.moimService.register(
                    moimRegisterRequest,
                    moimTagRequest,
                    null,
                    mockMultipartFile,
                    memberId
            );
        }
    }

    @SuppressWarnings("unchecked")
    @ParameterizedTest
    @DisplayName("모임 검색 - 태그로 검색")
    @ValueSource(ints = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9})
    void searchByTag(final int index) throws IOException {
        FakeContainer container = new FakeContainer();

//        회원가입
        Long memberIdSaved = container.memberService.register(new MemberRegisterRequest("asdf@asdf.com", "asdf1234!@", "닉네임"));

//        모임 반복 생성
        MoimRegisterRequest requestMoim = new MoimRegisterRequest("모임", "모임소개", "mixed", true);
        MoimTagRequest moimTagRequest = new MoimTagRequest(List.of("tag"));
        MockMultipartFile multipartFile = new MockMultipartFile("photo.jpeg", "photo.jpeg", "image/jpeg", new byte[1]);
        generateMoims(container, 100, requestMoim, moimTagRequest, multipartFile, memberIdSaved);

//        검색
        APIResponse response = container.moimController.search(new MoimSearchRequest("tag", "tag"), PageRequest.of(index, 10));
        Map<String, Object> body = response.getBody();

//        검증
        int totalPages = (int) body.get("totalPages");
        List<MoimSearchResponse> list = (List<MoimSearchResponse>) body.get("list");

        assertAll(
                () -> assertThat(totalPages).isEqualTo(10),
                () -> assertThat(list).extracting(MoimSearchResponse::getName).contains("모임" + (index * 10 + 1), "모임" + ((index + 1) * 10)),
                () -> assertThat(list).extracting(MoimSearchResponse::getIntroduction).contains("모임소개" + (index * 10 + 1), "모임소개" + ((index + 1) * 10)),
                () -> assertThat(list).extracting(MoimSearchResponse::getMoimType).contains(MoimType.MIXED, MoimType.FACE_TO_FACE),
                () -> assertThat(list).extracting(MoimSearchResponse::getTagList).contains(List.of("tag" + (index * 10 + 1)), List.of("tag" + ((index + 1) * 10))),
                () -> assertThat(list).extracting(MoimSearchResponse::getPhotoName).contains((index * 10 + 1) + "photo.jpeg", ((index + 1) * 10) + "photo.jpeg")
        );
    }

    @Test
    @DisplayName("모임 생성")
    void register() throws IOException {
        FakeContainer container = new FakeContainer();

//        회원가입
        Long memberIdSaved = container.memberService.register(new MemberRegisterRequest("asdf@asdf.com", "asdf1234!@", "닉네임"));

//        모임 생성
        MoimRegisterRequest moimRegisterRequest = new MoimRegisterRequest("모임", "모임 설명", "nonContact", true);
        MoimTagRequest moimTagRequest = new MoimTagRequest(List.of("tag1", "tag2", "tag3"));
        List<ApplicationQuestionRegisterRequest> applicationQuestionRegisterRequests = List.of(new ApplicationQuestionRegisterRequest("질문1", 1), new ApplicationQuestionRegisterRequest("질문2", 2));
        MockMultipartFile mockMultipartFile = new MockMultipartFile("photo.jpeg", "photo.jpeg", "image/jpeg", new byte[1]);

        Member member = container.memberRepository.findById(memberIdSaved)
                .orElseThrow(() -> new MoimException(MEMBER_NOT_FOUND));

        User user = User.mappingUserDetails(member);
        APIResponse response = container.moimController.register(moimRegisterRequest, moimTagRequest, applicationQuestionRegisterRequests, mockMultipartFile, user);
        Long moimId = (Long) response.getBody().get("moimId");

//        검증
        Moim moim = container.moimRepository.findById(moimId)
                .orElseThrow(() -> new MoimException(MOIM_NOT_FOUND));

        assertAll(
                () -> assertThat(moimId).isNotNull(),
                () -> assertThat(moimId).isEqualTo(moim.getId()),
                () -> assertThat(moim.getName()).isEqualTo("모임"),
                () -> assertThat(moim.getIntroduction()).isEqualTo("모임 설명"),
                () -> assertThat(moim.getType()).isEqualTo(MoimType.NON_CONTACT),
                () -> assertThat(moim.getVisible()).isTrue(),
                () -> assertThat(moim.getTagList()).extracting(MoimTag::getName).containsExactlyInAnyOrder("tag1", "tag2", "tag3"),
                () -> assertThat(moim.getQuestionList()).extracting(ApplicationQuestion::getContent).containsExactlyInAnyOrder("질문1", "질문2"),
                () -> assertThat(moim.getLeader()).isEqualTo(member)
        );
    }

    @Test
    @DisplayName("모임 수정")
    void update() throws IOException {
        FakeContainer container = new FakeContainer();

//        회원가입
        Long memberIdSaved = container.memberService.register(new MemberRegisterRequest("asdf@asdf.com", "asdf1234!@", "닉네임"));

//        모임 생성
        MoimRegisterRequest moimRegisterRequest = new MoimRegisterRequest("모임", "모임 설명", "nonContact", true);
        MoimTagRequest moimTagRequest = new MoimTagRequest(List.of("tag1", "tag2", "tag3"));
        List<ApplicationQuestionRegisterRequest> applicationQuestionRegisterRequests = List.of(
                new ApplicationQuestionRegisterRequest("질문1", 1),
                new ApplicationQuestionRegisterRequest("질문2", 2)
        );
        MockMultipartFile mockMultipartFile = new MockMultipartFile("photo.jpeg", "photo.jpeg", "image/jpeg", new byte[1]);

        Long moimId = container.moimService.register(moimRegisterRequest, moimTagRequest, applicationQuestionRegisterRequests, mockMultipartFile, memberIdSaved);

//        모임 변경
        MoimModificationRequest moimModificationRequest = new MoimModificationRequest("변경된 모임명", "변경된 모임 설명", "mixed", false, true);
        MoimTagRequest moimTagModificationRequest = new MoimTagRequest(List.of("태그1", "태그2", "태그3"));
        List<ApplicationQuestionRegisterRequest> questionModificationRequest = List.of(
                new ApplicationQuestionRegisterRequest("변경된 질문1", 1),
                new ApplicationQuestionRegisterRequest("변경된 질문2", 2),
                new ApplicationQuestionRegisterRequest("변경된 질문3", 3)
        );

        Member member = container.memberRepository.findById(memberIdSaved)
                .orElseThrow(() -> new MoimException(MEMBER_NOT_FOUND));

        User user = User.mappingUserDetails(member);
        container.moimController.update(
                moimId,
                moimModificationRequest,
                moimTagModificationRequest,
                questionModificationRequest,
                null,
                user
        );

//        검증
        Moim moim = container.moimRepository.findById(moimId)
                .orElseThrow(() -> new MoimException(MOIM_NOT_FOUND));

        assertAll(
                () -> assertThat(moim.getName()).isEqualTo("변경된 모임명"),
                () -> assertThat(moim.getIntroduction()).isEqualTo("변경된 모임 설명"),
                () -> assertThat(moim.getVisible()).isFalse(),
                () -> assertThat(moim.getType()).isEqualTo(MoimType.MIXED),
                () -> assertThat(moim.getTagList()).extracting(MoimTag::getName).containsExactlyInAnyOrder("태그1", "태그2", "태그3"),
                () -> assertThat(moim.getQuestionList()).extracting(ApplicationQuestion::getContent).containsExactlyInAnyOrder("변경된 질문1", "변경된 질문2", "변경된 질문3")
        );
    }

    @Test
    @DisplayName("모임 수정을 위한 모임 정보 획득")
    void getMoimForModification() throws IOException {
        FakeContainer container = new FakeContainer();

//        회원가입
        Long memberIdSaved = container.memberService.register(new MemberRegisterRequest("asdf@asdf.com", "asdf1234!@", "닉네임"));

//        모임 생성
        MoimRegisterRequest moimRegisterRequest = new MoimRegisterRequest("모임", "모임 설명", "nonContact", true);
        MoimTagRequest moimTagRequest = new MoimTagRequest(List.of("tag1", "tag2", "tag3"));
        List<ApplicationQuestionRegisterRequest> applicationQuestionRegisterRequests = List.of(
                new ApplicationQuestionRegisterRequest("질문1", 1),
                new ApplicationQuestionRegisterRequest("질문2", 2)
        );
        MockMultipartFile mockMultipartFile = new MockMultipartFile("photo.jpeg", "photo.jpeg", "image/jpeg", new byte[1]);

        Long moimId = container.moimService.register(moimRegisterRequest, moimTagRequest, applicationQuestionRegisterRequests, mockMultipartFile, memberIdSaved);

//        검증
        Member member = container.memberRepository.findById(memberIdSaved)
                .orElseThrow(() -> new MoimException(MEMBER_NOT_FOUND));

        User user = User.mappingUserDetails(member);

        APIResponse response = container.moimController.getMoimForModification(moimId, user);
        MoimModificationResponse detail = (MoimModificationResponse) response.getBody().get("detail");

        assertAll(
                () -> assertThat(detail.getName()).isEqualTo("모임"),
                () -> assertThat(detail.getIntroduction()).isEqualTo("모임 설명"),
                () -> assertThat(detail.isVisible()).isTrue(),
                () -> assertThat(detail.getType()).isEqualTo("nonContact"),
                () -> assertThat(detail.getPhotoName()).isEqualTo("photo.jpeg"),
                () -> assertThat(detail.getTagList()).containsExactlyInAnyOrder("tag1", "tag2", "tag3"),
                () -> assertThat(detail.getApplicationQuestionList()).extracting(ApplicationQuestionResponse::getContent).containsExactlyInAnyOrder("질문1", "질문2")
        );
    }

    @SuppressWarnings("unchecked")
    @Test
    @DisplayName("모임에 가입된 회원 리스트 획득")
    void getMoimMember() throws IOException {
        FakeContainer container = new FakeContainer();

//        회원가입
        Long memberIdSaved = container.memberService.register(new MemberRegisterRequest("asdf@asdf.com", "asdf1234!@", "닉네임"));

//        모임 생성
        MoimRegisterRequest moimRegisterRequest = new MoimRegisterRequest("모임", "모임 설명", "nonContact", true);
        MoimTagRequest moimTagRequest = new MoimTagRequest(List.of("tag1", "tag2", "tag3"));
        List<ApplicationQuestionRegisterRequest> applicationQuestionRegisterRequests = List.of(
                new ApplicationQuestionRegisterRequest("질문1", 1),
                new ApplicationQuestionRegisterRequest("질문2", 2)
        );
        MockMultipartFile mockMultipartFile = new MockMultipartFile("photo.jpeg", "photo.jpeg", "image/jpeg", new byte[1]);

        Long moimId = container.moimService.register(moimRegisterRequest, moimTagRequest, applicationQuestionRegisterRequests, mockMultipartFile, memberIdSaved);

//        모임에 가입된 회원 생성
        Long memberId = container.memberService.register(new MemberRegisterRequest("a@a.com", "asdf1234!@", "닉네임2"));

        Moim moim = container.moimRepository.findById(moimId)
                .orElseThrow(() -> new MoimException(MOIM_NOT_FOUND));
        Member member = container.memberRepository.findById(memberId)
                .orElseThrow(() -> new MoimException(MEMBER_NOT_FOUND));
        MoimMember moimMember = MoimMember.builder()
                .moim(moim)
                .member(member)
                .role(MemberRole.USER)
                .build();
        container.moimMemberRepository.save(moimMember);

//        검증
        Member leader = container.memberRepository.findById(memberIdSaved)
                .orElseThrow(() -> new MoimException(MEMBER_NOT_FOUND));

        User user = User.mappingUserDetails(leader);

        APIResponse response = container.moimController.getMoimMember(moimId, user);
        Map<String, Object> body = response.getBody();
        List<MoimMemberResponse> moimMemberList = (List<MoimMemberResponse>) body.get("moimMemberList");
        MemberRole loggedInMemberRole = (MemberRole) body.get("role");

        assertAll(
                () -> assertThat(moimMemberList).extracting(MoimMemberResponse::getMemberName).containsExactlyInAnyOrder("닉네임", "닉네임2"),
                () -> assertThat(moimMemberList).extracting(MoimMemberResponse::getRole).containsExactlyInAnyOrder(MemberRole.USER, MemberRole.LEADER),
                () -> assertThat(loggedInMemberRole).isEqualTo(MemberRole.LEADER)
        );
    }

    @Test
    @DisplayName("모임에 가입된 회원들의 역할 수정")
    void changeMoimMember() throws IOException {
        FakeContainer container = new FakeContainer();

//        회원가입
        Long leaderId = container.memberService.register(new MemberRegisterRequest("asdf@asdf.com", "asdf1234!@", "닉네임"));

//        모임 생성
        MoimRegisterRequest moimRegisterRequest = new MoimRegisterRequest("모임", "모임 설명", "nonContact", true);
        MoimTagRequest moimTagRequest = new MoimTagRequest(List.of("tag1", "tag2", "tag3"));
        List<ApplicationQuestionRegisterRequest> applicationQuestionRegisterRequests = List.of(
                new ApplicationQuestionRegisterRequest("질문1", 1),
                new ApplicationQuestionRegisterRequest("질문2", 2)
        );
        MockMultipartFile mockMultipartFile = new MockMultipartFile("photo.jpeg", "photo.jpeg", "image/jpeg", new byte[1]);

        Long moimId = container.moimService.register(moimRegisterRequest, moimTagRequest, applicationQuestionRegisterRequests, mockMultipartFile, leaderId);

//        새로운 회원 생성
        Long memberId = container.memberService.register(new MemberRegisterRequest("a@a.com", "asdf1234!@", "닉네임2"));

        Moim moim = container.moimRepository.findById(moimId)
                .orElseThrow(() -> new MoimException(MOIM_NOT_FOUND));

        Member member = container.memberRepository.findById(memberId)
                .orElseThrow(() -> new MoimException(MEMBER_NOT_FOUND));

        Long savedMoimMemberId = container.moimMemberRepository.save(
                MoimMember.builder()
                .moim(moim)
                .member(member)
                .role(MemberRole.USER)
                .build()
        );

//        역할 수정
        MoimMember leaderMoimMember = container.moimMemberRepository.findByMoimIdAndMemberId(moimId, leaderId)
                .orElseThrow(() -> new MoimException(MOIM_MEMBER_NOT_FOUND));

        List<MoimMemberModificationRequest> moimMemberModificationRequests = List.of(
                new MoimMemberModificationRequest(savedMoimMemberId, "닉네임2", MemberRole.LEADER),
                new MoimMemberModificationRequest(leaderMoimMember.getId(), "닉네임", MemberRole.MANAGER)
        );
        Member leader = container.memberRepository.findById(leaderId)
                .orElseThrow(() -> new MoimException(MEMBER_NOT_FOUND));
        User user = User.mappingUserDetails(leader);

        container.moimController.changeMoimMember(moimId, moimMemberModificationRequests, user);

//        검증
        MoimMember moimMember = container.moimMemberRepository.findById(savedMoimMemberId)
                .orElseThrow(() -> new MoimException(MOIM_MEMBER_NOT_FOUND));

        assertAll(
                () -> assertThat(leaderMoimMember.getMember()).isEqualTo(leader),
                () -> assertThat(leaderMoimMember.getMoim()).isEqualTo(moim),
                () -> assertThat(leaderMoimMember.getRole()).isEqualTo(MemberRole.MANAGER),
                () -> assertThat(moimMember.getMember()).isEqualTo(member),
                () -> assertThat(moimMember.getMoim()).isEqualTo(moim),
                () -> assertThat(moimMember.getRole()).isEqualTo(MemberRole.LEADER)
        );
    }

    @Test
    @DisplayName("회원 추방")
    void deportMember() throws IOException {
        FakeContainer container = new FakeContainer();

//        회원가입
        Long leaderId = container.memberService.register(new MemberRegisterRequest("asdf@asdf.com", "asdf1234!@", "닉네임"));

//        모임 생성
        MoimRegisterRequest moimRegisterRequest = new MoimRegisterRequest("모임", "모임 설명", "nonContact", true);
        MoimTagRequest moimTagRequest = new MoimTagRequest(List.of("tag1", "tag2", "tag3"));
        List<ApplicationQuestionRegisterRequest> applicationQuestionRegisterRequests = List.of(
                new ApplicationQuestionRegisterRequest("질문1", 1),
                new ApplicationQuestionRegisterRequest("질문2", 2)
        );
        MockMultipartFile mockMultipartFile = new MockMultipartFile("photo.jpeg", "photo.jpeg", "image/jpeg", new byte[1]);

        Long moimId = container.moimService.register(moimRegisterRequest, moimTagRequest, applicationQuestionRegisterRequests, mockMultipartFile, leaderId);

//        모임에 가입할 회원 생성
        Long memberId = container.memberService.register(new MemberRegisterRequest("a@a.com", "asdf1234!@", "닉네임2"));

        Moim moim = container.moimRepository.findById(moimId)
                .orElseThrow(() -> new MoimException(MOIM_NOT_FOUND));

        Member member = container.memberRepository.findById(memberId)
                .orElseThrow(() -> new MoimException(MEMBER_NOT_FOUND));

        Long savedMoimMemberId = container.moimMemberRepository.save(
                MoimMember.builder()
                        .moim(moim)
                        .member(member)
                        .role(MemberRole.USER)
                        .build()
        );

//        회원 추방
        Member leader = container.memberRepository.findById(leaderId)
                .orElseThrow(() -> new MoimException(MEMBER_NOT_FOUND));
        User user = User.mappingUserDetails(leader);

        container.moimController.deportMember(moimId, savedMoimMemberId, user);

//        검증
        List<MoimMember> moimMemberList = container.moimMemberRepository.findAllByMoimId(moimId);

        assertAll(
                () -> assertThat(moimMemberList).size().isEqualTo(1),
                () -> assertThat(moimMemberList).extracting(MoimMember::getMember).containsExactlyInAnyOrder(leader),
                () -> assertThat(moimMemberList).extracting(MoimMember::getMoim).containsExactlyInAnyOrder(moim),
                () -> assertThat(moimMemberList).extracting(MoimMember::getRole).containsExactlyInAnyOrder(MemberRole.LEADER)
        );
    }
}