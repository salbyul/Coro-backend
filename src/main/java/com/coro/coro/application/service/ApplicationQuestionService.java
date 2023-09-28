package com.coro.coro.application.service;

import com.coro.coro.application.domain.ApplicationQuestion;
import com.coro.coro.application.dto.request.ApplicationQuestionRegisterRequest;
import com.coro.coro.application.dto.response.ApplicationQuestionResponse;
import com.coro.coro.application.repository.ApplicationQuestionRepository;
import com.coro.coro.application.validator.ApplicationQuestionValidator;
import com.coro.coro.common.response.error.ErrorType;
import com.coro.coro.moim.domain.Moim;
import com.coro.coro.moim.exception.MoimException;
import com.coro.coro.moim.repository.MoimRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ApplicationQuestionService {

    private final MoimRepository moimRepository;
    private final ApplicationQuestionRepository applicationQuestionRepository;

    /* All delete and All Insert */
        @Transactional
        public void register(final Long moimId, final List<ApplicationQuestionRegisterRequest> requestQuestions) {
            Moim moim = moimRepository.findById(moimId)
                    .orElseThrow(() -> new MoimException(ErrorType.MOIM_NOT_FOUND));

            List<ApplicationQuestion> applicationQuestionList = requestQuestions.stream()
                    .map(requestQuestion -> ApplicationQuestion.generateApplicationQuestion(moim, requestQuestion))
                    .collect(Collectors.toList());

            ApplicationQuestionValidator.validateApplicationQuestion(applicationQuestionList);

            applicationQuestionRepository.deleteAllByMoimId(moim.getId());
            applicationQuestionRepository.saveAll(applicationQuestionList);
    }

    public List<ApplicationQuestionResponse> findQuestionList(final Long moimId) {
        List<ApplicationQuestion> questionList = applicationQuestionRepository.findAllByMoimId(moimId);
        return questionList.stream()
                .map(ApplicationQuestionResponse::new)
                .collect(Collectors.toList());
    }
}
