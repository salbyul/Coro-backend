package com.coro.coro.application.dto.request;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class ApplicationRequest {

    private List<ApplicationDTO> applicationList = new ArrayList<>();
}
