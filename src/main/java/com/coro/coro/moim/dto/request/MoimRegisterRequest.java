package com.coro.coro.moim.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MoimRegisterRequest {

    private String name;
    private String introduction;
    private String type;
    private Boolean visible;
}
