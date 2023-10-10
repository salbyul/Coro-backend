package com.coro.coro.moim.dto.response;

import com.coro.coro.moim.domain.Moim;
import com.coro.coro.moim.domain.MoimMember;
import com.coro.coro.moim.domain.MoimTag;
import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MoimDetailResponse {

    private String name;
    private String introduction;
    private List<String> tagList;
    private boolean isJoined;
    private boolean canManage;
    private String photoName;
    private byte[] photo;

    private MoimDetailResponse(final Moim moim, final boolean isJoined, final boolean canManage) {
        this.name = moim.getName();
        this.introduction = moim.getIntroduction();
        this.tagList = moim.getTagList().stream()
                .map(MoimTag::getName)
                .collect(Collectors.toList());
        this.isJoined = isJoined;
        this.canManage = canManage;
    }

    public static MoimDetailResponse generateInstance(final Moim moim, final boolean isJoined, final boolean canManage) {
        return new MoimDetailResponse(moim, isJoined, canManage);
    }

    public static MoimDetailResponse generateInstance(final Moim moim, final MoimMember moimMember) {
        return new MoimDetailResponse(moim, true, moimMember.canManage());
    }

    public void setPhoto(final String photoName, final byte[] photo) {
        this.photoName = photoName;
        this.photo = photo;
    }
}
