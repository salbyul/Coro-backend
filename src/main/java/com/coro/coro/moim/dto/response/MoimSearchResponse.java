package com.coro.coro.moim.dto.response;

import com.coro.coro.moim.domain.Moim;
import com.coro.coro.moim.domain.MoimTag;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MoimSearchResponse {

    private String name;
    private List<String> tagList;

    public MoimSearchResponse(final Moim moim) {
        this.name = moim.getName();
        this.tagList = moim.getTagList().stream().map(MoimTag::getName).collect(Collectors.toList());;
    }
}
