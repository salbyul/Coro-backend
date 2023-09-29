package com.coro.coro.application.controller;

import com.coro.coro.application.dto.request.ApplicationRequest;
import com.coro.coro.application.dto.response.ApplicationQuestionResponse;
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

    @GetMapping("/questions/{id}")
    @Override
    public APIResponse getQuestionList(@PathVariable("id") final Long moimId) {
        List<ApplicationQuestionResponse> questionList = applicationQuestionService.findQuestionList(moimId);
        return APIResponse.create()
                .addObject("questionList", questionList);
    }

    @PostMapping("/{moimId}")
    @Override
    public APIResponse submitApplication(@PathVariable("moimId") Long moimId,
                                  @RequestBody final ApplicationRequest applicationRequest,
                                  @AuthenticationPrincipal final User user){
        log.info("application: {}", applicationRequest);
        applicationService.register(moimId, applicationRequest, user.getId());
        return APIResponse.create();
    }
}
