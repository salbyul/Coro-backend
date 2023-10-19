package com.coro.coro.application.service;

import com.coro.coro.application.domain.ApplicationQuestion;
import com.coro.coro.application.dto.request.ApplicationQuestionRegisterRequest;
import com.coro.coro.application.exception.ApplicationException;
import com.coro.coro.application.repository.port.ApplicationQuestionRepository;
import com.coro.coro.application.validator.ApplicationQuestionValidator;
import com.coro.coro.moim.domain.Moim;
import com.coro.coro.moim.repository.port.MoimRepository;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.coro.coro.common.response.error.ErrorType.*;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Builder
@Service
public class ApplicationQuestionService {

    private final MoimRepository moimRepository;
    private final ApplicationQuestionRepository applicationQuestionRepository;

    /* All delete and All Insert */
    @Transactional
    public void register(final Long moimId, final List<ApplicationQuestionRegisterRequest> requestQuestions) {
        Moim moim = getMoimById(moimId);

        List<ApplicationQuestion> applicationQuestionList = requestQuestions.stream()
                .map(requestQuestion ->
                        ApplicationQuestion.builder()
                                .order(requestQuestion.getOrder())
                                .moim(moim)
                                .content(requestQuestion.getContent())
                                .build()
                )
                .collect(Collectors.toList());

        ApplicationQuestionValidator.validateApplicationQuestion(applicationQuestionList);

        applicationQuestionRepository.deleteAllByMoimId(moim.getId());
        applicationQuestionRepository.saveAll(applicationQuestionList);
    }

    private Moim getMoimById(final Long moimId) {
        return moimRepository.findById(moimId)
                .orElseThrow(() -> new ApplicationException(MOIM_NOT_FOUND));
    }

    public List<ApplicationQuestion> findQuestionList(final Long moimId) {
        return  applicationQuestionRepository.findAllByMoimId(moimId);
    }
}


