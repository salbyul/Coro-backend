package com.coro.coro.moim.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MoimModificationRequest {

    private String name;
    private String introduction;
    private String type;
    private Boolean visible;
    private Boolean isDeletedPhoto;
}
