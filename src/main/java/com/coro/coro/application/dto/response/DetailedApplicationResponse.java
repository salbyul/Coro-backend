package com.coro.coro.application.dto.response;

import com.coro.coro.application.domain.Application;
import com.coro.coro.application.domain.ApplicationAnswer;
import com.coro.coro.application.domain.ApplicationStatus;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class DetailedApplicationResponse {

    private final Long id;
    private final ApplicationStatus status;
    private final List<ApplicationAnswerDTO> applicationAnswerDTOList;

    private DetailedApplicationResponse(final Application application, final List<ApplicationAnswer> applicationAnswerList) {
        this.id = application.getId();
        this.status = application.getStatus();
        this.applicationAnswerDTOList = applicationAnswerList.stream()
                .map(ApplicationAnswerDTO::new)
                .collect(Collectors.toList());
    }

    public static DetailedApplicationResponse generate(final Application application, final List<ApplicationAnswer> applicationAnswerList) {
        return new DetailedApplicationResponse(application, applicationAnswerList);
    }
}
