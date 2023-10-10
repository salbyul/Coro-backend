package com.coro.coro.application.domain;

import com.coro.coro.member.domain.Member;
import com.coro.coro.moim.domain.Moim;
import com.coro.coro.moim.domain.MoimType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertAll;


class ApplicationTest {

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

        assertAll(
                () -> assertThat(application.getMember()).isEqualTo(member),
                () -> assertThat(application.getMoim()).isEqualTo(moim),
                () -> assertThat(application.getStatus()).isEqualTo(ApplicationStatus.WAIT),
                () -> assertThat(application.isWait()).isTrue()
        );
    }

    @Test
    @DisplayName("지원서 상태 변경 확인")
    void updateStatusTo() {
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
        application.updateStatusTo(ApplicationStatus.ACCEPT);

        assertThat(application.getStatus()).isEqualTo(ApplicationStatus.ACCEPT);
    }
}