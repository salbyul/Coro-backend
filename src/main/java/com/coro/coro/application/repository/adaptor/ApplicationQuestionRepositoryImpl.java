package com.coro.coro.application.repository.adaptor;

import com.coro.coro.application.domain.ApplicationQuestion;
import com.coro.coro.application.repository.ApplicationQuestionJpaRepository;
import com.coro.coro.application.service.port.ApplicationQuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@RequiredArgsConstructor
@Repository
public class ApplicationQuestionRepositoryImpl implements ApplicationQuestionRepository {

    private final ApplicationQuestionJpaRepository applicationQuestionJpaRepository;

    @Override
    public List<ApplicationQuestion> findAllByMoimId(final Long moimId) {
        return applicationQuestionJpaRepository.findAllByMoimId(moimId);
    }

    @Override
    public void deleteAllByMoimId(final Long moimId) {
        applicationQuestionJpaRepository.deleteAllByMoimId(moimId);
    }

    @Override
    public void saveAll(final List<ApplicationQuestion> applicationQuestionList) {
        applicationQuestionJpaRepository.saveAll(applicationQuestionList);
    }
}
