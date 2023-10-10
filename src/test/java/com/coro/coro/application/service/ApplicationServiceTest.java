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
import com.coro.coro.application.repository.ApplicationRepository;
import com.coro.coro.member.domain.MemberRole;
import com.coro.coro.member.dto.request.MemberRegisterRequest;
import com.coro.coro.member.repository.MemberRepository;
import com.coro.coro.member.service.MemberService;
import com.coro.coro.moim.domain.MoimMember;
import com.coro.coro.moim.dto.request.MoimRegisterRequest;
import com.coro.coro.moim.repository.MoimMemberRepository;
import com.coro.coro.moim.repository.MoimRepository;
import com.coro.coro.moim.service.MoimService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;

import static com.coro.coro.common.response.error.ErrorType.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertAll;

@Transactional
@SpringBootTest
class ApplicationServiceTest {

    @Autowired
    private MemberService memberService;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private MoimService moimService;
    @Autowired
    private MoimRepository moimRepository;
    @Autowired
    private ApplicationService applicationService;
    @Autowired
    private ApplicationRepository applicationRepository;
    @Autowired
    private MoimMemberRepository moimMemberRepository;

    @Test
    @DisplayName("지원서 등록")
    void register() throws IOException {
        Long memberId = memberService.register(new MemberRegisterRequest("asdf@asdf.com", "asdf1234!@", "닉네임"));
        Long moimId = moimService.register(
                new MoimRegisterRequest("모임", "모임 소개", "mixed", true),
                null,
                List.of(new ApplicationQuestionRegisterRequest("질문1", 1), new ApplicationQuestionRegisterRequest("질문2", 2)),
                null,
                memberId);
        Long willJoinedMemberId = memberService.register(new MemberRegisterRequest("a@a.com", "asdf1234!@", "가입할회원"));
        Long applicationId = applicationService.register(moimId, new ApplicationRequest(List.of(new ApplicationDTO("답변1", 1), new ApplicationDTO("답변2", 2))), willJoinedMemberId);
        Application application = applicationRepository.findById(applicationId).get();

        assertAll(
                () -> assertThat(application.getMember()).isEqualTo(memberRepository.findById(willJoinedMemberId).get()),
                () -> assertThat(application.getMoim()).isEqualTo(moimRepository.findById(moimId).get()),
                () -> assertThat(application.getStatus()).isEqualTo(ApplicationStatus.WAIT)
        );
    }

    @Test
    @DisplayName("지원서 등록 실패 - 올바르지 않은 모임 Id")
    void registerFailByNotValidMoimId() throws IOException {
        Long memberId = memberService.register(new MemberRegisterRequest("asdf@asdf.com", "asdf1234!@", "닉네임"));
        moimService.register(
                new MoimRegisterRequest("모임", "모임 소개", "mixed", true),
                null,
                List.of(new ApplicationQuestionRegisterRequest("질문1", 1), new ApplicationQuestionRegisterRequest("질문2", 2)),
                null,
                memberId);
        Long willJoinedMemberId = memberService.register(new MemberRegisterRequest("a@a.com", "asdf1234!@", "가입할회원"));

        assertThatThrownBy(() -> {
            applicationService.register(99999L, new ApplicationRequest(List.of(new ApplicationDTO("답변1", 1), new ApplicationDTO("답변2", 2))), willJoinedMemberId);
        }).isInstanceOf(ApplicationException.class)
                .hasMessage(MOIM_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("지원서 등록 실패 - 올바르지 않은 회원 Id")
    void registerFailByNotValidMemberId() throws IOException {
        Long memberId = memberService.register(new MemberRegisterRequest("asdf@asdf.com", "asdf1234!@", "닉네임"));
        Long moimId = moimService.register(
                new MoimRegisterRequest("모임", "모임 소개", "mixed", true),
                null,
                List.of(new ApplicationQuestionRegisterRequest("질문1", 1), new ApplicationQuestionRegisterRequest("질문2", 2)),
                null,
                memberId);
        memberService.register(new MemberRegisterRequest("a@a.com", "asdf1234!@", "가입할회원"));

        assertThatThrownBy(() -> {
            applicationService.register(moimId, new ApplicationRequest(List.of(new ApplicationDTO("답변1", 1), new ApplicationDTO("답변2", 2))), 99999L);
        }).isInstanceOf(ApplicationException.class)
                .hasMessage(MEMBER_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("지원서 등록 성공 - 거절된 지원서가 있을 경우")
    void registerSuccessButExistRefuseApplication() throws IOException {
        Long memberId = memberService.register(new MemberRegisterRequest("asdf@asdf.com", "asdf1234!@", "닉네임"));
        Long moimId = moimService.register(
                new MoimRegisterRequest("모임", "모임 소개", "mixed", true),
                null,
                List.of(new ApplicationQuestionRegisterRequest("질문1", 1), new ApplicationQuestionRegisterRequest("질문2", 2)),
                null,
                memberId);
        Long willJoinedMemberId = memberService.register(new MemberRegisterRequest("a@a.com", "asdf1234!@", "가입할회원"));
        Long applicationId = applicationService.register(moimId, new ApplicationRequest(List.of(new ApplicationDTO("답변1", 1), new ApplicationDTO("답변2", 2))), willJoinedMemberId);
        Application acceptedApplication = Application.generate(memberRepository.findById(memberId).get(), moimRepository.findById(moimId).get());
        acceptedApplication.updateStatusTo(ApplicationStatus.REFUSE);
        applicationRepository.save(acceptedApplication);

        Application application = applicationRepository.findById(applicationId).get();

        assertAll(
                () -> assertThat(application.getMember()).isEqualTo(memberRepository.findById(willJoinedMemberId).get()),
                () -> assertThat(application.getMoim()).isEqualTo(moimRepository.findById(moimId).get()),
                () -> assertThat(application.getStatus()).isEqualTo(ApplicationStatus.WAIT)
        );
    }

    @Test
    @DisplayName("지원서 등록 실패 - 이미 가입한 모임의 경우")
    void registerSuccessByNotWaitApplication() throws IOException {
        Long memberId = memberService.register(new MemberRegisterRequest("asdf@asdf.com", "asdf1234!@", "닉네임"));
        Long moimId = moimService.register(
                new MoimRegisterRequest("모임", "모임 소개", "mixed", true),
                null,
                List.of(new ApplicationQuestionRegisterRequest("질문1", 1), new ApplicationQuestionRegisterRequest("질문2", 2)),
                null,
                memberId);
        Long willJoinedMemberId = memberService.register(new MemberRegisterRequest("a@a.com", "asdf1234!@", "가입할회원"));
        moimMemberRepository.save(MoimMember.generate(moimRepository.findById(moimId).get(), memberRepository.findById(willJoinedMemberId).get(), MemberRole.USER));
        assertThatThrownBy(() -> {
            applicationService.register(moimId, new ApplicationRequest(List.of(new ApplicationDTO("답변1", 1), new ApplicationDTO("답변2", 2))), willJoinedMemberId);
        }).isInstanceOf(ApplicationException.class)
                .hasMessage(APPLICATION_EXIST_MEMBER.getMessage());
    }

    @Test
    @DisplayName("지원서 등록 실패 - 이미 등록된 지원서가 있을 경우")
    void registerFailByDuplicateApplication() throws IOException {
        Long memberId = memberService.register(new MemberRegisterRequest("asdf@asdf.com", "asdf1234!@", "닉네임"));
        Long moimId = moimService.register(
                new MoimRegisterRequest("모임", "모임 소개", "mixed", true),
                null,
                List.of(new ApplicationQuestionRegisterRequest("질문1", 1), new ApplicationQuestionRegisterRequest("질문2", 2)),
                null,
                memberId);
        Long willJoinedMemberId = memberService.register(new MemberRegisterRequest("a@a.com", "asdf1234!@", "가입할회원"));
        applicationService.register(moimId, new ApplicationRequest(List.of(new ApplicationDTO("답변1", 1), new ApplicationDTO("답변2", 2))), willJoinedMemberId);

        assertThatThrownBy(() -> {
            applicationService.register(moimId, new ApplicationRequest(List.of(new ApplicationDTO("답변1", 1), new ApplicationDTO("답변2", 2))), willJoinedMemberId);
        }).isInstanceOf(ApplicationException.class)
                .hasMessage(APPLICATION_EXIST.getMessage());
    }

    @Test
    @DisplayName("지원서 등록 실패 - 답변 수가 다른 경우")
    void registerFailByNotCompletedApplicationAnswer() throws IOException {
        Long memberId = memberService.register(new MemberRegisterRequest("asdf@asdf.com", "asdf1234!@", "닉네임"));
        Long moimId = moimService.register(
                new MoimRegisterRequest("모임", "모임 소개", "mixed", true),
                null,
                List.of(new ApplicationQuestionRegisterRequest("질문1", 1), new ApplicationQuestionRegisterRequest("질문2", 2)),
                null,
                memberId);
        Long willJoinedMemberId = memberService.register(new MemberRegisterRequest("a@a.com", "asdf1234!@", "가입할회원"));
        assertThatThrownBy(() -> {
            applicationService.register(moimId, new ApplicationRequest(List.of(new ApplicationDTO("답변1", 1))), willJoinedMemberId);
        }).isInstanceOf(ApplicationException.class)
                .hasMessage(APPLICATION_NOT_COMPLETE.getMessage());
    }

    @Test
    @DisplayName("지원서 등록 실패 - 답변을 하지 않은 질문이 있을 경우")
    void registerFailByNotCompletedApplicationAnswer2() throws IOException {
        Long memberId = memberService.register(new MemberRegisterRequest("asdf@asdf.com", "asdf1234!@", "닉네임"));
        Long moimId = moimService.register(
                new MoimRegisterRequest("모임", "모임 소개", "mixed", true),
                null,
                List.of(new ApplicationQuestionRegisterRequest("질문1", 1), new ApplicationQuestionRegisterRequest("질문2", 2)),
                null,
                memberId);
        Long willJoinedMemberId = memberService.register(new MemberRegisterRequest("a@a.com", "asdf1234!@", "가입할회원"));
        assertThatThrownBy(() -> {
            applicationService.register(moimId, new ApplicationRequest(List.of(new ApplicationDTO("답변1", 1), new ApplicationDTO("", 2))), willJoinedMemberId);
        }).isInstanceOf(ApplicationException.class)
                .hasMessage(APPLICATION_NOT_COMPLETE.getMessage());
    }

    @Test
    @DisplayName("특정 회원이 제출한 모든 지원서 찾기")
    void getApplication() throws IOException {
        Long memberId = memberService.register(new MemberRegisterRequest("asdf@asdf.com", "asdf1234!@", "닉네임"));
        Long moimId = moimService.register(
                new MoimRegisterRequest("모임", "모임 소개", "mixed", true),
                null,
                List.of(new ApplicationQuestionRegisterRequest("질문1", 1), new ApplicationQuestionRegisterRequest("질문2", 2)),
                null,
                memberId);
        Long willJoinedMemberId = memberService.register(new MemberRegisterRequest("a@a.com", "asdf1234!@", "가입할회원"));
        applicationService.register(moimId, new ApplicationRequest(List.of(new ApplicationDTO("답변1", 1), new ApplicationDTO("답변2", 2))), willJoinedMemberId);
        List<ApplicationResponse> applicationList = applicationService.getApplication(moimId, willJoinedMemberId, "all");

        assertAll(
                () -> assertThat(applicationList).extracting(ApplicationResponse::getApplicantName).containsExactlyInAnyOrder("가입할회원"),
                () -> assertThat(applicationList).extracting(ApplicationResponse::getStatus).containsExactlyInAnyOrder(ApplicationStatus.WAIT)
        );
    }

    @Test
    @DisplayName("특정 회원의 합격한 지원서 찾기")
    void getApplicationAccept() throws IOException {
        Long memberId = memberService.register(new MemberRegisterRequest("asdf@asdf.com", "asdf1234!@", "닉네임"));
        Long moimId = moimService.register(
                new MoimRegisterRequest("모임", "모임 소개", "mixed", true),
                null,
                List.of(new ApplicationQuestionRegisterRequest("질문1", 1), new ApplicationQuestionRegisterRequest("질문2", 2)),
                null,
                memberId);
        Long willJoinedMemberId = memberService.register(new MemberRegisterRequest("a@a.com", "asdf1234!@", "가입할회원"));
        applicationService.register(moimId, new ApplicationRequest(List.of(new ApplicationDTO("답변1", 1), new ApplicationDTO("답변2", 2))), willJoinedMemberId);
        Application application = Application.generate(memberRepository.findById(willJoinedMemberId).get(), moimRepository.findById(moimId).get());
        application.updateStatusTo(ApplicationStatus.ACCEPT);
        applicationRepository.save(application);
        List<ApplicationResponse> applicationList = applicationService.getApplication(moimId, willJoinedMemberId, "accept");

        assertAll(
                () -> assertThat(applicationList).extracting(ApplicationResponse::getApplicantName).containsExactlyInAnyOrder("가입할회원"),
                () -> assertThat(applicationList).extracting(ApplicationResponse::getStatus).containsExactlyInAnyOrder(ApplicationStatus.ACCEPT)
        );
    }

    @Test
    @DisplayName("특정 회원의 거절된 지원서 찾기")
    void getApplicationRefuse() throws IOException {
        Long memberId = memberService.register(new MemberRegisterRequest("asdf@asdf.com", "asdf1234!@", "닉네임"));
        Long moimId = moimService.register(
                new MoimRegisterRequest("모임", "모임 소개", "mixed", true),
                null,
                List.of(new ApplicationQuestionRegisterRequest("질문1", 1), new ApplicationQuestionRegisterRequest("질문2", 2)),
                null,
                memberId);
        Long willJoinedMemberId = memberService.register(new MemberRegisterRequest("a@a.com", "asdf1234!@", "가입할회원"));
        applicationService.register(moimId, new ApplicationRequest(List.of(new ApplicationDTO("답변1", 1), new ApplicationDTO("답변2", 2))), willJoinedMemberId);
        Application application = Application.generate(memberRepository.findById(willJoinedMemberId).get(), moimRepository.findById(moimId).get());
        application.updateStatusTo(ApplicationStatus.REFUSE);
        applicationRepository.save(application);
        List<ApplicationResponse> applicationList = applicationService.getApplication(moimId, willJoinedMemberId, "refuse");

        assertAll(
                () -> assertThat(applicationList).extracting(ApplicationResponse::getApplicantName).containsExactlyInAnyOrder("가입할회원"),
                () -> assertThat(applicationList).extracting(ApplicationResponse::getStatus).containsExactlyInAnyOrder(ApplicationStatus.REFUSE)
        );
    }

    @Test
    @DisplayName("모든 지원서 찾기")
    void getAllApplication() throws IOException {
        Long memberId = memberService.register(new MemberRegisterRequest("asdf@asdf.com", "asdf1234!@", "닉네임"));
        Long moimId = moimService.register(
                new MoimRegisterRequest("모임", "모임 소개", "mixed", true),
                null,
                List.of(new ApplicationQuestionRegisterRequest("질문1", 1), new ApplicationQuestionRegisterRequest("질문2", 2)),
                null,
                memberId);
        Long willJoinedMemberId = memberService.register(new MemberRegisterRequest("a@a.com", "asdf1234!@", "가입할회원"));
        applicationService.register(moimId, new ApplicationRequest(List.of(new ApplicationDTO("답변1", 1), new ApplicationDTO("답변2", 2))), willJoinedMemberId);
        List<ApplicationResponse> applicationList = applicationService.getApplication(moimId, "all");

        assertAll(
                () -> assertThat(applicationList).extracting(ApplicationResponse::getApplicantName).containsExactlyInAnyOrder("가입할회원"),
                () -> assertThat(applicationList).extracting(ApplicationResponse::getStatus).containsExactlyInAnyOrder(ApplicationStatus.WAIT)
        );
    }

    @Test
    @DisplayName("모든 거절된 지원서 찾기")
    void getAllApplicationRefuse() throws IOException {
        Long memberId = memberService.register(new MemberRegisterRequest("asdf@asdf.com", "asdf1234!@", "닉네임"));
        Long moimId = moimService.register(
                new MoimRegisterRequest("모임", "모임 소개", "mixed", true),
                null,
                List.of(new ApplicationQuestionRegisterRequest("질문1", 1), new ApplicationQuestionRegisterRequest("질문2", 2)),
                null,
                memberId);
        Long willJoinedMemberId = memberService.register(new MemberRegisterRequest("a@a.com", "asdf1234!@", "가입할회원"));
        Application application = Application.generate(memberRepository.findById(willJoinedMemberId).get(), moimRepository.findById(moimId).get());
        application.updateStatusTo(ApplicationStatus.REFUSE);
        applicationRepository.save(application);
        List<ApplicationResponse> applicationList = applicationService.getApplication(moimId, "refuse");

        assertAll(
                () -> assertThat(applicationList).extracting(ApplicationResponse::getApplicantName).containsExactlyInAnyOrder("가입할회원"),
                () -> assertThat(applicationList).extracting(ApplicationResponse::getStatus).containsExactlyInAnyOrder(ApplicationStatus.REFUSE)
        );
    }

    @Test
    @DisplayName("모든 승인된 지원서 찾기")
    void getAllApplicationAccept() throws IOException {
        Long memberId = memberService.register(new MemberRegisterRequest("asdf@asdf.com", "asdf1234!@", "닉네임"));
        Long moimId = moimService.register(
                new MoimRegisterRequest("모임", "모임 소개", "mixed", true),
                null,
                List.of(new ApplicationQuestionRegisterRequest("질문1", 1), new ApplicationQuestionRegisterRequest("질문2", 2)),
                null,
                memberId);
        Long willJoinedMemberId = memberService.register(new MemberRegisterRequest("a@a.com", "asdf1234!@", "가입할회원"));
        Application application = Application.generate(memberRepository.findById(willJoinedMemberId).get(), moimRepository.findById(moimId).get());
        application.updateStatusTo(ApplicationStatus.ACCEPT);
        applicationRepository.save(application);
        List<ApplicationResponse> applicationList = applicationService.getApplication(moimId, "accept");

        assertAll(
                () -> assertThat(applicationList).extracting(ApplicationResponse::getApplicantName).containsExactlyInAnyOrder("가입할회원"),
                () -> assertThat(applicationList).extracting(ApplicationResponse::getStatus).containsExactlyInAnyOrder(ApplicationStatus.ACCEPT)
        );
    }

    @Test
    @DisplayName("지원서 디테일 정보 획득 성공")
    void getDetailedApplication() throws IOException {
        Long memberId = memberService.register(new MemberRegisterRequest("asdf@asdf.com", "asdf1234!@", "닉네임"));
        Long moimId = moimService.register(
                new MoimRegisterRequest("모임", "모임 소개", "mixed", true),
                null,
                List.of(new ApplicationQuestionRegisterRequest("질문1", 1), new ApplicationQuestionRegisterRequest("질문2", 2)),
                null,
                memberId);
        Long willJoinedMemberId = memberService.register(new MemberRegisterRequest("a@a.com", "asdf1234!@", "가입할회원"));
        Long applicationId = applicationService.register(moimId, new ApplicationRequest(List.of(new ApplicationDTO("답변1", 1), new ApplicationDTO("답변2", 2))), willJoinedMemberId);

        DetailedApplicationResponse detailedApplication = applicationService.getDetailedApplication(applicationId);

        assertAll(
                () -> assertThat(detailedApplication.getStatus()).isEqualTo(ApplicationStatus.WAIT),
                () -> assertThat(detailedApplication.getApplicationAnswerDTOList()).extracting(ApplicationAnswerDTO::getAnswer).containsExactlyInAnyOrder("답변1", "답변2"),
                () -> assertThat(detailedApplication.getApplicationAnswerDTOList()).extracting(ApplicationAnswerDTO::getQuestion).containsExactlyInAnyOrder("질문1", "질문2")
        );
    }

    @Test
    @DisplayName("지원서 디테일 정보 획득 실패 - 올바르지 않은 지원서 Id")
    void getDetailedApplicationFailByNotValidApplicationId() throws IOException {
        Long memberId = memberService.register(new MemberRegisterRequest("asdf@asdf.com", "asdf1234!@", "닉네임"));
        Long moimId = moimService.register(
                new MoimRegisterRequest("모임", "모임 소개", "mixed", true),
                null,
                List.of(new ApplicationQuestionRegisterRequest("질문1", 1), new ApplicationQuestionRegisterRequest("질문2", 2)),
                null,
                memberId);
        Long willJoinedMemberId = memberService.register(new MemberRegisterRequest("a@a.com", "asdf1234!@", "가입할회원"));
        applicationService.register(moimId, new ApplicationRequest(List.of(new ApplicationDTO("답변1", 1), new ApplicationDTO("답변2", 2))), willJoinedMemberId);

        assertThatThrownBy(() -> {
            applicationService.getDetailedApplication(99999L);
        }).isInstanceOf(ApplicationException.class)
                .hasMessage(APPLICATION_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("지원서 거절 성공")
    void decideApplicationToRefuse() throws IOException {
        Long memberId = memberService.register(new MemberRegisterRequest("asdf@asdf.com", "asdf1234!@", "닉네임"));
        Long moimId = moimService.register(
                new MoimRegisterRequest("모임", "모임 소개", "mixed", true),
                null,
                List.of(new ApplicationQuestionRegisterRequest("질문1", 1), new ApplicationQuestionRegisterRequest("질문2", 2)),
                null,
                memberId);
        Long willJoinedMemberId = memberService.register(new MemberRegisterRequest("a@a.com", "asdf1234!@", "가입할회원"));
        Long applicationId = applicationService.register(moimId, new ApplicationRequest(List.of(new ApplicationDTO("답변1", 1), new ApplicationDTO("답변2", 2))), willJoinedMemberId);

        applicationService.decideApplication(memberId, applicationId, ApplicationStatus.REFUSE);

        Application application = applicationRepository.findById(applicationId).get();

        assertThat(application.getStatus()).isEqualTo(ApplicationStatus.REFUSE);
    }

    @Test
    @DisplayName("지원서 승인 성공")
    void decideApplicationToAccept() throws IOException {
        Long memberId = memberService.register(new MemberRegisterRequest("asdf@asdf.com", "asdf1234!@", "닉네임"));
        Long moimId = moimService.register(
                new MoimRegisterRequest("모임", "모임 소개", "mixed", true),
                null,
                List.of(new ApplicationQuestionRegisterRequest("질문1", 1), new ApplicationQuestionRegisterRequest("질문2", 2)),
                null,
                memberId);
        Long willJoinedMemberId = memberService.register(new MemberRegisterRequest("a@a.com", "asdf1234!@", "가입할회원"));
        Long applicationId = applicationService.register(moimId, new ApplicationRequest(List.of(new ApplicationDTO("답변1", 1), new ApplicationDTO("답변2", 2))), willJoinedMemberId);

        applicationService.decideApplication(memberId, applicationId, ApplicationStatus.ACCEPT);

        Application application = applicationRepository.findById(applicationId).get();
        MoimMember moimMember = moimMemberRepository.findByMoimIdAndMemberId(moimId, willJoinedMemberId).get();

        assertAll(
                () -> assertThat(application.getStatus()).isEqualTo(ApplicationStatus.ACCEPT),
                () -> assertThat(moimMember.getMoim().getId()).isEqualTo(moimId),
                () -> assertThat(moimMember.getMember().getId()).isEqualTo(willJoinedMemberId),
                () -> assertThat(moimMember.getRole()).isEqualTo(MemberRole.USER)
        );
    }

    @Test
    @DisplayName("지원서 상태 변경 실패 - 올바르지 않은 지원서 Id")
    void decideApplicationFailByNotValidApplicationId() throws IOException {
        Long memberId = memberService.register(new MemberRegisterRequest("asdf@asdf.com", "asdf1234!@", "닉네임"));
        Long moimId = moimService.register(
                new MoimRegisterRequest("모임", "모임 소개", "mixed", true),
                null,
                List.of(new ApplicationQuestionRegisterRequest("질문1", 1), new ApplicationQuestionRegisterRequest("질문2", 2)),
                null,
                memberId);
        Long willJoinedMemberId = memberService.register(new MemberRegisterRequest("a@a.com", "asdf1234!@", "가입할회원"));
        applicationService.register(moimId, new ApplicationRequest(List.of(new ApplicationDTO("답변1", 1), new ApplicationDTO("답변2", 2))), willJoinedMemberId);

        assertThatThrownBy(() -> {
            applicationService.decideApplication(memberId, 99999L, ApplicationStatus.ACCEPT);
        }).isInstanceOf(ApplicationException.class)
                .hasMessage(APPLICATION_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("지원서 상태 변경 실패 - 권한이 없는 회원이 변경하려는 경우")
    void decideApplicationFailByForbidden() throws IOException {
        Long memberId = memberService.register(new MemberRegisterRequest("asdf@asdf.com", "asdf1234!@", "닉네임"));
        Long moimId = moimService.register(
                new MoimRegisterRequest("모임", "모임 소개", "mixed", true),
                null,
                List.of(new ApplicationQuestionRegisterRequest("질문1", 1), new ApplicationQuestionRegisterRequest("질문2", 2)),
                null,
                memberId);
        Long joinedMember = memberService.register(new MemberRegisterRequest("b@b.com", "asdf1234!@", "기존회원"));
        Long willJoinedMemberId = memberService.register(new MemberRegisterRequest("a@a.com", "asdf1234!@", "가입할회원"));
        Long applicationId = applicationService.register(moimId, new ApplicationRequest(List.of(new ApplicationDTO("답변1", 1), new ApplicationDTO("답변2", 2))), willJoinedMemberId);
        moimMemberRepository.save(MoimMember.generate(moimRepository.findById(moimId).get(), memberRepository.findById(joinedMember).get(), MemberRole.USER));

        assertThatThrownBy(() -> {
            applicationService.decideApplication(joinedMember, applicationId, ApplicationStatus.ACCEPT);
        }).isInstanceOf(ApplicationException.class)
                .hasMessage(APPLICATION_FORBIDDEN.getMessage());
    }

    @Test
    @DisplayName("지원서 상태 변경 실패 - 이미 결정된 지원서의 경우")
    void decideApplicationFailByAlreadyDecided() throws IOException {
        Long memberId = memberService.register(new MemberRegisterRequest("asdf@asdf.com", "asdf1234!@", "닉네임"));
        Long moimId = moimService.register(
                new MoimRegisterRequest("모임", "모임 소개", "mixed", true),
                null,
                List.of(new ApplicationQuestionRegisterRequest("질문1", 1), new ApplicationQuestionRegisterRequest("질문2", 2)),
                null,
                memberId);
        Long willJoinedMemberId = memberService.register(new MemberRegisterRequest("a@a.com", "asdf1234!@", "가입할회원"));
        Long applicationId = applicationService.register(moimId, new ApplicationRequest(List.of(new ApplicationDTO("답변1", 1), new ApplicationDTO("답변2", 2))), willJoinedMemberId);
        applicationService.decideApplication(memberId, applicationId, ApplicationStatus.ACCEPT);

        assertThatThrownBy(() -> {
            applicationService.decideApplication(memberId, applicationId, ApplicationStatus.ACCEPT);
        }).isInstanceOf(ApplicationException.class)
                .hasMessage(APPLICATION_STATUS_ALREADY.getMessage());
    }
}