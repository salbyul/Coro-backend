package com.coro.coro.application.dto.response;

import com.coro.coro.application.domain.Application;
import com.coro.coro.application.domain.ApplicationStatus;
import lombok.Getter;

@Getter
public class ApplicationResponse {

    private final Long id;
    private final String applicantName;
    private final ApplicationStatus status;

    public ApplicationResponse(final Application application) {
        this.id = application.getId();
        this.applicantName = application.getMember().getNickname();
        this.status = application.getStatus();
    }
}
