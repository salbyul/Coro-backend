package com.coro.coro.application.service;

import com.coro.coro.application.domain.Application;
import com.coro.coro.application.domain.ApplicationStatus;
import com.coro.coro.application.dto.request.ApplicationDTO;
import com.coro.coro.application.dto.request.ApplicationQuestionRegisterRequest;
import com.coro.coro.application.dto.request.ApplicationRequest;
import com.coro.coro.application.dto.response.ApplicationAnswerDTO;
import com.coro.coro.application.dto.response.ApplicationResponse;
import com.coro.coro.application.dto.response.DetailedApplicationResponse;
import com.coro.coro.application.exception.ApplicationException;
import com.coro.coro.member.domain.Member;
import com.coro.coro.member.domain.MemberRole;
import com.coro.coro.member.dto.request.MemberRegisterRequest;
import com.coro.coro.mock.FakeContainer;
import com.coro.coro.moim.domain.Moim;
import com.coro.coro.moim.domain.MoimMember;
import com.coro.coro.moim.dto.request.MoimRegisterRequest;
import com.coro.coro.moim.dto.request.MoimTagRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static com.coro.coro.common.response.error.ErrorType.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertAll;

class ApplicationServiceTest {

    public static final String MEMBER_NICKNAME_TO_BE_JOINED = "가입할회원";

    @Test
    @DisplayName("지원서 등록")
    void register() throws IOException {
//        회원가입
        FakeContainer container = new FakeContainer();
        Long memberId = container.memberService.register(new MemberRegisterRequest("asdf@asdf.com", "asdf1234!@", "닉네임"));

//        모임 생성
        Long moimId = container.moimService.register(
                new MoimRegisterRequest("모임", "모임 소개", "mixed", true),
                new MoimTagRequest(),
                List.of(
                        new ApplicationQuestionRegisterRequest("질문1", 1),
                        new ApplicationQuestionRegisterRequest("질문2", 2)
                ),
                null,
                memberId
        );

//        회원 생성
        Long memberIdToBeJoined = container.memberService.register(new MemberRegisterRequest("a@a.com", "asdf1234!@", MEMBER_NICKNAME_TO_BE_JOINED));

//        지원서 제출
        Long applicationId = container.applicationService.register(
                moimId,
                new ApplicationRequest(List.of(new ApplicationDTO("답변1", 1), new ApplicationDTO("답변2", 2))),
                memberIdToBeJoined
        );

//        검증
        Application application = container.applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ApplicationException(APPLICATION_NOT_FOUND));

        Member member = container.memberRepository.findById(memberIdToBeJoined)
                .orElseThrow(() -> new ApplicationException(MEMBER_NOT_FOUND));

        Moim moim = container.moimRepository.findById(moimId)
                .orElseThrow(() -> new ApplicationException(MOIM_NOT_FOUND));

        assertAll(
                () -> assertThat(application.getMember()).isEqualTo(member),
                () -> assertThat(application.getMoim()).isEqualTo(moim),
                () -> assertThat(application.getStatus()).isEqualTo(ApplicationStatus.WAIT)
        );
    }

    @Test
    @DisplayName("지원서 등록 실패 - 올바르지 않은 모임 Id")
    void registerFailByNotValidMoimId() throws IOException {
        FakeContainer container = new FakeContainer();

//        회원가입
        Long memberId = container.memberService.register(new MemberRegisterRequest("asdf@asdf.com", "asdf1234!@", "닉네임"));

//        모임 생성
        container.moimService.register(
                new MoimRegisterRequest("모임", "모임 소개", "mixed", true),
                new MoimTagRequest(),
                List.of(
                        new ApplicationQuestionRegisterRequest("질문1", 1),
                        new ApplicationQuestionRegisterRequest("질문2", 2)
                ),
                null,
                memberId
        );

//        회원 생성
        Long memberIdToBeJoined = container.memberService.register(new MemberRegisterRequest("a@a.com", "asdf1234!@", MEMBER_NICKNAME_TO_BE_JOINED));

//        검증
        assertThatThrownBy(() ->
                container.applicationService.register(
                        99999L,
                        new ApplicationRequest(List.of(new ApplicationDTO("답변1", 1), new ApplicationDTO("답변2", 2))),
                        memberIdToBeJoined
                )
        )
                .isInstanceOf(ApplicationException.class)
                .hasMessage(MOIM_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("지원서 등록 실패 - 올바르지 않은 회원 Id")
    void registerFailByNotValidMemberId() throws IOException {
        FakeContainer container = new FakeContainer();

//        회원가입
        Long memberId = container.memberService.register(new MemberRegisterRequest("asdf@asdf.com", "asdf1234!@", "닉네임"));

//        모임 생성
        Long moimId = container.moimService.register(
                new MoimRegisterRequest("모임", "모임 소개", "mixed", true),
                new MoimTagRequest(),
                List.of(
                        new ApplicationQuestionRegisterRequest("질문1", 1),
                        new ApplicationQuestionRegisterRequest("질문2", 2)
                ),
                null,
                memberId
        );

//        회원 생성
        container.memberService.register(new MemberRegisterRequest("a@a.com", "asdf1234!@", MEMBER_NICKNAME_TO_BE_JOINED));

//        검증
        assertThatThrownBy(() ->
                container.applicationService.register(
                        moimId,
                        new ApplicationRequest(List.of(new ApplicationDTO("답변1", 1), new ApplicationDTO("답변2", 2))),
                        99999L
                )
        )
                .isInstanceOf(ApplicationException.class)
                .hasMessage(MEMBER_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("지원서 등록 성공 - 거절된 지원서가 있을 경우")
    void registerSuccessButExistRefuseApplication() throws IOException {
        FakeContainer container = new FakeContainer();

//        회원가입
        Long memberId = container.memberService.register(new MemberRegisterRequest("asdf@asdf.com", "asdf1234!@", "닉네임"));

//        모임 생성
        Long moimId = container.moimService.register(
                new MoimRegisterRequest("모임", "모임 소개", "mixed", true),
                new MoimTagRequest(),
                List.of(
                        new ApplicationQuestionRegisterRequest("질문1", 1),
                        new ApplicationQuestionRegisterRequest("질문2", 2)
                ),
                null,
                memberId
        );

//        회원 생성
        Long memberIdToBeJoined = container.memberService.register(new MemberRegisterRequest("a@a.com", "asdf1234!@", MEMBER_NICKNAME_TO_BE_JOINED));

//        거절된 지원서 생성
        Member memberTobeJoined = container.memberRepository.findById(memberIdToBeJoined)
                .orElseThrow(() -> new ApplicationException(MEMBER_NOT_FOUND));

        Moim moim = container.moimRepository.findById(moimId)
                .orElseThrow(() -> new ApplicationException(MOIM_NOT_FOUND));

        Application acceptedApplication = Application.builder()
                .member(memberTobeJoined)
                .moim(moim)
                .status(ApplicationStatus.REFUSE)
                .build();
        container.applicationRepository.save(acceptedApplication);

//        지원서 제출
        Long applicationId = container.applicationService.register(
                moimId,
                new ApplicationRequest(List.of(new ApplicationDTO("답변1", 1), new ApplicationDTO("답변2", 2))),
                memberIdToBeJoined
        );

//        검증
        Application application = container.applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ApplicationException(APPLICATION_NOT_FOUND));

        assertAll(
                () -> assertThat(application.getMember()).isEqualTo(memberTobeJoined),
                () -> assertThat(application.getMoim()).isEqualTo(moim),
                () -> assertThat(application.getStatus()).isEqualTo(ApplicationStatus.WAIT)
        );
    }

    @Test
    @DisplayName("지원서 등록 실패 - 이미 가입한 모임의 경우")
    void registerSuccessByNotWaitApplication() throws IOException {
        FakeContainer container = new FakeContainer();

//        회원가입
        Long memberId = container.memberService.register(new MemberRegisterRequest("asdf@asdf.com", "asdf1234!@", "닉네임"));

//        모임 생성
        Long moimId = container.moimService.register(
                new MoimRegisterRequest("모임", "모임 소개", "mixed", true),
                new MoimTagRequest(),
                List.of(
                        new ApplicationQuestionRegisterRequest("질문1", 1),
                        new ApplicationQuestionRegisterRequest("질문2", 2)
                ),
                null,
                memberId
        );

//        회원 생성
        Long memberIdToBeJoined = container.memberService.register(new MemberRegisterRequest("a@a.com", "asdf1234!@", MEMBER_NICKNAME_TO_BE_JOINED));

//        가입한 회원으로 전환
        Moim moim = container.moimRepository.findById(moimId)
                .orElseThrow(() -> new ApplicationException(MOIM_NOT_FOUND));

        Member memberToBeJoined = container.memberRepository.findById(memberIdToBeJoined)
                .orElseThrow(() -> new ApplicationException(MEMBER_NOT_FOUND));

        container.moimMemberRepository.save(
                MoimMember.builder()
                        .moim(moim)
                        .member(memberToBeJoined)
                        .role(MemberRole.USER)
                        .build()
        );

//        검증
        assertThatThrownBy(() ->
                container.applicationService.register(
                        moimId,
                        new ApplicationRequest(List.of(new ApplicationDTO("답변1", 1), new ApplicationDTO("답변2", 2))),
                        memberIdToBeJoined
                )
        )
                .isInstanceOf(ApplicationException.class)
                .hasMessage(APPLICATION_EXIST_MEMBER.getMessage());
    }

    @Test
    @DisplayName("지원서 등록 실패 - 이미 등록된 지원서가 있을 경우")
    void registerFailByDuplicateApplication() throws IOException {
        FakeContainer container = new FakeContainer();

//        회원가입
        Long memberId = container.memberService.register(new MemberRegisterRequest("asdf@asdf.com", "asdf1234!@", "닉네임"));

//        모임 생성
        Long moimId = container.moimService.register(
                new MoimRegisterRequest("모임", "모임 소개", "mixed", true),
                new MoimTagRequest(),
                List.of(
                        new ApplicationQuestionRegisterRequest("질문1", 1),
                        new ApplicationQuestionRegisterRequest("질문2", 2)
                ),
                null,
                memberId
        );

//        회원 생성
        Long memberIdToBeJoined = container.memberService.register(new MemberRegisterRequest("a@a.com", "asdf1234!@", MEMBER_NICKNAME_TO_BE_JOINED));

//        지원서 제출
        container.applicationService.register(
                moimId,
                new ApplicationRequest(List.of(new ApplicationDTO("답변1", 1), new ApplicationDTO("답변2", 2))),
                memberIdToBeJoined
        );

//        검증
        assertThatThrownBy(() ->
                container.applicationService.register(
                        moimId,
                        new ApplicationRequest(List.of(new ApplicationDTO("답변1", 1), new ApplicationDTO("답변2", 2))),
                        memberIdToBeJoined
                )
        )
                .isInstanceOf(ApplicationException.class)
                .hasMessage(APPLICATION_ALREADY_EXIST.getMessage());
    }

    @Test
    @DisplayName("지원서 등록 실패 - 답변 수가 다른 경우")
    void registerFailByNotCompletedApplicationAnswer() throws IOException {
        FakeContainer container = new FakeContainer();

//        회원가입
        Long memberId = container.memberService.register(new MemberRegisterRequest("asdf@asdf.com", "asdf1234!@", "닉네임"));

//        모임 생성
        Long moimId = container.moimService.register(
                new MoimRegisterRequest("모임", "모임 소개", "mixed", true),
                new MoimTagRequest(),
                List.of(
                        new ApplicationQuestionRegisterRequest("질문1", 1),
                        new ApplicationQuestionRegisterRequest("질문2", 2)
                ),
                null,
                memberId
        );

//        회원 생성
        Long memberIdToBeJoined = container.memberService.register(new MemberRegisterRequest("a@a.com", "asdf1234!@", MEMBER_NICKNAME_TO_BE_JOINED));

//        검증
        assertThatThrownBy(() ->
                container.applicationService.register(
                        moimId,
                        new ApplicationRequest(List.of(new ApplicationDTO("답변1", 1))),
                        memberIdToBeJoined
                )
        )
                .isInstanceOf(ApplicationException.class)
                .hasMessage(APPLICATION_ANSWER_NOT_COMPLETE.getMessage());
    }

    @Test
    @DisplayName("지원서 등록 실패 - 답변을 하지 않은 질문이 있을 경우")
    void registerFailByNotCompletedApplicationAnswer2() throws IOException {
        FakeContainer container = new FakeContainer();

//        회원가입
        Long memberId = container.memberService.register(new MemberRegisterRequest("asdf@asdf.com", "asdf1234!@", "닉네임"));

//        모임 생성
        Long moimId = container.moimService.register(
                new MoimRegisterRequest("모임", "모임 소개", "mixed", true),
                new MoimTagRequest(),
                List.of(
                        new ApplicationQuestionRegisterRequest("질문1", 1),
                        new ApplicationQuestionRegisterRequest("질문2", 2)
                ),
                null,
                memberId
        );

//        회원 생성
        Long memberIdToBeJoined = container.memberService.register(new MemberRegisterRequest("a@a.com", "asdf1234!@", MEMBER_NICKNAME_TO_BE_JOINED));

//        검증
        assertThatThrownBy(() ->
                container.applicationService.register(
                        moimId,
                        new ApplicationRequest(List.of(new ApplicationDTO("답변1", 1), new ApplicationDTO("", 2))),
                        memberIdToBeJoined
                )
        )
                .isInstanceOf(ApplicationException.class)
                .hasMessage(APPLICATION_ANSWER_NOT_COMPLETE.getMessage());
    }

    @Test
    @DisplayName("특정 회원이 제출한 모든 지원서 찾기")
    void getApplication() throws IOException {
        FakeContainer container = new FakeContainer();

//        회원가입
        Long memberId = container.memberService.register(new MemberRegisterRequest("asdf@asdf.com", "asdf1234!@", "닉네임"));

//        모임 생성
        Long moimId = container.moimService.register(
                new MoimRegisterRequest("모임", "모임 소개", "mixed", true),
                new MoimTagRequest(),
                List.of(
                        new ApplicationQuestionRegisterRequest("질문1", 1),
                        new ApplicationQuestionRegisterRequest("질문2", 2)
                ),
                null,
                memberId
        );

//        회원 생성
        Long memberIdToBeJoined = container.memberService.register(new MemberRegisterRequest("a@a.com", "asdf1234!@", MEMBER_NICKNAME_TO_BE_JOINED));

//        거절된 지원서 생성
        Member memberToBeJoined = container.memberRepository.findById(memberIdToBeJoined)
                .orElseThrow(() -> new ApplicationException(MEMBER_NOT_FOUND));

        Moim moim = container.moimRepository.findById(moimId)
                .orElseThrow(() -> new ApplicationException(MOIM_NOT_FOUND));

        Application refusedApplication = Application.builder()
                .member(memberToBeJoined)
                .moim(moim)
                .status(ApplicationStatus.REFUSE)
                .build();

        container.applicationRepository.save(refusedApplication);

//        대기중인 지원서 생성
        container.applicationService.register(
                moimId,
                new ApplicationRequest(List.of(new ApplicationDTO("답변1", 1), new ApplicationDTO("답변2", 2))),
                memberIdToBeJoined
        );

//        특정 회원이 특정 모임에 제출한 모든 지원서 찾기
        List<ApplicationResponse> applicationList = container.applicationService.getApplication(moimId, memberIdToBeJoined, "all");

//        검증
        assertAll(
                () -> assertThat(applicationList)
                        .extracting(ApplicationResponse::getApplicantName)
                        .containsExactlyInAnyOrder(MEMBER_NICKNAME_TO_BE_JOINED, MEMBER_NICKNAME_TO_BE_JOINED),
                () -> assertThat(applicationList)
                        .extracting(ApplicationResponse::getStatus)
                        .containsExactlyInAnyOrder(ApplicationStatus.WAIT, ApplicationStatus.REFUSE)
        );
    }

    @Test
    @DisplayName("특정 회원의 합격한 지원서 찾기")
    void getApplicationAccept() throws IOException {
        FakeContainer container = new FakeContainer();

//        회원가입
        Long memberId = container.memberService.register(new MemberRegisterRequest("asdf@asdf.com", "asdf1234!@", "닉네임"));

//        모임 생성
        Long moimId = container.moimService.register(
                new MoimRegisterRequest("모임", "모임 소개", "mixed", true),
                new MoimTagRequest(),
                List.of(
                        new ApplicationQuestionRegisterRequest("질문1", 1),
                        new ApplicationQuestionRegisterRequest("질문2", 2)
                ),
                null,
                memberId
        );

//        회원 생성
        Long memberIdToBeJoined = container.memberService.register(new MemberRegisterRequest("a@a.com", "asdf1234!@", MEMBER_NICKNAME_TO_BE_JOINED));

//        합격한 지원서 생성
        Member memberToBeJoined = container.memberRepository.findById(memberIdToBeJoined)
                .orElseThrow(() -> new ApplicationException(MEMBER_NOT_FOUND));

        Moim moim = container.moimRepository.findById(moimId)
                .orElseThrow(() -> new ApplicationException(MOIM_NOT_FOUND));

        Application application = Application.builder()
                .member(memberToBeJoined)
                .moim(moim)
                .status(ApplicationStatus.ACCEPT)
                .build();
        container.applicationRepository.save(application);

//        특정 회원이 특정 모임에 제출한 모든 지원서 찾기
        List<ApplicationResponse> applicationList = container.applicationService.getApplication(moimId, memberIdToBeJoined, "accept");

//        검증
        assertAll(
                () -> assertThat(applicationList)
                        .extracting(ApplicationResponse::getApplicantName)
                        .containsExactlyInAnyOrder(MEMBER_NICKNAME_TO_BE_JOINED),
                () -> assertThat(applicationList)
                        .extracting(ApplicationResponse::getStatus)
                        .containsExactlyInAnyOrder(ApplicationStatus.ACCEPT)
        );
    }

    @Test
    @DisplayName("특정 회원의 거절된 지원서 찾기")
    void getApplicationRefuse() throws IOException {
        FakeContainer container = new FakeContainer();

//        회원가입
        Long memberId = container.memberService.register(new MemberRegisterRequest("asdf@asdf.com", "asdf1234!@", "닉네임"));

//        모임 생성
        Long moimId = container.moimService.register(
                new MoimRegisterRequest("모임", "모임 소개", "mixed", true),
                new MoimTagRequest(),
                List.of(
                        new ApplicationQuestionRegisterRequest("질문1", 1),
                        new ApplicationQuestionRegisterRequest("질문2", 2)
                ),
                null,
                memberId
        );

//        회원 생성
        Long memberIdToBeJoined = container.memberService.register(new MemberRegisterRequest("a@a.com", "asdf1234!@", MEMBER_NICKNAME_TO_BE_JOINED));

//        거절된 지원서 생성
        Member memberToBeJoined = container.memberRepository.findById(memberIdToBeJoined)
                .orElseThrow(() -> new ApplicationException(MEMBER_NOT_FOUND));

        Moim moim = container.moimRepository.findById(moimId)
                .orElseThrow(() -> new ApplicationException(MOIM_NOT_FOUND));

        Application application = Application.builder()
                .member(memberToBeJoined)
                .moim(moim)
                .status(ApplicationStatus.REFUSE)
                .build();
        container.applicationRepository.save(application);

//        거절된 지원서 획득
        List<ApplicationResponse> applicationList = container.applicationService.getApplication(moimId, memberIdToBeJoined, "refuse");

//        검증
        assertAll(
                () -> assertThat(applicationList)
                        .extracting(ApplicationResponse::getApplicantName)
                        .containsExactlyInAnyOrder(MEMBER_NICKNAME_TO_BE_JOINED),
                () -> assertThat(applicationList)
                        .extracting(ApplicationResponse::getStatus)
                        .containsExactlyInAnyOrder(ApplicationStatus.REFUSE)
        );
    }

    @Test
    @DisplayName("모든 지원서 찾기")
    void getAllApplication() throws IOException {
        FakeContainer container = new FakeContainer();

//        회원가입
        Long memberId = container.memberService.register(new MemberRegisterRequest("asdf@asdf.com", "asdf1234!@", "닉네임"));

//        모임 생성
        Long moimId = container.moimService.register(
                new MoimRegisterRequest("모임", "모임 소개", "mixed", true),
                new MoimTagRequest(),
                List.of(
                        new ApplicationQuestionRegisterRequest("질문1", 1),
                        new ApplicationQuestionRegisterRequest("질문2", 2)
                ),
                null,
                memberId
        );

//        회원 생성
        Long memberIdToBeJoined = container.memberService.register(new MemberRegisterRequest("a@a.com", "asdf1234!@", MEMBER_NICKNAME_TO_BE_JOINED));

        Moim moim = container.moimRepository.findById(moimId)
                .orElseThrow(() -> new ApplicationException(MOIM_NOT_FOUND));

//        WAIT 지원서 생성
        container.applicationService.register(
                moimId,
                new ApplicationRequest(List.of(new ApplicationDTO("답변1", 1), new ApplicationDTO("답변2", 2))),
                memberIdToBeJoined
        );

//        REFUSE 지원서 생성
        Long memberId2 = container.memberService.register(new MemberRegisterRequest("b@b.com", "asdf1234!@", "닉네임2"));
        Member member2 = container.memberRepository.findById(memberId2)
                .orElseThrow(() -> new ApplicationException(MEMBER_NOT_FOUND));
        Application refusedApplication = Application.builder()
                .moim(moim)
                .member(member2)
                .status(ApplicationStatus.REFUSE)
                .build();

//        ACCEPT 지원서 생성
        Long memberId3 = container.memberService.register(new MemberRegisterRequest("c@c.com", "asdf1234!@", "닉네임3"));
        Member member3 = container.memberRepository.findById(memberId3)
                .orElseThrow(() -> new ApplicationException(MEMBER_NOT_FOUND));
        Application acceptedApplication = Application.builder()
                .moim(moim)
                .member(member3)
                .status(ApplicationStatus.ACCEPT)
                .build();

        container.applicationRepository.save(refusedApplication);
        container.applicationRepository.save(acceptedApplication);

//        모든 지원서 획득
        List<ApplicationResponse> applicationList = container.applicationService.getApplication(moimId, "all");

//        검증
        assertAll(
                () -> assertThat(applicationList)
                        .extracting(ApplicationResponse::getApplicantName)
                        .containsExactlyInAnyOrder(MEMBER_NICKNAME_TO_BE_JOINED, "닉네임2", "닉네임3"),
                () -> assertThat(applicationList)
                        .extracting(ApplicationResponse::getStatus)
                        .containsExactlyInAnyOrder(ApplicationStatus.WAIT, ApplicationStatus.REFUSE, ApplicationStatus.ACCEPT)
        );
    }

    @Test
    @DisplayName("지원서 디테일 정보 획득 성공")
    void getDetailedApplication() throws IOException {
        FakeContainer container = new FakeContainer();

//        회원가입
        Long memberId = container.memberService.register(new MemberRegisterRequest("asdf@asdf.com", "asdf1234!@", "닉네임"));

//        모임 생성
        Long moimId = container.moimService.register(
                new MoimRegisterRequest("모임", "모임 소개", "mixed", true),
                new MoimTagRequest(),
                List.of(
                        new ApplicationQuestionRegisterRequest("질문1", 1),
                        new ApplicationQuestionRegisterRequest("질문2", 2)
                ),
                null,
                memberId
        );

//        회원 생성
        Long memberIdToBeJoined = container.memberService.register(new MemberRegisterRequest("a@a.com", "asdf1234!@", MEMBER_NICKNAME_TO_BE_JOINED));

//        지원서 제출
        Long applicationId = container.applicationService.register(
                moimId,
                new ApplicationRequest(List.of(new ApplicationDTO("답변1", 1), new ApplicationDTO("답변2", 2))),
                memberIdToBeJoined
        );

//        지원서 디테일 정보 가져오기
        DetailedApplicationResponse detailedApplication = container.applicationService.getDetailedApplication(applicationId);

//        검증
        assertAll(
                () -> assertThat(detailedApplication.getStatus()).isEqualTo(ApplicationStatus.WAIT),
                () -> assertThat(detailedApplication.getApplicationAnswerDTOList())
                        .extracting(ApplicationAnswerDTO::getAnswer)
                        .containsExactlyInAnyOrder("답변1", "답변2"),
                () -> assertThat(detailedApplication.getApplicationAnswerDTOList())
                        .extracting(ApplicationAnswerDTO::getQuestion)
                        .containsExactlyInAnyOrder("질문1", "질문2")
        );
    }

    @Test
    @DisplayName("지원서 디테일 정보 획득 실패 - 올바르지 않은 지원서 Id")
    void getDetailedApplicationFailByNotValidApplicationId() throws IOException {
        FakeContainer container = new FakeContainer();

//        회원가입
        Long memberId = container.memberService.register(new MemberRegisterRequest("asdf@asdf.com", "asdf1234!@", "닉네임"));

//        모임 생성
        Long moimId = container.moimService.register(
                new MoimRegisterRequest("모임", "모임 소개", "mixed", true),
                new MoimTagRequest(),
                List.of(
                        new ApplicationQuestionRegisterRequest("질문1", 1),
                        new ApplicationQuestionRegisterRequest("질문2", 2)
                ),
                null,
                memberId
        );

//        회원 생성
        Long memberIdToBeJoined = container.memberService.register(new MemberRegisterRequest("a@a.com", "asdf1234!@", MEMBER_NICKNAME_TO_BE_JOINED));

//        지원서 제출
        container.applicationService.register(
                moimId,
                new ApplicationRequest(List.of(new ApplicationDTO("답변1", 1), new ApplicationDTO("답변2", 2))),
                memberIdToBeJoined
        );

//        검증
        assertThatThrownBy(() ->
                container.applicationService.getDetailedApplication(99999L)
        )
                .isInstanceOf(ApplicationException.class)
                .hasMessage(APPLICATION_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("지원서 거절 성공")
    void decideApplicationToRefuse() throws IOException {
        FakeContainer container = new FakeContainer();

//        회원가입
        Long memberId = container.memberService.register(new MemberRegisterRequest("asdf@asdf.com", "asdf1234!@", "닉네임"));

//        모임 생성
        Long moimId = container.moimService.register(
                new MoimRegisterRequest("모임", "모임 소개", "mixed", true),
                new MoimTagRequest(),
                List.of(
                        new ApplicationQuestionRegisterRequest("질문1", 1),
                        new ApplicationQuestionRegisterRequest("질문2", 2)
                ),
                null,
                memberId
        );

//        회원 생성
        Long memberIdToBeJoined = container.memberService.register(new MemberRegisterRequest("a@a.com", "asdf1234!@", MEMBER_NICKNAME_TO_BE_JOINED));

//        지원서 제출
        Long applicationId = container.applicationService.register(
                moimId,
                new ApplicationRequest(List.of(new ApplicationDTO("답변1", 1), new ApplicationDTO("답변2", 2))),
                memberIdToBeJoined
        );

//        지원서 거절
        container.applicationService.decideApplication(memberId, applicationId, ApplicationStatus.REFUSE);

//        검증
        Application application = container.applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ApplicationException(APPLICATION_NOT_FOUND));

        assertThat(application.getStatus()).isEqualTo(ApplicationStatus.REFUSE);
    }

    @Test
    @DisplayName("지원서 승인 성공")
    void decideApplicationToAccept() throws IOException {
        FakeContainer container = new FakeContainer();

//        회원가입
        Long memberId = container.memberService.register(new MemberRegisterRequest("asdf@asdf.com", "asdf1234!@", "닉네임"));

//        모임 생성
        Long moimId = container.moimService.register(
                new MoimRegisterRequest("모임", "모임 소개", "mixed", true),
                new MoimTagRequest(),
                List.of(
                        new ApplicationQuestionRegisterRequest("질문1", 1),
                        new ApplicationQuestionRegisterRequest("질문2", 2)
                ),
                null,
                memberId
        );

//        회원 생성
        Long memberIdToBeJoined = container.memberService.register(new MemberRegisterRequest("a@a.com", "asdf1234!@", MEMBER_NICKNAME_TO_BE_JOINED));

//        지원서 제출
        Long applicationId = container.applicationService.register(
                moimId,
                new ApplicationRequest(List.of(new ApplicationDTO("답변1", 1), new ApplicationDTO("답변2", 2))),
                memberIdToBeJoined
        );

//        지원서 승인
        container.applicationService.decideApplication(memberId, applicationId, ApplicationStatus.ACCEPT);

//        검증
        Application application = container.applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ApplicationException(APPLICATION_NOT_FOUND));

        MoimMember moimMember = container.moimMemberRepository.findByMoimIdAndMemberId(moimId, memberIdToBeJoined)
                .orElseThrow(() -> new ApplicationException(MOIM_MEMBER_NOT_FOUND));

        assertAll(
                () -> assertThat(application.getStatus()).isEqualTo(ApplicationStatus.ACCEPT),
                () -> assertThat(moimMember.getMoim().getId()).isEqualTo(moimId),
                () -> assertThat(moimMember.getMember().getId()).isEqualTo(memberIdToBeJoined),
                () -> assertThat(moimMember.getRole()).isEqualTo(MemberRole.USER)
        );
    }

    @Test
    @DisplayName("지원서 상태 변경 실패 - 올바르지 않은 지원서 Id")
    void decideApplicationFailByNotValidApplicationId() throws IOException {
        FakeContainer container = new FakeContainer();

//        회원가입
        Long memberId = container.memberService.register(new MemberRegisterRequest("asdf@asdf.com", "asdf1234!@", "닉네임"));

//        모임 생성
        Long moimId = container.moimService.register(
                new MoimRegisterRequest("모임", "모임 소개", "mixed", true),
                new MoimTagRequest(),
                List.of(
                        new ApplicationQuestionRegisterRequest("질문1", 1),
                        new ApplicationQuestionRegisterRequest("질문2", 2)
                ),
                null,
                memberId
        );

//        회원 생성
        Long memberIdToBeJoined = container.memberService.register(new MemberRegisterRequest("a@a.com", "asdf1234!@", MEMBER_NICKNAME_TO_BE_JOINED));

//        지원서 제출
        container.applicationService.register(
                moimId,
                new ApplicationRequest(List.of(new ApplicationDTO("답변1", 1), new ApplicationDTO("답변2", 2))),
                memberIdToBeJoined);

//        검증
        assertThatThrownBy(() ->
                container.applicationService.decideApplication(memberId, 99999L, ApplicationStatus.ACCEPT)
        )
                .isInstanceOf(ApplicationException.class)
                .hasMessage(APPLICATION_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("지원서 상태 변경 실패 - 권한이 없는 회원이 변경하려는 경우")
    void decideApplicationFailByForbidden() throws IOException {
        FakeContainer container = new FakeContainer();

//        회원가입
        Long memberId = container.memberService.register(new MemberRegisterRequest("asdf@asdf.com", "asdf1234!@", "닉네임"));

//        모임 생성
        Long moimId = container.moimService.register(
                new MoimRegisterRequest("모임", "모임 소개", "mixed", true),
                new MoimTagRequest(),
                List.of(
                        new ApplicationQuestionRegisterRequest("질문1", 1),
                        new ApplicationQuestionRegisterRequest("질문2", 2)
                ),
                null,
                memberId
        );

//        회원 생성
        Long memberIdToBeJoined = container.memberService.register(new MemberRegisterRequest("a@a.com", "asdf1234!@", MEMBER_NICKNAME_TO_BE_JOINED));

//        USER 등급의 회원 모임에 가입
        Long joinedMember = container.memberService.register(new MemberRegisterRequest("b@b.com", "asdf1234!@", "기존회원"));
        Moim moim = container.moimRepository.findById(moimId)
                .orElseThrow(() -> new ApplicationException(MOIM_NOT_FOUND));

        Member member = container.memberRepository.findById(joinedMember)
                .orElseThrow(() -> new ApplicationException(MEMBER_NOT_FOUND));

        MoimMember moimMember = MoimMember.builder()
                .moim(moim)
                .member(member)
                .role(MemberRole.USER)
                .build();
        container.moimMemberRepository.save(moimMember);

//        지원서 제출
        Long applicationId = container.applicationService.register(
                moimId,
                new ApplicationRequest(List.of(new ApplicationDTO("답변1", 1), new ApplicationDTO("답변2", 2))),
                memberIdToBeJoined
        );

//        검증
        assertThatThrownBy(() ->
                container.applicationService.decideApplication(joinedMember, applicationId, ApplicationStatus.ACCEPT)
        )
                .isInstanceOf(ApplicationException.class)
                .hasMessage(APPLICATION_FORBIDDEN.getMessage());
    }

    @Test
    @DisplayName("지원서 상태 변경 실패 - 이미 결정된 지원서의 경우")
    void decideApplicationFailByAlreadyDecided() throws IOException {
        FakeContainer container = new FakeContainer();

//        회원가입
        Long memberId = container.memberService.register(new MemberRegisterRequest("asdf@asdf.com", "asdf1234!@", "닉네임"));

//        모임 생성
        Long moimId = container.moimService.register(
                new MoimRegisterRequest("모임", "모임 소개", "mixed", true),
                new MoimTagRequest(),
                List.of(
                        new ApplicationQuestionRegisterRequest("질문1", 1),
                        new ApplicationQuestionRegisterRequest("질문2", 2)
                ),
                null,
                memberId
        );

//        회원 생성
        Long memberIdToBeJoined = container.memberService.register(new MemberRegisterRequest("a@a.com", "asdf1234!@", MEMBER_NICKNAME_TO_BE_JOINED));

//        지원서 제출
        Long applicationId = container.applicationService.register(
                moimId,
                new ApplicationRequest(List.of(new ApplicationDTO("답변1", 1), new ApplicationDTO("답변2", 2))),
                memberIdToBeJoined
        );

//        지원서 승인
        container.applicationService.decideApplication(memberId, applicationId, ApplicationStatus.ACCEPT);

//        검증
        assertThatThrownBy(() ->
                container.applicationService.decideApplication(memberId, applicationId, ApplicationStatus.WAIT)
        )
                .isInstanceOf(ApplicationException.class)
                .hasMessage(APPLICATION_STATUS_ALREADY_DECIDED.getMessage());
    }
}