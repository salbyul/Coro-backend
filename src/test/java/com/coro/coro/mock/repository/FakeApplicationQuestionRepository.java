package com.coro.coro.mock.repository;

import com.coro.coro.application.domain.ApplicationQuestion;
import com.coro.coro.application.service.port.ApplicationQuestionRepository;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class FakeApplicationQuestionRepository implements ApplicationQuestionRepository {

    private final DataSet dataSet;

    public FakeApplicationQuestionRepository(final DataSet dataSet) {
        this.dataSet = dataSet;
    }

    private Long save(final ApplicationQuestion applicationQuestion) {
        if (Objects.isNull(applicationQuestion.getId())) {
            ApplicationQuestion toBeSaved = ApplicationQuestion.builder()
                    .id(dataSet.applicationQuestionSequence++)
                    .order(applicationQuestion.getOrder())
                    .moim(applicationQuestion.getMoim())
                    .content(applicationQuestion.getContent())
                    .build();
            toBeSaved.prePersist();
            dataSet.applicationQuestionData.put(toBeSaved.getId(), toBeSaved);
            return toBeSaved.getId();
        }
        applicationQuestion.preUpdate();
        dataSet.applicationQuestionData.put(applicationQuestion.getId(), applicationQuestion);
        return applicationQuestion.getId();
    }

    @Override
    public List<ApplicationQuestion> findAllByMoimId(final Long moimId) {
        return dataSet.applicationQuestionData.values().stream()
                .filter(applicationQuestion -> applicationQuestion.getMoim().getId().equals(moimId))
                .collect(Collectors.toList());
    }

    @Override
    public void deleteAllByMoimId(final Long moimId) {
        List<ApplicationQuestion> toBeRemoved = dataSet.applicationQuestionData.values().stream()
                .filter(applicationQuestion -> applicationQuestion.getMoim().getId().equals(moimId))
                .collect(Collectors.toList());

        for (ApplicationQuestion applicationQuestion : toBeRemoved) {
            dataSet.applicationQuestionData.remove(applicationQuestion.getId());
        }
    }

    @Override
    public void saveAll(final List<ApplicationQuestion> applicationQuestionList) {
        applicationQuestionList.forEach(this::save);
    }
}
