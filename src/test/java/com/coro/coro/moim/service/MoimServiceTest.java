package com.coro.coro.moim.service;

import com.coro.coro.application.dto.request.ApplicationQuestionRegisterRequest;
import com.coro.coro.application.dto.response.ApplicationQuestionResponse;
import com.coro.coro.member.domain.Member;
import com.coro.coro.member.domain.MemberRole;
import com.coro.coro.member.dto.request.MemberRegisterRequest;
import com.coro.coro.member.exception.MemberException;
import com.coro.coro.member.repository.MemberRepository;
import com.coro.coro.member.service.MemberService;
import com.coro.coro.moim.domain.Moim;
import com.coro.coro.moim.domain.MoimMember;
import com.coro.coro.moim.domain.MoimState;
import com.coro.coro.moim.domain.MoimType;
import com.coro.coro.moim.dto.request.MoimMemberModificationRequest;
import com.coro.coro.moim.dto.request.MoimModificationRequest;
import com.coro.coro.moim.dto.request.MoimRegisterRequest;
import com.coro.coro.moim.dto.request.MoimTagRequest;
import com.coro.coro.moim.dto.response.MoimDetailResponse;
import com.coro.coro.moim.dto.response.MoimMemberResponse;
import com.coro.coro.moim.dto.response.MoimModificationResponse;
import com.coro.coro.moim.exception.MoimException;
import com.coro.coro.moim.repository.MoimMemberRepository;
import com.coro.coro.moim.repository.MoimRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.IOException;
import java.util.List;

import static com.coro.coro.common.response.error.ErrorType.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertAll;

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
    @Autowired
    private MoimMemberRepository moimMemberRepository;
    @PersistenceContext
    private EntityManager em;
    private Member member;
    private MoimRegisterRequest requestMoim;
    private Long savedMoimId;
    private MoimMember moimMember;

    @BeforeEach
    void setUp() throws IOException {
        memberService.register(new MemberRegisterRequest("asdf@asdf.com", "asdf1234!@", "닉네임"));
        member = memberRepository.findByEmail("asdf@asdf.com")
                .orElseThrow(() -> new MemberException(MEMBER_NOT_FOUND));
        MoimTagRequest requestMoimTag = new MoimTagRequest(List.of("tag1", "tag2", "tag3"));
        List<ApplicationQuestionRegisterRequest> questionRequests = List.of(new ApplicationQuestionRegisterRequest("질문1", 1), new ApplicationQuestionRegisterRequest("질문2", 2));
        savedMoimId = moimService.register(
                new MoimRegisterRequest(EXAMPLE_NAME, EXAMPLE_INTRODUCTION, EXAMPLE_TYPE, true),
                requestMoimTag,
                questionRequests,
                null,
                member.getId());
        requestMoim = new MoimRegisterRequest("모임", "모임 소개", "mixed", true);
        moimMember = moimMemberRepository.findByMoimIdAndMemberId(savedMoimId, member.getId()).get();
        em.clear();
    }

    @Test
    @DisplayName("[모임 생성] 정상적인 모임 생성")
    void register() throws IOException {
        MoimRegisterRequest requestMoim = new MoimRegisterRequest("모임", "", "mixed", true);
        moimService.register(requestMoim, new MoimTagRequest(), null, null, member.getId());
        Moim moim = moimRepository.findByName("모임")
                .orElseThrow(() -> new MoimException(MOIM_NOT_FOUND));
        assertAll(
                () -> assertThat(moim.getIntroduction()).isEqualTo("우리 모임을 소개해주세요."),
                () -> assertThat(moim.getType()).isEqualTo(MoimType.MIXED),
                () -> assertThat(moim.getVisible()).isTrue()
        );
    }

    @Test
    @DisplayName("[모임 생성] 이름 중복의 경우")
    void registerFailByDuplicateName() {
        MoimRegisterRequest requestMoim = new MoimRegisterRequest(EXAMPLE_NAME, "", "mixed", true);
        assertThatThrownBy(() -> moimService.register(requestMoim, new MoimTagRequest(), null, null, member.getId()))
                .isInstanceOf(MoimException.class)
                .hasMessage(MOIM_NAME_DUPLICATE.getMessage());
    }

    @Test
    @DisplayName("[모임 생성] 태그 값이 비어있을 경우")
    void registerFailByEmptyTag() {
        MoimTagRequest requestMoimTag = new MoimTagRequest(List.of("tag1", "tag2", ""));
        assertThatThrownBy(() -> moimService.register(requestMoim, requestMoimTag, null, null, member.getId()))
                .isInstanceOf(MoimException.class)
                .hasMessage(MOIM_TAG_NULL.getMessage());
    }

    @Test
    @DisplayName("[모임 생성] 태그 값이 중복될 경우")
    void registerFailByDuplicateTag() {
        MoimTagRequest requestMoimTag = new MoimTagRequest(List.of("tag1", "tag1"));
        assertThatThrownBy(() -> moimService.register(requestMoim, requestMoimTag, null, null, member.getId()))
                .isInstanceOf(MoimException.class)
                .hasMessage(MOIM_TAG_DUPLICATE.getMessage());
    }

    @ParameterizedTest
    @DisplayName("[모임 생성] 태그 값이 유효하지 않을 경우")
    @ValueSource(strings = {"tag12345678", "!@#", "-_a"})
    void registerFailByNotValidTag(final String input) {
        MoimTagRequest requestMoimTag = new MoimTagRequest(List.of(input));
        assertThatThrownBy(() -> moimService.register(requestMoim, requestMoimTag, null, null, member.getId()))
                .isInstanceOf(MoimException.class)
                .hasMessage(MOIM_TAG_NOT_VALID.getMessage());
    }

    @Test
    @DisplayName("[모임 수정] 정상적인 모임 수정")
    void update() throws IOException {
        MoimTagRequest moimTagRequest = new MoimTagRequest(List.of("tag1", "tag2", "tag3"));
        List<ApplicationQuestionRegisterRequest> requestQuestions = List.of(new ApplicationQuestionRegisterRequest("question1", 1), new ApplicationQuestionRegisterRequest("question2", 2));
        moimService.update(savedMoimId, new MoimModificationRequest(EXAMPLE_NAME, "수정되었습니다.", "mixed", true, false), moimTagRequest, null, requestQuestions, member.getId());
    }

    @Test
    @DisplayName("[모임 수정] 존재하지 않는 모임")
    void updateFailByNotExistId() {
        assertThatThrownBy(() -> moimService.update(0L, new MoimModificationRequest("수정", "소개 수정", "mixed", true, false), null, null, null, member.getId()))
                .isInstanceOf(MoimException.class)
                .hasMessage(MOIM_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("[모임 수정] 이름 중복의 경우")
    void updateFailByDuplicateName() throws IOException {
        Long moimId = moimService.register(new MoimRegisterRequest("모임 예", "모임 소개", "mixed", true), null, null, null, member.getId());
        assertThatThrownBy(() -> moimService.update(moimId, new MoimModificationRequest(EXAMPLE_NAME, "모임 소개", "mixed", true, false), new MoimTagRequest(), null, null, member.getId()))
                .isInstanceOf(MoimException.class)
                .hasMessage(MOIM_NAME_DUPLICATE.getMessage());
    }

    @Test
    @DisplayName("모임 디테일 정보 성공")
    void getDetail() throws IOException {
        MoimDetailResponse detail = moimService.getDetail(savedMoimId, member.getId());
        assertAll(
                () -> assertThat(detail.getName()).isEqualTo(EXAMPLE_NAME),
                () -> assertThat(detail.getIntroduction()).isEqualTo(EXAMPLE_INTRODUCTION),
                () -> assertThat(detail.isJoined()).isTrue(),
                () -> assertThat(detail.isCanManage()).isTrue(),
                () -> assertThat(detail.getTagList()).size().isEqualTo(3),
                () ->assertThat(detail.getTagList()).containsExactlyInAnyOrder("tag1", "tag2", "tag3")
        );
    }

    @Test
    @DisplayName("모임 디테일 정보 획득실패 - 올바르지 않는 모임 Id값")
    void getDetailFailByNotValidMoimId() {
        assertThatThrownBy(() -> {
            moimService.getDetail(19999999L, 1L);
        }).isInstanceOf(MoimException.class)
                .hasMessage(MOIM_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("모임 수정을 위한 데이터 획득 성공")
    void getDetailForModification() throws IOException {
        MoimModificationResponse result = moimService.getDetailForModification(savedMoimId, member.getId());
        assertAll(
                () -> assertThat(result.getName()).isEqualTo(EXAMPLE_NAME),
                () -> assertThat(result.getIntroduction()).isEqualTo(EXAMPLE_INTRODUCTION),
                () -> assertThat(result.isVisible()).isTrue(),
                () -> assertThat(result.getType()).isEqualTo(MoimType.MIXED.toString()),
                () -> assertThat(result.getTagList()).size().isEqualTo(3),
                () -> assertThat(result.getTagList()).containsExactlyInAnyOrder("tag1", "tag2", "tag3"),
                () -> assertThat(result.getTagList()).containsExactlyInAnyOrder("tag1", "tag2", "tag3"),
                () -> assertThat(result.getApplicationQuestionList()).size().isEqualTo(2),
                () -> assertThat(result.getApplicationQuestionList()).extracting(ApplicationQuestionResponse::getContent).containsExactlyInAnyOrder("질문1", "질문2")
        );
    }

    @Test
    @DisplayName("모임 수정을 위한 데이터 획득 실패 - 올바르지 않는 모임 Id 값")
    void getDetailForModificationFailByNotValidMoimId() {
        assertThatThrownBy(() -> {
            moimService.getDetailForModification(9999999L, member.getId());
        }).isInstanceOf(MoimException.class)
                .hasMessage(MOIM_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("가입된 모든 모임 획득 성공")
    void getMoimListByMemberId() throws IOException {
        moimService.register(new MoimRegisterRequest("모임2", "모임 설명", "faceToFace", true), null, null, null, member.getId());
        List<Moim> moimList = moimService.getMoimListByMemberId(member.getId());
        assertAll(
                () -> assertThat(moimList).size().isEqualTo(2),
                () -> assertThat(moimList).extracting(Moim::getName).containsExactlyInAnyOrder(EXAMPLE_NAME, "모임2"),
                () -> assertThat(moimList).extracting(Moim::getIntroduction).containsExactlyInAnyOrder(EXAMPLE_INTRODUCTION, "모임 설명"),
                () -> assertThat(moimList).extracting(Moim::getVisible).containsExactlyInAnyOrder(true, true),
                () -> assertThat(moimList).extracting(Moim::getType).containsExactlyInAnyOrder(MoimType.MIXED, MoimType.FACE_TO_FACE),
                () -> assertThat(moimList).extracting(Moim::getState).containsExactlyInAnyOrder(MoimState.ACTIVE, MoimState.ACTIVE)
        );
    }

    @Test
    @DisplayName("모든 모임 멤버 획득 성공")
    void getMoimMemberList() {
        List<MoimMemberResponse> moimMemberList = moimService.getMoimMemberList(savedMoimId);
        assertAll(
                () -> assertThat(moimMemberList).size().isEqualTo(1),
                () -> assertThat(moimMemberList).extracting(MoimMemberResponse::getMemberName).containsExactlyInAnyOrder("닉네임"),
                () -> assertThat(moimMemberList).extracting(MoimMemberResponse::getRole).containsExactlyInAnyOrder(MemberRole.LEADER)
        );
    }

    @Test
    @DisplayName("모임 멤버 수정 성공")
    void modifyMoimMember() {
        Long newMemberId = memberService.register(new MemberRegisterRequest("a@a.com", "asdf1234!@", "회원2"));
        Member newMember = memberRepository.findById(newMemberId).get();
        MoimMember newMoimMember = moimMemberRepository.save(MoimMember.generate(moimRepository.findById(savedMoimId).get(), newMember, MemberRole.USER));
        moimService.modifyMoimMember(savedMoimId, List.of(new MoimMemberModificationRequest(moimMember.getId(), "닉네임", MemberRole.LEADER), new MoimMemberModificationRequest(newMoimMember.getId(), "회원2", MemberRole.MANAGER)), member.getId());
        assertThat(newMoimMember.getMember()).isEqualTo(newMember);
        assertThat(newMoimMember.getMoim().getName()).isEqualTo(EXAMPLE_NAME);
        assertThat(newMoimMember.getRole()).isEqualTo(MemberRole.MANAGER);
    }

    @Test
    @DisplayName("모임 멤버 수정 실패 - 리더 중복")
    void modifyMoimMemberFailByDuplicateLeader() {
        Long newMemberId = memberService.register(new MemberRegisterRequest("a@a.com", "asdf1234!@", "회원2"));
        Member newMember = memberRepository.findById(newMemberId).get();
        MoimMember newMoimMember = moimMemberRepository.save(MoimMember.generate(moimRepository.findById(savedMoimId).get(), newMember, MemberRole.USER));
        assertThatThrownBy(() -> {
            moimService.modifyMoimMember(savedMoimId,
                    List.of(new MoimMemberModificationRequest(newMoimMember.getId(),
                            "회원2",
                            MemberRole.LEADER),
                            new MoimMemberModificationRequest(moimMember.getId(),
                                    "닉네임",
                                    MemberRole.LEADER)
                            ), member.getId());
        }).isInstanceOf(MoimException.class)
                .hasMessage(MOIM_MEMBER_NOT_VALID.getMessage());
    }

    @Test
    @DisplayName("모임 멤버 수정 실패 - 리더가 존재하지 않을 경우")
    void modifyMoimMemberFailByNoLeader() {
        Long newMemberId = memberService.register(new MemberRegisterRequest("a@a.com", "asdf1234!@", "회원2"));
        Member newMember = memberRepository.findById(newMemberId).get();
        MoimMember newMoimMember = moimMemberRepository.save(MoimMember.generate(moimRepository.findById(savedMoimId).get(), newMember, MemberRole.USER));
        assertThatThrownBy(() -> {
            moimService.modifyMoimMember(savedMoimId,
                    List.of(new MoimMemberModificationRequest(newMoimMember.getId(),
                            "회원2",
                            MemberRole.MANAGER),
                            new MoimMemberModificationRequest(moimMember.getId(),
                                    "닉네임",
                                    MemberRole.MANAGER)
                            ), member.getId());
        }).isInstanceOf(MoimException.class)
                .hasMessage(MOIM_MEMBER_NOT_VALID.getMessage());
    }

    @Test
    @DisplayName("모임 멤버 수정 실패 - 모든 모임 멤버가 아닌 경우")
    void modifyMoimMemberFailByNotAllMoimMember() {
        Long newMemberId = memberService.register(new MemberRegisterRequest("a@a.com", "asdf1234!@", "회원2"));
        Member newMember = memberRepository.findById(newMemberId).get();
        MoimMember newMoimMember = moimMemberRepository.save(MoimMember.generate(moimRepository.findById(savedMoimId).get(), newMember, MemberRole.USER));
        assertThatThrownBy(() -> {
            moimService.modifyMoimMember(savedMoimId,
                    List.of(new MoimMemberModificationRequest(newMoimMember.getId(),
                            "회원2",
                            MemberRole.LEADER)),
                            member.getId());
        }).isInstanceOf(MoimException.class)
                .hasMessage(MOIM_MEMBER_NOT_VALID.getMessage());
    }

    @Test
    @DisplayName("모임 멤버 수정 실패 - 올바르지 않은 모임 멤버의 경우")
    void modifyMoimMemberFailByNotValidMoimMember() {
        Long newMemberId = memberService.register(new MemberRegisterRequest("a@a.com", "asdf1234!@", "회원2"));
        Member newMember = memberRepository.findById(newMemberId).get();
        MoimMember newMoimMember = moimMemberRepository.save(MoimMember.generate(moimRepository.findById(savedMoimId).get(), newMember, MemberRole.USER));
        assertThatThrownBy(() -> {
            moimService.modifyMoimMember(savedMoimId,
                    List.of(new MoimMemberModificationRequest(
                            newMoimMember.getId(),
                            "회원2",
                            MemberRole.LEADER),
                            new MoimMemberModificationRequest(
                                    99999L,
                            "asdf",
                            MemberRole.USER
                            )),
                            member.getId());
        }).isInstanceOf(MoimException.class)
                .hasMessage(MOIM_MEMBER_NOT_VALID.getMessage());
    }

    @Test
    @DisplayName("멤버의 등급 획득 성공")
    void getMemberRole() {
        MemberRole memberRole = moimService.getMemberRole(member.getId(), savedMoimId);
        assertThat(memberRole.isLeader()).isTrue();
        assertThat(memberRole.isNotLeader()).isFalse();
    }

    @Test
    @DisplayName("멤버의 등급 획득 실패 - 올바르지 않은 회원 Id")
    void getMemberRoleFailByNotValidMemberId() {
        assertThatThrownBy(() -> {
            moimService.getMemberRole(999999L, savedMoimId);
        }).isInstanceOf(MoimException.class)
                .hasMessage(MOIM_MEMBER_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("멤버의 등급 획득 실패 - 올바르지 않은 모임 Id")
    void getMemberRoleFailByNotValidMoimId() {
        assertThatThrownBy(() -> {
            moimService.getMemberRole(member.getId(), 9999999L);
        }).isInstanceOf(MoimException.class)
                .hasMessage(MOIM_MEMBER_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("정상적인 회원 추방")
    void deport() {
        Long newMemberId = memberService.register(new MemberRegisterRequest("a@a.com", "asdf1234!@", "회원2"));
        Member newMember = memberRepository.findById(newMemberId).get();
        MoimMember newMoimMember = moimMemberRepository.save(MoimMember.generate(moimRepository.findById(savedMoimId).get(), newMember, MemberRole.USER));
        moimService.deport(savedMoimId, newMoimMember.getId(), member.getId());
        List<MoimMember> moimMemberList = moimMemberRepository.findAllByMoimId(savedMoimId);
        assertThat(moimMemberList).size().isEqualTo(1);
        assertThat(moimMemberList).extracting(MoimMember::getMember)
                .extracting(Member::getNickname)
                .containsExactlyInAnyOrder("닉네임");
    }

    @Test
    @DisplayName("회원 추방 실패 - 올바르지 않은 모임 Id")
    void deportFailByNotValidMoimId() {
        Long newMemberId = memberService.register(new MemberRegisterRequest("a@a.com", "asdf1234!@", "회원2"));
        Member newMember = memberRepository.findById(newMemberId).get();
        MoimMember newMoimMember = moimMemberRepository.save(MoimMember.generate(moimRepository.findById(savedMoimId).get(), newMember, MemberRole.USER));
        assertThatThrownBy(() -> {
            moimService.deport(999999L, newMoimMember.getId(), member.getId());
        }).isInstanceOf(MoimException.class)
                .hasMessage(MOIM_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("회원 추방 실패 - 권한이 없을 경우")
    void deportFailByForbidden() {
        Long newMemberId = memberService.register(new MemberRegisterRequest("a@a.com", "asdf1234!@", "회원2"));
        Member newMember = memberRepository.findById(newMemberId).get();
        MoimMember newMoimMember = moimMemberRepository.save(MoimMember.generate(moimRepository.findById(savedMoimId).get(), newMember, MemberRole.USER));
        assertThatThrownBy(() -> {
            moimService.deport(savedMoimId, newMoimMember.getId(), newMemberId);
        }).isInstanceOf(MoimException.class)
                .hasMessage(MOIM_FORBIDDEN.getMessage());
    }

    @Test
    @DisplayName("회원 추방 실패 - 리더 추방의 경우")
    void deportFailByDeportLeader() {
        Long newMemberId = memberService.register(new MemberRegisterRequest("a@a.com", "asdf1234!@", "회원2"));
        Member newMember = memberRepository.findById(newMemberId).get();
        moimMemberRepository.save(MoimMember.generate(moimRepository.findById(savedMoimId).get(), newMember, MemberRole.MANAGER));
        assertThatThrownBy(() -> {
            moimService.deport(savedMoimId, moimMember.getId(), newMemberId);
        }).isInstanceOf(MoimException.class)
                .hasMessage(MOIM_FORBIDDEN.getMessage());
    }
}