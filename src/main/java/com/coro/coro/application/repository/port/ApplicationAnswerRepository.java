package com.coro.coro.application.repository.port;

import com.coro.coro.application.domain.ApplicationAnswer;

import java.util.List;

public interface ApplicationAnswerRepository {

    void saveAll(final List<ApplicationAnswer> applicationAnswerList);

    List<ApplicationAnswer> findAllByApplicationId(final Long applicationId);
}
