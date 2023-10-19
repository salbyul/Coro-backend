package com.coro.coro.moim.domain;

import com.coro.coro.member.domain.Member;
import com.coro.coro.member.domain.MemberRole;
import com.coro.coro.moim.dto.request.MoimMemberModificationRequest;
import com.coro.coro.moim.exception.MoimException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.coro.coro.common.response.error.ErrorType.*;
import static org.assertj.core.api.Assertions.*;

class MoimMemberTest {

    private Member leader;
    private Moim moim;

    @BeforeEach
    void setUp() {
        leader = Member.builder()
                .email("asdf@asdf.com")
                .nickname("리더")
                .password("asdf1234!@")
                .build();
        moim = Moim.builder()
                .leader(leader)
                .name("모임")
                .introduction("모임 소개입니다.")
                .visible(true)
                .type(MoimType.FACE_TO_FACE)
                .build();
    }

    @Test
    @DisplayName("관리할 수 없는 멤버일 경우 확인")
    void cantManage() {
        Member member = Member.builder()
                .email("a@a.com")
                .nickname("회원")
                .password("asdf1234!@")
                .build();
        MoimMember moimMember = MoimMember.builder()
                .moim(moim)
                .member(member)
                .role(MemberRole.USER)
                .build();

        assertThat(moimMember.canManage()).isFalse();
    }

    @Test
    @DisplayName("관리할 수 있는 멤버일 경우 확인")
    void canManage() {
        MoimMember moimMember = MoimMember.builder()
                .moim(moim)
                .member(leader)
                .role(MemberRole.LEADER)
                .build();

        assertThat(moimMember.canManage()).isTrue();
    }

    @Test
    @DisplayName("업데이트 시 이름이 다를 경우")
    void updateFailByNotValidName() {
        Member member = Member.builder()
                .email("a@a.com")
                .nickname("회원")
                .password("asdf1234!@")
                .build();

        MoimMember moimMember = MoimMember.builder()
                .moim(moim)
                .member(member)
                .role(MemberRole.USER)
                .build();
        MoimMemberModificationRequest modificationRequest = MoimMemberModificationRequest.builder()
                .memberName("회원1")
                .role(MemberRole.MANAGER)
                .build();

        assertThatThrownBy(() -> moimMember.update(modificationRequest))
                .isInstanceOf(MoimException.class)
                .hasMessage(MOIM_MEMBER_NOT_VALID.getMessage());
    }

    @Test
    @DisplayName("리더로 업데이트할 경우")
    void updateToLeader() {
        Member member = Member.builder()
                .email("a@a.com")
                .nickname("회원")
                .password("asdf1234!@")
                .build();

        MoimMember moimMember = MoimMember.builder()
                .moim(moim)
                .member(member)
                .role(MemberRole.USER)
                .build();
        MoimMemberModificationRequest modificationRequest = MoimMemberModificationRequest.builder()
                .memberName("회원")
                .role(MemberRole.LEADER)
                .build();

        moimMember.update(modificationRequest);

        assertThat(moimMember.canManage()).isTrue();
        assertThat(moim.getLeader()).isEqualTo(member);
    }

    @Test
    @DisplayName("매니저로 업데이트할 경우")
    void updateToManager() {
        Member member = Member.builder()
                .email("a@a.com")
                .nickname("회원")
                .password("asdf1234!@")
                .build();

        MoimMember moimMember = MoimMember.builder()
                .moim(moim)
                .member(member)
                .role(MemberRole.USER)
                .build();
        MoimMemberModificationRequest modificationRequest = MoimMemberModificationRequest.builder()
                .memberName("회원")
                .role(MemberRole.MANAGER)
                .build();
        moimMember.update(modificationRequest);

        assertThat(moimMember.canManage()).isTrue();
        assertThat(moim.getLeader()).isNotEqualTo(member);
    }
}