package com.coro.coro.moim.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MoimTagRequest {

    private List<String> tagList = new ArrayList<>();
}
