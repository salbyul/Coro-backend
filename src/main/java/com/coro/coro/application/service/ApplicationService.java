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
import com.coro.coro.application.repository.ApplicationAnswerRepository;
import com.coro.coro.application.repository.ApplicationQuestionRepository;
import com.coro.coro.application.repository.ApplicationRepository;
import com.coro.coro.member.domain.Member;
import com.coro.coro.member.domain.MemberRole;
import com.coro.coro.member.exception.MemberException;
import com.coro.coro.member.repository.MemberRepository;
import com.coro.coro.moim.domain.Moim;
import com.coro.coro.moim.domain.MoimMember;
import com.coro.coro.moim.exception.MoimException;
import com.coro.coro.moim.repository.MoimMemberRepository;
import com.coro.coro.moim.repository.MoimRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.coro.coro.common.response.error.ErrorType.*;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class ApplicationService {

    public static final String ALL = "all";
    private final ApplicationQuestionRepository applicationQuestionRepository;
    private final MemberRepository memberRepository;
    private final MoimRepository moimRepository;
    private final ApplicationRepository applicationRepository;
    private final ApplicationAnswerRepository applicationAnswerRepository;
    private final MoimMemberRepository moimMemberRepository;

    @Transactional
    public void register(final Long moimId, final ApplicationRequest applicationRequest, final Long memberId) {
        List<ApplicationQuestion> applicationQuestionList = applicationQuestionRepository.findAllByMoimId(moimId);
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MEMBER_NOT_FOUND));
        Moim moim = moimRepository.findById(moimId)
                .orElseThrow(() -> new MoimException(MOIM_NOT_FOUND));

        List<ApplicationDTO> applicationList = applicationRequest.getApplicationList();

        validateHasWaitApplication(moimId, memberId);
        validateExistMoimMember(moim.getId(), memberId);
        validateAnswer(applicationQuestionList, applicationList);

        Application application = Application.generate(member, moim);
        applicationRepository.save(application);

        List<ApplicationAnswer> applicationAnswerList = applicationRequestToEntity(application, applicationQuestionList, applicationList);
        applicationAnswerRepository.saveAll(applicationAnswerList);
    }

    private void validateHasWaitApplication(final Long moimId, final Long memberId) {
        List<Application> existApplication = applicationRepository.findByMemberAndMoim(memberId, moimId);
        for (Application application : existApplication) {
            if (application.getStatus().equals(ApplicationStatus.WAIT)) {
                throw new ApplicationException(APPLICATION_EXIST);
            }
        }
    }

    private void validateAnswer(final List<ApplicationQuestion> applicationQuestionList, final List<ApplicationDTO> applicationList) {
        if (applicationList.size() != applicationQuestionList.size()) {
            throw new ApplicationException(APPLICATION_NOT_COMPLETE);
        }

        for (ApplicationDTO applicationDTO : applicationList) {
            if (!StringUtils.hasText(applicationDTO.getContent())) {
                throw new ApplicationException(APPLICATION_NOT_COMPLETE);
            }
        }
    }

    private void validateExistMoimMember(final Long moimId, final Long memberId) {
        if (moimMemberRepository.findByMoimIdAndMemberId(moimId, memberId).isPresent()) {
            throw new ApplicationException(APPLICATION_EXIST_MEMBER);
        }
    }

    private List<ApplicationAnswer> applicationRequestToEntity(final Application application, final List<ApplicationQuestion> applicationQuestionList, final List<ApplicationDTO> applicationList) {
        List<ApplicationAnswer> result = new ArrayList<>();
        for (ApplicationQuestion applicationQuestion : applicationQuestionList) {
            for (ApplicationDTO applicationDTO : applicationList) {
                if (applicationQuestion.getOrder() == applicationDTO.getOrder()) {
                    result.add(ApplicationAnswer.generate(application, applicationQuestion, applicationDTO.getContent()));
                    break;
                }
            }
        }
        return result;
    }

    public List<ApplicationResponse> getApplication(final Long moimId, final Long memberId, final String status) {
        List<Application> applicationList;
        if (status.equals(ALL)) {
            applicationList = applicationRepository.findByMemberAndMoim(memberId, moimId);
        } else {
            applicationList = applicationRepository.findByMemberAndMoimAndStatus(memberId, moimId, status);
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
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ApplicationException(APPLICATION_NOT_FOUND));

        List<ApplicationAnswer> applicationAnswerList = applicationAnswerRepository.findAllByApplicationId(applicationId);
        return DetailedApplicationResponse.generate(application, applicationAnswerList);
    }

    @Transactional
    public void decideApplication(final Long memberId, final Long applicationId, final ApplicationStatus status) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ApplicationException(APPLICATION_NOT_FOUND));

        if (!application.isWait()) {
            throw new ApplicationException(APPLICATION_STATUS_ALREADY);
        }

        Long moimId = application.getMoim().getId();
        moimMemberRepository.findByMoimIdAndMemberId(moimId, memberId)
                .filter(MoimMember::canManage)
                .orElseThrow(() -> new ApplicationException(APPLICATION_FORBIDDEN));
        application.updateStatusTo(status);

        if (status.equals(ApplicationStatus.ACCEPT)) {
            MoimMember moimMember = MoimMember.generate(application.getMoim(), application.getMember(), MemberRole.USER);
            moimMemberRepository.save(moimMember);
        }
    }
}
