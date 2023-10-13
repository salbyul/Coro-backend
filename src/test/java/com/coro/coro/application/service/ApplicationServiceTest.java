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
import com.coro.coro.application.repository.port.ApplicationRepository;
import com.coro.coro.member.domain.MemberRole;
import com.coro.coro.member.dto.request.MemberRegisterRequest;
import com.coro.coro.member.repository.port.MemberRepository;
import com.coro.coro.member.service.MemberService;
import com.coro.coro.moim.domain.MoimMember;
import com.coro.coro.moim.dto.request.MoimRegisterRequest;
import com.coro.coro.moim.repository.port.MoimMemberRepository;
import com.coro.coro.moim.repository.port.MoimRepository;
import com.coro.coro.moim.service.MoimService;
import org.junit.jupiter.api.BeforeEach;
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
class  ApplicationServiceTest {

    public static final String MEMBER_NICKNAME_TO_BE_JOIN = "가입할회원";
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

    private Long memberId;
    private Long moimId;
    private Long memberIdToBeJoin;

    @BeforeEach
    void setUp() throws IOException {
        memberId = memberService.register(new MemberRegisterRequest("asdf@asdf.com", "asdf1234!@", "닉네임"));
        moimId = moimService.register(
                new MoimRegisterRequest("모임", "모임 소개", "mixed", true),
                null,
                List.of(
                        new ApplicationQuestionRegisterRequest("질문1", 1),
                        new ApplicationQuestionRegisterRequest("질문2", 2)
                ),
                null,
                memberId
        );
        memberIdToBeJoin = memberService.register(new MemberRegisterRequest("a@a.com", "asdf1234!@", MEMBER_NICKNAME_TO_BE_JOIN));
    }

    @Test
    @DisplayName("지원서 등록")
    void register() {
        Long applicationId = applicationService.register(
                moimId,
                new ApplicationRequest(List.of(new ApplicationDTO("답변1", 1), new ApplicationDTO("답변2", 2))),
                memberIdToBeJoin
        );
        Application application = applicationRepository.findById(applicationId).get();

        assertAll(
                () -> assertThat(application.getMember()).isEqualTo(memberRepository.findById(memberIdToBeJoin).get()),
                () -> assertThat(application.getMoim()).isEqualTo(moimRepository.findById(moimId).get()),
                () -> assertThat(application.getStatus()).isEqualTo(ApplicationStatus.WAIT)
        );
    }

    @Test
    @DisplayName("지원서 등록 실패 - 올바르지 않은 모임 Id")
    void registerFailByNotValidMoimId() {
        assertThatThrownBy(() ->
                applicationService.register(
                        99999L,
                        new ApplicationRequest(List.of(new ApplicationDTO("답변1", 1), new ApplicationDTO("답변2", 2))),
                        memberIdToBeJoin
                )
        )
                .isInstanceOf(ApplicationException.class)
                .hasMessage(MOIM_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("지원서 등록 실패 - 올바르지 않은 회원 Id")
    void registerFailByNotValidMemberId() {
        assertThatThrownBy(() ->
                applicationService.register(
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
    void registerSuccessButExistRefuseApplication() {
        Application acceptedApplication = Application.builder()
                .member(memberRepository.findById(memberId).get())
                .moim(moimRepository.findById(moimId).get())
                .status(ApplicationStatus.WAIT)
                .build();
        acceptedApplication.updateStatusTo(ApplicationStatus.REFUSE);
        applicationRepository.save(acceptedApplication);

        Long applicationId = applicationService.register(
                moimId,
                new ApplicationRequest(List.of(new ApplicationDTO("답변1", 1), new ApplicationDTO("답변2", 2))),
                memberIdToBeJoin
        );

        Application application = applicationRepository.findById(applicationId).get();

        assertAll(
                () -> assertThat(application.getMember()).isEqualTo(memberRepository.findById(memberIdToBeJoin).get()),
                () -> assertThat(application.getMoim()).isEqualTo(moimRepository.findById(moimId).get()),
                () -> assertThat(application.getStatus()).isEqualTo(ApplicationStatus.WAIT)
        );
    }

    @Test
    @DisplayName("지원서 등록 실패 - 이미 가입한 모임의 경우")
    void registerSuccessByNotWaitApplication() {
        moimMemberRepository.save(
                MoimMember.builder()
                        .moim(moimRepository.findById(moimId).get())
                        .member(memberRepository.findById(memberIdToBeJoin).get())
                        .role(MemberRole.USER)
                        .build()
        );

        assertThatThrownBy(() ->
                applicationService.register(
                        moimId,
                        new ApplicationRequest(List.of(new ApplicationDTO("답변1", 1), new ApplicationDTO("답변2", 2))),
                        memberIdToBeJoin
                )
        )
                .isInstanceOf(ApplicationException.class)
                .hasMessage(APPLICATION_EXIST_MEMBER.getMessage());
    }

    @Test
    @DisplayName("지원서 등록 실패 - 이미 등록된 지원서가 있을 경우")
    void registerFailByDuplicateApplication() {
        applicationService.register(
                moimId,
                new ApplicationRequest(List.of(new ApplicationDTO("답변1", 1), new ApplicationDTO("답변2", 2))),
                memberIdToBeJoin
        );

        assertThatThrownBy(() ->
                applicationService.register(
                        moimId,
                        new ApplicationRequest(List.of(new ApplicationDTO("답변1", 1), new ApplicationDTO("답변2", 2))),
                        memberIdToBeJoin
                )
        )
                .isInstanceOf(ApplicationException.class)
                .hasMessage(APPLICATION_ALREADY_EXIST.getMessage());
    }

    @Test
    @DisplayName("지원서 등록 실패 - 답변 수가 다른 경우")
    void registerFailByNotCompletedApplicationAnswer() {
        assertThatThrownBy(() ->
                applicationService.register(
                        moimId,
                        new ApplicationRequest(List.of(new ApplicationDTO("답변1", 1))),
                        memberIdToBeJoin
                )
        )
                .isInstanceOf(ApplicationException.class)
                .hasMessage(APPLICATION_NOT_COMPLETE.getMessage());
    }

    @Test
    @DisplayName("지원서 등록 실패 - 답변을 하지 않은 질문이 있을 경우")
    void registerFailByNotCompletedApplicationAnswer2() {
        assertThatThrownBy(() ->
                applicationService.register(
                        moimId,
                        new ApplicationRequest(List.of(new ApplicationDTO("답변1", 1), new ApplicationDTO("", 2))),
                        memberIdToBeJoin
                )
        )
                .isInstanceOf(ApplicationException.class)
                .hasMessage(APPLICATION_NOT_COMPLETE.getMessage());
    }

    @Test
    @DisplayName("특정 회원이 제출한 모든 지원서 찾기")
    void getApplication() {
        applicationService.register(
                moimId,
                new ApplicationRequest(List.of(new ApplicationDTO("답변1", 1), new ApplicationDTO("답변2", 2))),
                memberIdToBeJoin
        );
        List<ApplicationResponse> applicationList = applicationService.getApplication(moimId, memberIdToBeJoin, "all");

        assertAll(
                () -> assertThat(applicationList)
                        .extracting(ApplicationResponse::getApplicantName)
                        .containsExactlyInAnyOrder(MEMBER_NICKNAME_TO_BE_JOIN),
                () -> assertThat(applicationList)
                        .extracting(ApplicationResponse::getStatus)
                        .containsExactlyInAnyOrder(ApplicationStatus.WAIT)
        );
    }

    @Test
    @DisplayName("특정 회원의 합격한 지원서 찾기")
    void getApplicationAccept() {
        Application application = Application.builder()
                .member(memberRepository.findById(memberIdToBeJoin).get())
                .moim(moimRepository.findById(moimId).get())
                .status(ApplicationStatus.WAIT)
                .build();
        application.updateStatusTo(ApplicationStatus.ACCEPT);
        applicationRepository.save(application);

        List<ApplicationResponse> applicationList = applicationService.getApplication(moimId, memberIdToBeJoin, "accept");

        assertAll(
                () -> assertThat(applicationList)
                        .extracting(ApplicationResponse::getApplicantName)
                        .containsExactlyInAnyOrder(MEMBER_NICKNAME_TO_BE_JOIN),
                () -> assertThat(applicationList)
                        .extracting(ApplicationResponse::getStatus)
                        .containsExactlyInAnyOrder(ApplicationStatus.ACCEPT)
        );
    }

    @Test
    @DisplayName("특정 회원의 거절된 지원서 찾기")
    void getApplicationRefuse() {
        Application application = Application.builder()
                .member(memberRepository.findById(memberIdToBeJoin).get())
                .moim(moimRepository.findById(moimId).get())
                .status(ApplicationStatus.WAIT)
                .build();
        application.updateStatusTo(ApplicationStatus.REFUSE);
        applicationRepository.save(application);

        List<ApplicationResponse> applicationList = applicationService.getApplication(moimId, memberIdToBeJoin, "refuse");

        assertAll(
                () -> assertThat(applicationList)
                        .extracting(ApplicationResponse::getApplicantName)
                        .containsExactlyInAnyOrder(MEMBER_NICKNAME_TO_BE_JOIN),
                () -> assertThat(applicationList)
                        .extracting(ApplicationResponse::getStatus)
                        .containsExactlyInAnyOrder(ApplicationStatus.REFUSE)
        );
    }

    @Test
    @DisplayName("모든 지원서 찾기")
    void getAllApplication() {
        applicationService.register(
                moimId,
                new ApplicationRequest(List.of(new ApplicationDTO("답변1", 1), new ApplicationDTO("답변2", 2))),
                memberIdToBeJoin
        );

        List<ApplicationResponse> applicationList = applicationService.getApplication(moimId, "all");

        assertAll(
                () -> assertThat(applicationList)
                        .extracting(ApplicationResponse::getApplicantName)
                        .containsExactlyInAnyOrder(MEMBER_NICKNAME_TO_BE_JOIN),
                () -> assertThat(applicationList)
                        .extracting(ApplicationResponse::getStatus)
                        .containsExactlyInAnyOrder(ApplicationStatus.WAIT)
        );
    }

    @Test
    @DisplayName("모든 거절된 지원서 찾기")
    void getAllApplicationRefuse() {
        Application application = Application.builder()
                .member(memberRepository.findById(memberIdToBeJoin).get())
                .moim(moimRepository.findById(moimId).get())
                .status(ApplicationStatus.WAIT)
                .build();
        application.updateStatusTo(ApplicationStatus.REFUSE);
        applicationRepository.save(application);

        List<ApplicationResponse> applicationList = applicationService.getApplication(moimId, "refuse");

        assertAll(
                () -> assertThat(applicationList)
                        .extracting(ApplicationResponse::getApplicantName)
                        .containsExactlyInAnyOrder(MEMBER_NICKNAME_TO_BE_JOIN),
                () -> assertThat(applicationList)
                        .extracting(ApplicationResponse::getStatus)
                        .containsExactlyInAnyOrder(ApplicationStatus.REFUSE)
        );
    }

    @Test
    @DisplayName("모든 승인된 지원서 찾기")
    void getAllApplicationAccept() {
        Application application = Application.builder()
                .member(memberRepository.findById(memberIdToBeJoin).get())
                .moim(moimRepository.findById(moimId).get())
                .status(ApplicationStatus.WAIT)
                .build();
        application.updateStatusTo(ApplicationStatus.ACCEPT);
        applicationRepository.save(application);

        List<ApplicationResponse> applicationList = applicationService.getApplication(moimId, "accept");

        assertAll(
                () -> assertThat(applicationList)
                        .extracting(ApplicationResponse::getApplicantName)
                        .containsExactlyInAnyOrder(MEMBER_NICKNAME_TO_BE_JOIN),
                () -> assertThat(applicationList)
                        .extracting(ApplicationResponse::getStatus)
                        .containsExactlyInAnyOrder(ApplicationStatus.ACCEPT)
        );
    }

    @Test
    @DisplayName("지원서 디테일 정보 획득 성공")
    void getDetailedApplication() {
        Long applicationId = applicationService.register(
                moimId,
                new ApplicationRequest(List.of(new ApplicationDTO("답변1", 1), new ApplicationDTO("답변2", 2))),
                memberIdToBeJoin
        );

        DetailedApplicationResponse detailedApplication = applicationService.getDetailedApplication(applicationId);

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
    void getDetailedApplicationFailByNotValidApplicationId() {
        applicationService.register(
                moimId,
                new ApplicationRequest(List.of(new ApplicationDTO("답변1", 1), new ApplicationDTO("답변2", 2))),
                memberIdToBeJoin
        );

        assertThatThrownBy(() ->
                applicationService.getDetailedApplication(99999L)
        )
                .isInstanceOf(ApplicationException.class)
                .hasMessage(APPLICATION_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("지원서 거절 성공")
    void decideApplicationToRefuse() {
        Long applicationId = applicationService.register(
                moimId,
                new ApplicationRequest(List.of(new ApplicationDTO("답변1", 1), new ApplicationDTO("답변2", 2))),
                memberIdToBeJoin
        );
        applicationService.decideApplication(memberId, applicationId, ApplicationStatus.REFUSE);

        Application application = applicationRepository.findById(applicationId).get();

        assertThat(application.getStatus()).isEqualTo(ApplicationStatus.REFUSE);
    }

    @Test
    @DisplayName("지원서 승인 성공")
    void decideApplicationToAccept() {
        Long applicationId = applicationService.register(
                moimId,
                new ApplicationRequest(List.of(new ApplicationDTO("답변1", 1), new ApplicationDTO("답변2", 2))),
                memberIdToBeJoin
        );
        applicationService.decideApplication(memberId, applicationId, ApplicationStatus.ACCEPT);

        Application application = applicationRepository.findById(applicationId).get();
        MoimMember moimMember = moimMemberRepository.findByMoimIdAndMemberId(moimId, memberIdToBeJoin).get();

        assertAll(
                () -> assertThat(application.getStatus()).isEqualTo(ApplicationStatus.ACCEPT),
                () -> assertThat(moimMember.getMoim().getId()).isEqualTo(moimId),
                () -> assertThat(moimMember.getMember().getId()).isEqualTo(memberIdToBeJoin),
                () -> assertThat(moimMember.getRole()).isEqualTo(MemberRole.USER)
        );
    }

    @Test
    @DisplayName("지원서 상태 변경 실패 - 올바르지 않은 지원서 Id")
    void decideApplicationFailByNotValidApplicationId() {
        applicationService.register(
                moimId,
                new ApplicationRequest(List.of(new ApplicationDTO("답변1", 1), new ApplicationDTO("답변2", 2))),
                memberIdToBeJoin);

        assertThatThrownBy(() ->
                applicationService.decideApplication(memberId, 99999L, ApplicationStatus.ACCEPT)
        )
                .isInstanceOf(ApplicationException.class)
                .hasMessage(APPLICATION_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("지원서 상태 변경 실패 - 권한이 없는 회원이 변경하려는 경우")
    void decideApplicationFailByForbidden() {
        Long joinedMember = memberService.register(new MemberRegisterRequest("b@b.com", "asdf1234!@", "기존회원"));
        MoimMember moimMember = MoimMember.builder()
                .moim(moimRepository.findById(moimId).get())
                .member(memberRepository.findById(joinedMember).get())
                .role(MemberRole.USER)
                .build();
        moimMemberRepository.save(moimMember);

        Long applicationId = applicationService.register(
                moimId,
                new ApplicationRequest(List.of(new ApplicationDTO("답변1", 1), new ApplicationDTO("답변2", 2))),
                memberIdToBeJoin
        );

        assertThatThrownBy(() ->
                applicationService.decideApplication(joinedMember, applicationId, ApplicationStatus.ACCEPT)
        )
                .isInstanceOf(ApplicationException.class)
                .hasMessage(APPLICATION_FORBIDDEN.getMessage());
    }

    @Test
    @DisplayName("지원서 상태 변경 실패 - 이미 결정된 지원서의 경우")
    void decideApplicationFailByAlreadyDecided() {
        Long applicationId = applicationService.register(
                moimId,
                new ApplicationRequest(List.of(new ApplicationDTO("답변1", 1), new ApplicationDTO("답변2", 2))),
                memberIdToBeJoin
        );

        applicationService.decideApplication(memberId, applicationId, ApplicationStatus.ACCEPT);

        assertThatThrownBy(() ->
                applicationService.decideApplication(memberId, applicationId, ApplicationStatus.WAIT)
        )
                .isInstanceOf(ApplicationException.class)
                .hasMessage(APPLICATION_STATUS_ALREADY.getMessage());
    }
}