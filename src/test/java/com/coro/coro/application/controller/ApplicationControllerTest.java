package com.coro.coro.application.controller;

import com.coro.coro.application.domain.Application;
import com.coro.coro.application.domain.ApplicationStatus;
import com.coro.coro.application.dto.request.ApplicationDTO;
import com.coro.coro.application.dto.request.ApplicationQuestionRegisterRequest;
import com.coro.coro.application.dto.request.ApplicationRequest;
import com.coro.coro.application.dto.response.ApplicationAnswerDTO;
import com.coro.coro.application.dto.response.ApplicationQuestionResponse;
import com.coro.coro.application.dto.response.ApplicationResponse;
import com.coro.coro.application.dto.response.DetailedApplicationResponse;
import com.coro.coro.application.exception.ApplicationException;
import com.coro.coro.common.response.APIResponse;
import com.coro.coro.member.domain.Member;
import com.coro.coro.member.domain.MemberRole;
import com.coro.coro.member.dto.request.MemberRegisterRequest;
import com.coro.coro.member.service.User;
import com.coro.coro.mock.FakeContainer;
import com.coro.coro.moim.domain.Moim;
import com.coro.coro.moim.domain.MoimMember;
import com.coro.coro.moim.dto.request.MoimRegisterRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static com.coro.coro.common.response.error.ErrorType.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertAll;

class ApplicationControllerTest {

    @SuppressWarnings("unchecked")
    @Test
    @DisplayName("해당 모임에 지원된 모든 지원서 획득")
    void getApplication() throws IOException {
        FakeContainer container = new FakeContainer();

//        회원가입
        Long leaderId = container.memberService.register(new MemberRegisterRequest("asdf@asdf.com", "asdf1234!@", "닉네임"));

//        모임 생성
        Long moimId = container.moimService.register(
                new MoimRegisterRequest("모임", "모임설명", "faceToFace", true),
                null,
                null,
                null,
                leaderId
        );

//        지원할 회원 생성
        Long memberId = container.memberService.register(new MemberRegisterRequest("a@a.com", "asdf1234!@", "닉네임2"));

//        지원서 제출
        Member member = container.memberRepository.findById(memberId)
                .orElseThrow(() -> new ApplicationException(MEMBER_NOT_FOUND));

        Moim moim = container.moimRepository.findById(moimId)
                .orElseThrow(() -> new ApplicationException(MOIM_NOT_FOUND));

        Application refuseApplication = Application.builder()
                .member(member)
                .moim(moim)
                .status(ApplicationStatus.REFUSE)
                .build();

        container.applicationRepository.save(refuseApplication);
        container.applicationService.register(moimId, new ApplicationRequest(List.of()), memberId);

//        모든 지원서 획득
        APIResponse response = container.applicationController.getApplication(moimId, "all");
        List<ApplicationResponse> applicationList = (List<ApplicationResponse>) response.getBody().get("applicationList");

//        검증
        assertAll(
                () -> assertThat(applicationList).extracting(ApplicationResponse::getApplicantName).containsOnly("닉네임2"),
                () -> assertThat(applicationList).extracting(ApplicationResponse::getStatus).containsExactlyInAnyOrder(ApplicationStatus.WAIT, ApplicationStatus.REFUSE)
        );
    }

    @SuppressWarnings("unchecked")
    @Test
    @DisplayName("로그인한 유저의 해당 모임에 지원한 지원서 획득")
    void getApplicationByMember() throws IOException {
        FakeContainer container = new FakeContainer();

//        회원가입
        Long leaderId = container.memberService.register(new MemberRegisterRequest("asdf@asdf.com", "asdf1234!@", "닉네임"));

//        모임 생성
        Long moimId = container.moimService.register(
                new MoimRegisterRequest("모임", "모임설명", "faceToFace", true),
                null,
                null,
                null,
                leaderId
        );

//        지원할 회원 생성
        Long memberId = container.memberService.register(new MemberRegisterRequest("a@a.com", "asdf1234!@", "닉네임2"));

//        지원서 제출
        container.applicationService.register(moimId, new ApplicationRequest(List.of()), memberId);

//        검증
        Member member = container.memberRepository.findById(memberId)
                .orElseThrow(() -> new ApplicationException(MEMBER_NOT_FOUND));

        User user = User.mappingUserDetails(member);

        APIResponse response = container.applicationController.getApplicationByMember(moimId, "all", user);
        List<ApplicationResponse> applicationList = (List<ApplicationResponse>) response.getBody().get("applicationList");

        assertThat(applicationList).extracting(ApplicationResponse::getApplicantName).containsExactlyInAnyOrder("닉네임2");
        assertThat(applicationList).extracting(ApplicationResponse::getStatus).containsExactlyInAnyOrder(ApplicationStatus.WAIT);
    }

    @Test
    @DisplayName("디테일한 지원서 정보 획득")
    void getDetailedApplication() throws IOException {
        FakeContainer container = new FakeContainer();

//        회원가입
        Long leaderId = container.memberService.register(new MemberRegisterRequest("asdf@asdf.com", "asdf1234!@", "닉네임"));

//        모임 생성
        List<ApplicationQuestionRegisterRequest> questionRequestList = List.of(
                new ApplicationQuestionRegisterRequest("질문1", 1),
                new ApplicationQuestionRegisterRequest("질문2", 2)
        );
        Long moimId = container.moimService.register(
                new MoimRegisterRequest("모임", "모임설명", "faceToFace", true),
                null,
                questionRequestList,
                null,
                leaderId
        );

//        지원할 회원 생성
        Long memberId = container.memberService.register(new MemberRegisterRequest("a@a.com", "asdf1234!@", "닉네임2"));

//        지원서 제출
        ApplicationRequest applicationRequest = new ApplicationRequest(List.of(
                new ApplicationDTO("답변1", 1),
                new ApplicationDTO("답변2", 2))
        );
        Long applicationId = container.applicationService.register(moimId, applicationRequest, memberId);

//        디테일한 지원서 획득
        APIResponse response = container.applicationController.getDetailedApplication(applicationId);
        DetailedApplicationResponse application = (DetailedApplicationResponse) response.getBody().get("application");

//        검증
        assertAll(
                () -> assertThat(application.getStatus()).isEqualTo(ApplicationStatus.WAIT),
                () -> assertThat(application.getApplicationAnswerDTOList()).extracting(ApplicationAnswerDTO::getQuestion).containsExactlyInAnyOrder("질문1", "질문2"),
                () -> assertThat(application.getApplicationAnswerDTOList()).extracting(ApplicationAnswerDTO::getAnswer).containsExactlyInAnyOrder("답변1", "답변2")
        );
    }

    @SuppressWarnings("unchecked")
    @Test
    @DisplayName("특정 모임의 지원 양식 획득")
    void getQuestionList() throws IOException {
        FakeContainer container = new FakeContainer();

//        회원가입
        Long leaderId = container.memberService.register(new MemberRegisterRequest("asdf@asdf.com", "asdf1234!@", "닉네임"));

//        모임 생성
        List<ApplicationQuestionRegisterRequest> questionRequestList = List.of(
                new ApplicationQuestionRegisterRequest("질문1", 1),
                new ApplicationQuestionRegisterRequest("질문2", 2)
        );
        Long moimId = container.moimService.register(
                new MoimRegisterRequest("모임", "모임설명", "faceToFace", true),
                null,
                questionRequestList,
                null,
                leaderId
        );

        APIResponse response = container.applicationController.getQuestionList(moimId);
        List<ApplicationQuestionResponse> questionList = (List<ApplicationQuestionResponse>) response.getBody().get("questionList");

        assertThat(questionList).extracting(ApplicationQuestionResponse::getContent).containsExactlyInAnyOrder("질문1", "질문2");
    }

    @Test
    @DisplayName("지원서 제출")
    void submitApplication() throws IOException {
        FakeContainer container = new FakeContainer();

//        회원가입
        Long leaderId = container.memberService.register(new MemberRegisterRequest("asdf@asdf.com", "asdf1234!@", "닉네임"));

//        모임 생성
        List<ApplicationQuestionRegisterRequest> questionRequestList = List.of(
                new ApplicationQuestionRegisterRequest("질문1", 1),
                new ApplicationQuestionRegisterRequest("질문2", 2)
        );
        Long moimId = container.moimService.register(
                new MoimRegisterRequest("모임", "모임설명", "faceToFace", true),
                null,
                questionRequestList,
                null,
                leaderId
        );

//        지원할 회원 생성
        Long memberId = container.memberService.register(new MemberRegisterRequest("a@a.com", "asdf1234!@", "닉네임2"));

//        지원서 제출
        ApplicationRequest applicationRequest = new ApplicationRequest(List.of(
                new ApplicationDTO("답변1", 1),
                new ApplicationDTO("답변2", 2))
        );

        Member member = container.memberRepository.findById(memberId)
                .orElseThrow(() -> new ApplicationException(MEMBER_NOT_FOUND));

        User user = User.mappingUserDetails(member);

        APIResponse response = container.applicationController.submitApplication(moimId, applicationRequest, user);
        Long applicationId = (Long) response.getBody().get("applicationId");

//        검증
        Application application = container.applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ApplicationException(APPLICATION_NOT_FOUND));

        Moim moim = container.moimRepository.findById(moimId)
                .orElseThrow(() -> new ApplicationException(MOIM_NOT_FOUND));

        assertAll(
                () -> assertThat(application.getMoim()).isEqualTo(moim),
                () -> assertThat(application.getStatus()).isEqualTo(ApplicationStatus.WAIT),
                () -> assertThat(application.getMember()).isEqualTo(member)
        );
    }

    @Test
    @DisplayName("지원서 결정하기")
    void decideApplication() throws IOException {
        FakeContainer container = new FakeContainer();

//        회원가입
        Long leaderId = container.memberService.register(new MemberRegisterRequest("asdf@asdf.com", "asdf1234!@", "닉네임"));

//        모임 생성
        List<ApplicationQuestionRegisterRequest> questionRequestList = List.of(
                new ApplicationQuestionRegisterRequest("질문1", 1),
                new ApplicationQuestionRegisterRequest("질문2", 2)
        );
        Long moimId = container.moimService.register(
                new MoimRegisterRequest("모임", "모임설명", "faceToFace", true),
                null,
                questionRequestList,
                null,
                leaderId
        );

//        지원할 회원 생성
        Long memberId = container.memberService.register(new MemberRegisterRequest("a@a.com", "asdf1234!@", "닉네임2"));

//        지원서 제출
        ApplicationRequest applicationRequest = new ApplicationRequest(List.of(
                new ApplicationDTO("답변1", 1),
                new ApplicationDTO("답변2", 2))
        );

        Member member = container.memberRepository.findById(memberId)
                .orElseThrow(() -> new ApplicationException(MEMBER_NOT_FOUND));

        User user = User.mappingUserDetails(member);

        APIResponse submitResponse = container.applicationController.submitApplication(moimId, applicationRequest, user);

//        지원서 결정
        Long applicationId = (Long) submitResponse.getBody().get("applicationId");

        Member leader = container.memberRepository.findById(leaderId)
                .orElseThrow(() -> new ApplicationException(MEMBER_NOT_FOUND));
        User leaderUser = User.mappingUserDetails(leader);

        container.applicationController.decideApplication(applicationId, ApplicationStatus.ACCEPT, leaderUser);

//        검증
        Application application = container.applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ApplicationException(APPLICATION_NOT_FOUND));
        MoimMember moimMember = container.moimMemberRepository.findByMoimIdAndMemberId(moimId, memberId)
                .orElseThrow(() -> new ApplicationException(MOIM_MEMBER_NOT_FOUND));
        Moim moim = container.moimRepository.findById(moimId)
                .orElseThrow(() -> new ApplicationException(MOIM_NOT_FOUND));

        assertAll(
                () -> assertThat(application.getStatus()).isEqualTo(ApplicationStatus.ACCEPT),
                () -> assertThat(moimMember.getMember()).isEqualTo(member),
                () -> assertThat(moimMember.getMoim()).isEqualTo(moim),
                () -> assertThat(moimMember.getRole()).isEqualTo(MemberRole.USER)
        );
    }
}