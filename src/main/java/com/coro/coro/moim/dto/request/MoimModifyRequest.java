package com.coro.coro.moim.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MoimModifyRequest {

    private String name;
    private String introduction;
    private Boolean visible;
}