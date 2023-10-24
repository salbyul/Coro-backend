package com.coro.coro.moim.dto.response;

import com.coro.coro.application.domain.ApplicationQuestion;
import com.coro.coro.application.dto.response.ApplicationQuestionResponse;
import com.coro.coro.moim.domain.Moim;
import com.coro.coro.moim.domain.MoimPhoto;
import com.coro.coro.moim.domain.MoimTag;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class MoimModificationResponse {

    private final String name;
    private final String introduction;
    private final boolean visible;
    private final String type;
    private String photoName;
    private byte[] photo;
    private String contentType;
    private final List<String> tagList;
    private final List<ApplicationQuestionResponse> applicationQuestionList;

    private MoimModificationResponse(final Moim moim, final List<ApplicationQuestion> applicationQuestionList) {
        this.name = moim.getName();
        this.introduction = moim.getIntroduction();
        this.visible = moim.getVisible();
        this.type = moim.getType().toString();
        this.tagList = moim.getTagList().stream()
                .map(MoimTag::getName)
                .collect(Collectors.toList());
        this.applicationQuestionList = applicationQuestionList.stream()
                .map(ApplicationQuestionResponse::new)
                .collect(Collectors.toList());
    }

    public static MoimModificationResponse generate(final Moim moim, final List<ApplicationQuestion> applicationQuestionList) {
        return new MoimModificationResponse(moim, applicationQuestionList);
    }

    public void setPhoto(final MoimPhoto moimPhoto, final byte[] photo) {
        this.photoName = moimPhoto.getOriginalName();
        this.photo = photo;
        this.contentType = moimPhoto.getContentType();
    }
}
