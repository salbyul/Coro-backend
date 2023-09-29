package com.coro.coro.application.dto.response;

import com.coro.coro.application.domain.Application;
import com.coro.coro.application.domain.ApplicationStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ApplicationResponse {

    private Long id;
    private ApplicationStatus status;

    public ApplicationResponse(final Application application) {
        this.id = application.getId();
        this.status = application.getStatus();
    }
}
