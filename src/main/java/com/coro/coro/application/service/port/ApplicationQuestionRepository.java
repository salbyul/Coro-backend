package com.coro.coro.application.service.port;

import com.coro.coro.application.domain.ApplicationQuestion;

import java.util.List;

public interface ApplicationQuestionRepository {

    List<ApplicationQuestion> findAllByMoimId(final Long moimId);

    void deleteAllByMoimId(final Long moimId);

    void saveAll(final List<ApplicationQuestion> applicationQuestionList);
}
