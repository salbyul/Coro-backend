package com.coro.coro.application.domain;

import com.coro.coro.application.dto.request.ApplicationQuestionRegisterRequest;
import com.coro.coro.member.domain.Member;
import com.coro.coro.moim.domain.Moim;
import com.coro.coro.moim.domain.MoimType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertAll;

class ApplicationAnswerTest {

    @Test
    @DisplayName("객체 생성 확인")
    void generate() {
        Member member = Member.builder()
                .email("asdf@asdf.com")
                .nickname("닉네임")
                .password("asdf1234!@")
                .build();
        Moim moim = Moim.builder()
                .leader(member)
                .name("모임")
                .introduction("모임 설명입니다.")
                .visible(true)
                .type(MoimType.FACE_TO_FACE)
                .build();
        Application application = Application.generate(member, moim);
        ApplicationQuestion applicationQuestion =
                ApplicationQuestion.generate(moim, new ApplicationQuestionRegisterRequest("질문", 1));

        ApplicationAnswer applicationAnswer = ApplicationAnswer.generate(application, applicationQuestion, "답변입니다.");

        assertAll(
                () -> assertThat(applicationAnswer.getApplication()).isEqualTo(application),
                () -> assertThat(applicationAnswer.getContent()).isEqualTo("답변입니다."),
                () -> assertThat(applicationAnswer.getQuestion()).isEqualTo("질문"),
                () -> assertThat(applicationAnswer.isNew()).isTrue()
        );
    }
}