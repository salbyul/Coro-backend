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
import com.coro.coro.member.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/applications")
public class ApplicationController implements ApplicationControllerDocs {

    private final ApplicationQuestionService applicationQuestionService;
    private final ApplicationService applicationService;

    @GetMapping
    @Override
    public APIResponse getApplication(@ModelAttribute(name = "moim") final Long moimId, final String status) {
        List<ApplicationResponse> applicationList = applicationService.getApplication(moimId, status);
        return APIResponse.create()
                .addObject("applicationList", applicationList);
    }

    @GetMapping("/members")
    @Override
    public APIResponse getApplicationByMember(@ModelAttribute(name = "moim") final Long moimId,
                                              final String status,
                                              @AuthenticationPrincipal final User user) {
        List<ApplicationResponse> applicationList = applicationService.getApplication(moimId, user.getId(), status);
        return APIResponse.create()
                .addObject("applicationList", applicationList);
    }

    @GetMapping("/{id}")
    public APIResponse getDetailedApplication(@PathVariable(name = "id") final Long applicationId) {
        DetailedApplicationResponse detailedApplication = applicationService.getDetailedApplication(applicationId);
        return APIResponse.create()
                .addObject("application", detailedApplication);
    }

    @GetMapping("/questions/{id}")
    @Override
    public APIResponse getQuestionList(@PathVariable("id") final Long moimId) {
        List<ApplicationQuestionResponse> questionList = applicationQuestionService.findQuestionList(moimId);
        return APIResponse.create()
                .addObject("questionList", questionList);
    }

    @PostMapping("/{moimId}")
    @Override
    public APIResponse submitApplication(@PathVariable("moimId") final Long moimId,
                                         @RequestBody final ApplicationRequest applicationRequest,
                                         @AuthenticationPrincipal final User user) {
        log.info("application: {}", applicationRequest);
        applicationService.register(moimId, applicationRequest, user.getId());
        return APIResponse.create();
    }

    @PutMapping("/{applicationId}")
    @Override
    public APIResponse decideApplication(@PathVariable(name = "applicationId") final Long applicationId, @StatusValue final ApplicationStatus status, @AuthenticationPrincipal final User user) {
        applicationService.decideApplication(user.getId(), applicationId, status);
        return APIResponse.create();
    }
}
