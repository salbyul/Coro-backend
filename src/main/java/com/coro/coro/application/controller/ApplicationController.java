package com.coro.coro.application.controller;

import com.coro.coro.application.dto.request.ApplicationQuestionRegisterRequest;
import com.coro.coro.application.service.ApplicationQuestionService;
import com.coro.coro.common.response.APIResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/applications")
public class ApplicationController {

    private final ApplicationQuestionService applicationQuestionService;

    @PostMapping("/questions")
    public APIResponse register(@RequestBody final List<ApplicationQuestionRegisterRequest> requestQuestions, final Long id) {
        applicationQuestionService.register(id, requestQuestions);
        return APIResponse.create();
    }
}
