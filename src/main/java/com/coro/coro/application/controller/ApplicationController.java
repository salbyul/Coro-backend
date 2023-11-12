package com.coro.coro.application.controller;

import com.coro.coro.application.annotation.StatusValue;
import com.coro.coro.application.domain.ApplicationStatus;
import com.coro.coro.application.dto.request.ApplicationRequest;
import com.coro.coro.application.dto.response.ApplicationQuestionResponse;
import com.coro.coro.application.dto.response.ApplicationResponse;
import com.coro.coro.application.dto.response.DetailedApplicationResponse;
import com.coro.coro.application.service.ApplicationQuestionService;
import com.coro.coro.application.service.ApplicationService;
import com.coro.coro.common.response.APIResponse;
import com.coro.coro.member.service.User;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@RestController
@Builder
@RequestMapping("/api/applications")
public class ApplicationController implements ApplicationControllerDocs {

    private final ApplicationQuestionService applicationQuestionService;
    private final ApplicationService applicationService;

    /**
     * 특정 모임의 지원된 특정 상태의 지원서 모두 획득 (상태)
     *
     * @param moimId 해당 모임의 Id 값
     * @param status 획득할 지원서의 상태 값
     * @return 지원서 리스트
     */
    @GetMapping
    @Override
    public APIResponse getApplication(@ModelAttribute(name = "moim") final Long moimId, final String status) {
        List<ApplicationResponse> applicationList = applicationService.getApplication(moimId, status);
        return APIResponse.create()
                .addObject("applicationList", applicationList);
    }

    /**
     * 특정 회원이 특정 모임에 지원한 특정 상태의 지원서 획득 (상태)
     *
     * @param moimId 해당 모임의 Id 값
     * @param status 획득할 지원서의 상태 값
     * @param user 로그인한 유저
     * @return 지원서 리스트
     */
    @GetMapping("/members")
    @Override
    public APIResponse getApplicationByMember(@ModelAttribute(name = "moim") final Long moimId,
                                              final String status,
                                              @AuthenticationPrincipal final User user) {
        List<ApplicationResponse> applicationList = applicationService.getApplication(moimId, user.getId(), status);
        return APIResponse.create()
                .addObject("applicationList", applicationList);
    }

    /**
     * 디테일한 지원서 데이터 획득
     *
     * @param applicationId 획득할 지원서의 Id 값
     * @return 디테일한 지원서 데이터
     */
    @GetMapping("/{id}")
    public APIResponse getDetailedApplication(@PathVariable(name = "id") final Long applicationId) {
        DetailedApplicationResponse detailedApplication = applicationService.getDetailedApplication(applicationId);
        return APIResponse.create()
                .addObject("application", detailedApplication);
    }

    /**
     * 특정 모임의 지원 양식 획득
     *
     * @param moimId 해당 모임의 Id 값
     * @return 지원 양식
     */
    @GetMapping("/questions/{id}")
    @Override
    public APIResponse getQuestionList(@PathVariable("id") final Long moimId) {
        List<ApplicationQuestionResponse> questionList = applicationQuestionService.findQuestionList(moimId)
                .stream()
                .map(ApplicationQuestionResponse::new)
                .collect(Collectors.toList());
        return APIResponse.create()
                .addObject("questionList", questionList);
    }

    /**
     * 지원서 제출
     *
     * @param moimId 지원될 모임의 Id 값
     * @param applicationRequest 지원서의 데이터가 담긴 객체
     * @param user 로그인한 유저
     * @return 지원한 지원서의 Id 값
     */
    @PostMapping("/{moimId}")
    @Override
    public APIResponse submitApplication(@PathVariable("moimId") final Long moimId,
                                         @RequestBody final ApplicationRequest applicationRequest,
                                         @AuthenticationPrincipal final User user) {
        Long applicationId = applicationService.register(moimId, applicationRequest, user.getId());
        return APIResponse.create()
                .addObject("applicationId", applicationId);
    }

    /**
     * 지원서 상태 변경
     *
     * @param applicationId 상태 변경될 지원서의 Id 값
     * @param status 변경될 상태 값
     * @param user 로그인한 유저
     * @return 반환값 없음
     */
    @PutMapping("/{applicationId}")
    @Override
    public APIResponse decideApplication(@PathVariable(name = "applicationId") final Long applicationId, @StatusValue final ApplicationStatus status, @AuthenticationPrincipal final User user) {
        applicationService.decideApplication(user.getId(), applicationId, status);
        return APIResponse.create();
    }
}
