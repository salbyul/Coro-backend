package com.coro.coro.application.controller;

import com.coro.coro.application.dto.response.ApplicationQuestionResponse;
import com.coro.coro.application.service.ApplicationQuestionService;
import com.coro.coro.common.response.APIResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/applications")
public class ApplicationController implements ApplicationControllerDocs {

    private final ApplicationQuestionService applicationQuestionService;

    @GetMapping("/questions/{id}")
    @Override
    public APIResponse getQuestionList(@PathVariable("id") final Long moimId) {
        List<ApplicationQuestionResponse> questionList = applicationQuestionService.findQuestionList(moimId);
        return APIResponse.create()
                .addObject("questionList", questionList);
    }
}
