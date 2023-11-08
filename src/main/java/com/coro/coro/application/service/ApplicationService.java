package com.coro.coro.application.service;

import com.coro.coro.application.domain.Application;
import com.coro.coro.application.domain.ApplicationAnswer;
import com.coro.coro.application.domain.ApplicationQuestion;
import com.coro.coro.application.domain.ApplicationStatus;
import com.coro.coro.application.dto.request.ApplicationDTO;
import com.coro.coro.application.dto.request.ApplicationRequest;
import com.coro.coro.application.dto.response.ApplicationResponse;
import com.coro.coro.application.dto.response.DetailedApplicationResponse;
import com.coro.coro.application.exception.ApplicationException;
import com.coro.coro.application.service.port.ApplicationAnswerRepository;
import com.coro.coro.application.service.port.ApplicationQuestionRepository;
import com.coro.coro.application.service.port.ApplicationRepository;
import com.coro.coro.member.domain.Member;
import com.coro.coro.member.domain.MemberRole;
import com.coro.coro.member.service.port.MemberRepository;
import com.coro.coro.moim.domain.Moim;
import com.coro.coro.moim.domain.MoimMember;
import com.coro.coro.moim.service.port.MoimMemberRepository;
import com.coro.coro.moim.service.port.MoimRepository;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.coro.coro.common.response.error.ErrorType.*;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Builder
@Service
public class ApplicationService {

    private static final String ALL = "all";
    private final ApplicationQuestionRepository applicationQuestionRepository;
    private final MemberRepository memberRepository;
    private final MoimRepository moimRepository;
    private final ApplicationRepository applicationRepository;
    private final ApplicationAnswerRepository applicationAnswerRepository;
    private final MoimMemberRepository moimMemberRepository;

    @Transactional
    public Long register(final Long moimId, final ApplicationRequest applicationRequest, final Long memberId) {
        Member member = getMemberById(memberId);
        Moim moim = getMoimById(moimId);
        List<ApplicationQuestion> applicationQuestionList = applicationQuestionRepository.findAllByMoimId(moimId);

        List<ApplicationDTO> applicationList = applicationRequest.getApplicationList();

        validateHasWaitApplication(moimId, memberId);
        validateExistMoimMember(moim.getId(), memberId);
        validateAnswer(applicationQuestionList, applicationList);

        Application application = Application.builder()
                .member(member)
                .moim(moim)
                .status(ApplicationStatus.WAIT)
                .build();
        Long savedApplicationId = applicationRepository.save(application);

        List<ApplicationAnswer> applicationAnswerList = applicationRequestToDomain(getApplicationById(savedApplicationId), applicationQuestionList, applicationList);
        applicationAnswerRepository.saveAll(applicationAnswerList);
        return savedApplicationId;
    }

    private Moim getMoimById(final Long moimId) {
        return moimRepository.findById(moimId)
                .orElseThrow(() -> new ApplicationException(MOIM_NOT_FOUND));
    }

    private Member getMemberById(final Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new ApplicationException(MEMBER_NOT_FOUND));
    }

    private void validateHasWaitApplication(final Long moimId, final Long memberId) {
        List<Application> existApplication = applicationRepository.findByMemberIdAndMoimId(memberId, moimId);
        for (Application application : existApplication) {
            if (application.getStatus().equals(ApplicationStatus.WAIT)) {
                throw new ApplicationException(APPLICATION_ALREADY_EXIST);
            }
        }
    }

    private void validateAnswer(final List<ApplicationQuestion> applicationQuestionList, final List<ApplicationDTO> applicationList) {
        if (applicationList.size() != applicationQuestionList.size()) {
            throw new ApplicationException(APPLICATION_ANSWER_NOT_COMPLETE);
        }

        for (ApplicationDTO applicationDTO : applicationList) {
            if (!StringUtils.hasText(applicationDTO.getContent())) {
                throw new ApplicationException(APPLICATION_ANSWER_NOT_COMPLETE);
            }
        }
    }

    private void validateExistMoimMember(final Long moimId, final Long memberId) {
        if (moimMemberRepository.findByMoimIdAndMemberId(moimId, memberId).isPresent()) {
            throw new ApplicationException(APPLICATION_EXIST_MEMBER);
        }
    }

    private List<ApplicationAnswer> applicationRequestToDomain(final Application application,
                                                               final List<ApplicationQuestion> applicationQuestionList,
                                                               final List<ApplicationDTO> applicationList) {
        List<ApplicationAnswer> result = new ArrayList<>();
        for (ApplicationQuestion applicationQuestion : applicationQuestionList) {
            for (ApplicationDTO applicationDTO : applicationList) {
                if (Objects.equals(applicationQuestion.getOrder(), applicationDTO.getOrder())) {
                    ApplicationAnswer applicationAnswer = ApplicationAnswer.builder()
                            .application(application)
                            .question(applicationQuestion.getContent())
                            .content(applicationDTO.getContent())
                            .build();
                    result.add(applicationAnswer);
                    break;
                }
            }
        }
        return result;
    }

    public List<ApplicationResponse> getApplication(final Long moimId, final Long memberId, final String status) {
        List<Application> applicationList;
        if (status.equals(ALL)) {
            applicationList = applicationRepository.findByMemberIdAndMoimId(memberId, moimId);
        } else {
            applicationList = applicationRepository.findByMemberIdAndMoimIdAndStatus(memberId, moimId, status);
        }
        return applicationList.stream()
                .map(ApplicationResponse::new)
                .collect(Collectors.toList());
    }

    public List<ApplicationResponse> getApplication(final Long moimId, final String status) {
        List<Application> applicationList;
        if (status.equals(ALL)) {
            applicationList = applicationRepository.findAllByMoimId(moimId);
        } else {
            applicationList = applicationRepository.findAllByMoimIdAndStatus(moimId, status);
        }
        return applicationList.stream()
                .map(ApplicationResponse::new)
                .collect(Collectors.toList());
    }

    public DetailedApplicationResponse getDetailedApplication(final Long applicationId) {
        Application application = getApplicationById(applicationId);
        List<ApplicationAnswer> applicationAnswerList = applicationAnswerRepository.findAllByApplicationId(applicationId);
        return DetailedApplicationResponse.generate(application, applicationAnswerList);
    }

    private Application getApplicationById(final Long applicationId) {
        return applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ApplicationException(APPLICATION_NOT_FOUND));
    }

    @Transactional
    public void decideApplication(final Long loggedInMemberId, final Long applicationId, final ApplicationStatus status) {
        Application application = getApplicationById(applicationId);

        if (!application.isWait()) {
            throw new ApplicationException(APPLICATION_STATUS_ALREADY);
        }

        Long moimId = application.getMoim().getId();

        MoimMember loggedInMoimMember = getMoimMemberByMoimIdAndMemberId(moimId, loggedInMemberId);
        if (!loggedInMoimMember.canManage()) {
            throw new ApplicationException(APPLICATION_FORBIDDEN);
        }
        application.updateStatusTo(status);

        if (status.equals(ApplicationStatus.ACCEPT)) {
            MoimMember moimMember = MoimMember.builder()
                    .moim(application.getMoim())
                    .member(application.getMember())
                    .role(MemberRole.USER)
                    .build();
            moimMemberRepository.save(moimMember);
        }
    }

    private MoimMember getMoimMemberByMoimIdAndMemberId(final Long moimId, final Long loggedInMemberId) {
        return moimMemberRepository.findByMoimIdAndMemberId(moimId, loggedInMemberId)
                .orElseThrow(() -> new ApplicationException(MOIM_MEMBER_NOT_FOUND));
    }
}
