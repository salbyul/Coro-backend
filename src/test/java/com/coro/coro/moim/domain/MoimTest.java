package com.coro.coro.moim.domain;

import com.coro.coro.member.domain.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class MoimTest {

    @Test
    @DisplayName("리더 변경 시 확인")
    void changeLeader() {
        Member leader = Member.builder()
                .nickname("닉네임")
                .password("asdf1234!@")
                .email("asdf@asdf.com")
                .build();
        Moim moim = Moim.builder()
                .leader(leader)
                .name("모임")
                .introduction("모임 소개입니다.")
                .visible(true)
                .type(MoimType.FACE_TO_FACE)
                .build();
        Member newLeader = Member.builder()
                .nickname("새로운리더")
                .password("asdf1234!@")
                .email("a@a.com")
                .build();

        moim.changeLeader(newLeader);

        assertThat(moim.getLeader()).isEqualTo(newLeader);
    }
}