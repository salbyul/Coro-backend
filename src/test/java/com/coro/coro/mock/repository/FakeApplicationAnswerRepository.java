package com.coro.coro.mock.repository;

import com.coro.coro.application.domain.ApplicationAnswer;
import com.coro.coro.application.repository.port.ApplicationAnswerRepository;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class FakeApplicationAnswerRepository implements ApplicationAnswerRepository {

    private final DataSet dataSet;

    public FakeApplicationAnswerRepository(final DataSet dataSet) {
        this.dataSet = dataSet;
    }

    private Long save(final ApplicationAnswer applicationAnswer) {
        if (Objects.isNull(applicationAnswer.getId())) {
            ApplicationAnswer toBeSaved = ApplicationAnswer.builder()
                    .id(dataSet.applicationAnswerSequence++)
                    .application(applicationAnswer.getApplication())
                    .question(applicationAnswer.getQuestion())
                    .content(applicationAnswer.getContent())
                    .build();
            dataSet.applicationAnswerData.put(toBeSaved.getId(), toBeSaved);
            return toBeSaved.getId();
        }
        dataSet.applicationAnswerData.put(applicationAnswer.getId(), applicationAnswer);
        return applicationAnswer.getId();
    }

    @Override
    public void saveAll(final List<ApplicationAnswer> applicationAnswerList) {
        applicationAnswerList.forEach(this::save);
    }

    @Override
    public List<ApplicationAnswer> findAllByApplicationId(final Long applicationId) {
        return dataSet.applicationAnswerData.values().stream()
                .filter(applicationAnswer -> applicationAnswer.getApplication().getId().equals(applicationId))
                .collect(Collectors.toList());
    }
}
