package com.coro.coro.application.repository.adaptor;

import com.coro.coro.application.domain.ApplicationAnswer;
import com.coro.coro.application.repository.ApplicationAnswerJpaRepository;
import com.coro.coro.application.service.port.ApplicationAnswerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@RequiredArgsConstructor
@Repository
public class ApplicationAnswerRepositoryImpl implements ApplicationAnswerRepository {

    private final ApplicationAnswerJpaRepository applicationAnswerJpaRepository;

    @Override
    public void saveAll(final List<ApplicationAnswer> applicationAnswerList) {
        applicationAnswerJpaRepository.saveAll(applicationAnswerList);
    }

    @Override
    public List<ApplicationAnswer> findAllByApplicationId(final Long applicationId) {
        return applicationAnswerJpaRepository.findAllByApplicationId(applicationId);
    }
}
