package com.coro.coro.moim.service;

import com.coro.coro.member.domain.Member;
import com.coro.coro.member.dto.request.MemberRegisterRequest;
import com.coro.coro.member.exception.MemberException;
import com.coro.coro.member.repository.MemberRepository;
import com.coro.coro.member.service.MemberService;
import com.coro.coro.moim.domain.Moim;
import com.coro.coro.moim.domain.MoimType;
import com.coro.coro.moim.dto.request.MoimModifyRequest;
import com.coro.coro.moim.dto.request.MoimRegisterRequest;
import com.coro.coro.moim.dto.request.MoimTagRequest;
import com.coro.coro.moim.exception.MoimException;
import com.coro.coro.moim.repository.MoimRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;

import static com.coro.coro.common.response.error.ErrorType.*;
import static org.assertj.core.api.Assertions.*;

@Transactional
@SpringBootTest
class MoimServiceTest {

    private static final String EXAMPLE_NAME = "모임 예제";
    private static final String EXAMPLE_INTRODUCTION = "모임 소개입니다.";
    private static final String EXAMPLE_TYPE = "mixed";

    @Autowired
    private MoimService moimService;
    @Autowired
    private MoimRepository moimRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private MemberService memberService;
    private Member member;
    private MoimRegisterRequest requestMoim;
    private Long savedMoimId;

    @BeforeEach
    void setUp() {
        memberService.register(new MemberRegisterRequest("asdf@asdf.com", "asdf1234!@", "닉네임"));
        member = memberRepository.findByEmail("asdf@asdf.com")
                .orElseThrow(() -> new MemberException(MEMBER_NOT_FOUND));
        savedMoimId = moimService.register(
                new MoimRegisterRequest(EXAMPLE_NAME, EXAMPLE_INTRODUCTION, EXAMPLE_TYPE, true),
                new MoimTagRequest(List.of("tag1", "tag2", "tag3")),
                null,
                member.getId());
        requestMoim = new MoimRegisterRequest("모임", "모임 소개", "mixed", true);
    }

    @Test
    @DisplayName("[모임 생성] 정상적인 모임 생성")
    void register() {
        MoimRegisterRequest requestMoim = new MoimRegisterRequest("모임", "", "mixed", true);
        moimService.register(requestMoim, new MoimTagRequest(), null, member.getId());
        Moim moim = moimRepository.findByName("모임")
                .orElseThrow(() -> new MoimException(MOIM_NOT_FOUND));
        assertThat(moim.getIntroduction()).isEqualTo("우리 모임을 소개해주세요.");
        assertThat(moim.getType()).isEqualTo(MoimType.MIXED);
        assertThat(moim.getVisible()).isTrue();
    }

    @Test
    @DisplayName("[모임 생성] 이름 중복의 경우")
    void registerFailByDuplicateName() {
        MoimRegisterRequest requestMoim = new MoimRegisterRequest(EXAMPLE_NAME, "", "mixed", true);
        assertThatThrownBy(() -> moimService.register(requestMoim, new MoimTagRequest(), null, member.getId()))
                .isInstanceOf(MoimException.class)
                .hasMessage(MOIM_NAME_DUPLICATE.getMessage());
    }

    @Test
    @DisplayName("[모임 생성] 태그 값이 비어있을 경우")
    void registerFailByEmptyTag() {
        MoimTagRequest requestMoimTag = new MoimTagRequest(List.of("tag1", "tag2", ""));
        assertThatThrownBy(() -> moimService.register(requestMoim, requestMoimTag, null, member.getId()))
                .isInstanceOf(MoimException.class)
                .hasMessage(MOIM_TAG_NULL.getMessage());
    }

    @Test
    @DisplayName("[모임 생성] 태그 값이 중복될 경우")
    void registerFailByDuplicateTag() {
        MoimTagRequest requestMoimTag = new MoimTagRequest(List.of("tag1", "tag1"));
        assertThatThrownBy(() -> moimService.register(requestMoim, requestMoimTag, null, member.getId()))
                .isInstanceOf(MoimException.class)
                .hasMessage(MOIM_TAG_DUPLICATE.getMessage());
    }

    @ParameterizedTest
    @DisplayName("[모임 생성] 태그 값이 유효하지 않을 경우")
    @ValueSource(strings = {"tag12345678", "!@#", "-_a"})
    void registerFailByNotValidTag(final String input) {
        MoimTagRequest requestMoimTag = new MoimTagRequest(List.of(input));
        assertThatThrownBy(() -> moimService.register(requestMoim, requestMoimTag, null, member.getId()))
                .isInstanceOf(MoimException.class)
                .hasMessage(MOIM_TAG_NOT_VALID.getMessage());
    }

    @Test
    @DisplayName("[모임 수정] 정상적인 모임 수정")
    void update() throws IOException {
        moimService.update(savedMoimId, new MoimModifyRequest(EXAMPLE_NAME, "수정되었습니다.", "mixed", true), null, null);
    }

    @Test
    @DisplayName("[모임 수정] 존재하지 않는 모임")
    void updateFailByNotExistId() {
        assertThatThrownBy(() -> moimService.update(0L, new MoimModifyRequest("수정", "소개 수정", "mixed", true), null, null))
                .isInstanceOf(MoimException.class)
                .hasMessage(MOIM_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("[모임 수정] 이름 중복의 경우")
    void updateFailByDuplicateName() {
        Long savedId = moimService.register(new MoimRegisterRequest("모임 예", "모임 소개", "mixed", true), null, null, member.getId());
        Moim moim = moimRepository.findById(savedId).orElseThrow(() -> new MoimException(MOIM_NOT_FOUND));
        assertThatThrownBy(() -> moimService.update(moim.getId(), new MoimModifyRequest(EXAMPLE_NAME, "모임 소개", "mixed", true), new MoimTagRequest(), null))
                .isInstanceOf(MoimException.class)
                .hasMessage(MOIM_NAME_DUPLICATE.getMessage());
    }
}