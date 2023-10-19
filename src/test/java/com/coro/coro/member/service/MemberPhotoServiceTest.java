package com.coro.coro.member.service;

import com.coro.coro.common.response.APIResponse;
import com.coro.coro.member.dto.request.MemberRegisterRequest;
import com.coro.coro.member.exception.MemberException;
import com.coro.coro.mock.FakeContainer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import static com.coro.coro.common.response.error.ErrorType.MEMBER_PHOTO_NOT_VALID;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MemberPhotoServiceTest {

//    FIXME
    @Test
    @DisplayName("유저 사진 변경 실패 - 이미지외에 파일")
    void changeProfileImage() {
        FakeContainer container = new FakeContainer();

//        회원가입
        MemberRegisterRequest request = new MemberRegisterRequest("asdf@asdf.com", "asdf1234!@", "닉네임");
        APIResponse joinResponse = container.memberController.register(request);
        Long savedId = (Long) joinResponse.getBody().get("savedId");

//        유저 사진 변경
        MockMultipartFile multipartFile = new MockMultipartFile("photo.js", "photo.js", "text/javascript", new byte[0]);

//        검증
        assertThatThrownBy(() ->
                container.memberController.update(savedId, multipartFile, null)
        )
                .isInstanceOf(MemberException.class)
                .hasMessage(MEMBER_PHOTO_NOT_VALID.getMessage());
    }

}