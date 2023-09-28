package com.coro.coro.moim.dto.response;

import com.coro.coro.moim.domain.Moim;
import com.coro.coro.moim.domain.MoimTag;
import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MoimDetailResponse {

    private String name;
    private String introduction;
    private List<String> tagList;
    private String photoName;
    private byte[] photo;

    private MoimDetailResponse(final Moim moim) {
        this.name = moim.getName();
        this.introduction = moim.getIntroduction();
        this.tagList = moim.getTagList().stream()
                .map(MoimTag::getName)
                .collect(Collectors.toList());
    }

    public static MoimDetailResponse generateInstance(final Moim moim) {
        return new MoimDetailResponse(moim);
    }

    public void setPhoto(final String photoName, final byte[] photo) {
        this.photoName = photoName;
        this.photo = photo;
    }
}
