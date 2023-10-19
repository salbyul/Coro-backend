package com.coro.coro.moim.domain;

import com.coro.coro.member.domain.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class MoimTagTest {

    @Test
    @DisplayName("isDuplicateName 메서드 확인")
    void validateDuplicateName() {
        Member leader = Member.builder()
                .nickname("닉네임")
                .email("asdf@asdf.com")
                .password("asdf1234!@")
                .build();
        Moim moim = Moim.builder()
                .leader(leader)
                .name("모임")
                .introduction("모임 소개입니다.")
                .visible(true)
                .type(MoimType.FACE_TO_FACE)
                .build();

        MoimTag moimTag = MoimTag.builder()
                .name("중복태그")
                .moim(moim)
                .build();

        MoimTag duplicatedTag = MoimTag.builder()
                .name("중복태그")
                .moim(moim)
                .build();

        assertThat(moimTag.getName()).isEqualTo("중복태그");
        assertThat(moimTag.isDuplicateName(duplicatedTag)).isTrue();
    }
}