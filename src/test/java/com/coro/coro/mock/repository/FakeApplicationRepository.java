package com.coro.coro.mock.repository;

import com.coro.coro.application.domain.Application;
import com.coro.coro.application.domain.ApplicationStatus;
import com.coro.coro.application.repository.port.ApplicationRepository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class FakeApplicationRepository implements ApplicationRepository {

    private final DataSet dataSet;

    public FakeApplicationRepository(final DataSet dataSet) {
        this.dataSet = dataSet;
    }

    @Override
    public Long save(final Application application) {
        if (Objects.isNull(application.getId())) {
            Application toBeSaved = Application.builder()
                    .id(dataSet.applicationSequence++)
                    .member(application.getMember())
                    .moim(application.getMoim())
                    .status(application.getStatus())
                    .build();
            toBeSaved.prePersist();
            dataSet.applicationData.put(toBeSaved.getId(), toBeSaved);
            return toBeSaved.getId();
        }
        application.preUpdate();
        dataSet.applicationData.put(application.getId(), application);
        return application.getId();
    }

    @Override
    public Optional<Application> findById(final Long id) {
        return dataSet.applicationData.values().stream()
                .filter(application -> application.getId().equals(id))
                .findFirst();
    }

    @Override
    public List<Application> findByMemberIdAndMoimId(final Long memberId, final Long moimId) {
        return dataSet.applicationData.values().stream()
                .filter(application ->
                        application.getMember().getId().equals(memberId) &&
                                application.getMoim().getId().equals(moimId)
                )
                .collect(Collectors.toList());
    }

    @Override
    public List<Application> findByMemberIdAndMoimIdAndStatus(final Long memberId, final Long moimId, final String status) {
        return dataSet.applicationData.values().stream()
                .filter(application ->
                        application.getMember().getId().equals(memberId) &&
                                application.getMoim().getId().equals(moimId) &&
                                application.getStatus().equals(ApplicationStatus.getApplicationStatus(status))
                )
                .collect(Collectors.toList());
    }

    @Override
    public List<Application> findAllByMoimId(final Long moimId) {
        return dataSet.applicationData.values().stream()
                .filter(application -> application.getMoim().getId().equals(moimId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Application> findAllByMoimIdAndStatus(final Long moimId, final String status) {
        return dataSet.applicationData.values().stream()
                .filter(application ->
                        application.getMoim().getId().equals(moimId) &&
                                application.getStatus().equals(ApplicationStatus.getApplicationStatus(status))
                )
                .collect(Collectors.toList());
    }
}
