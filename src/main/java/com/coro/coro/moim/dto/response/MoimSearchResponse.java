package com.coro.coro.moim.dto.response;

import com.coro.coro.moim.domain.Moim;
import com.coro.coro.moim.domain.MoimTag;
import com.coro.coro.moim.domain.MoimType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MoimSearchResponse {

    private Long id;
    private String name;
    private String introduction;
    private MoimType moimType;
    private List<String> tagList;
    private String photoName;
    private byte[] photo;

    public MoimSearchResponse(final Moim moim, final String photoName, final byte[] photo) {
        this.id = moim.getId();
        this.name = moim.getName();
        this.introduction = moim.getIntroduction();
        this.moimType = moim.getType();
        this.tagList = moim.getTagList().stream().map(MoimTag::getName).collect(Collectors.toList());;
        this.photoName = photoName;
        this.photo = photo;
    }
}
