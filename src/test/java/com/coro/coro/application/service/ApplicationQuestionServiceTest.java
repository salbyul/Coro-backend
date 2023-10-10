package com.coro.coro.application.service;

import com.coro.coro.application.domain.ApplicationQuestion;
import com.coro.coro.application.dto.request.ApplicationQuestionRegisterRequest;
import com.coro.coro.application.exception.ApplicationException;
import com.coro.coro.member.domain.Member;
import com.coro.coro.member.dto.request.MemberRegisterRequest;
import com.coro.coro.member.repository.MemberRepository;
import com.coro.coro.member.service.MemberService;
import com.coro.coro.moim.domain.Moim;
import com.coro.coro.moim.dto.request.MoimRegisterRequest;
import com.coro.coro.moim.repository.MoimRepository;
import com.coro.coro.moim.service.MoimService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.coro.coro.common.response.error.ErrorType.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest
class ApplicationQuestionServiceTest {

    @Autowired
    private MoimService moimService;
    @Autowired
    private MemberService memberService;
    @Autowired
    private ApplicationQuestionService applicationQuestionService;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private MoimRepository moimRepository;
    private Member member;
    private Moim moim;


    @BeforeEach
    void setUp() throws IOException {
        Long memberId = memberService.register(new MemberRegisterRequest("asdf@asdf.com", "asdf1234!@", "닉네임"));
        Long moimId = moimService.register(new MoimRegisterRequest("모임", "모임설명", "mixed", true), null, null, null, memberId);
        member = memberRepository.findById(memberId).get();
        moim = moimRepository.findById(moimId).get();
    }

    @Test
    @DisplayName("정상적인 지원 양식 작성")
    void register() {
        List<ApplicationQuestionRegisterRequest> requestQuestions = new ArrayList<>();
        for (int i = 1; i < 6; i++) {
            requestQuestions.add(new ApplicationQuestionRegisterRequest("질문" + i, i));
        }
        applicationQuestionService.register(moim.getId(), requestQuestions);
    }

    @Test
    @DisplayName("[지원 양식 작성] 이름 초과")
    void registerFailByNameLength() {
        List<ApplicationQuestionRegisterRequest> requestQuestions = new ArrayList<>();
        String value = "글".repeat(201);
        requestQuestions.add(new ApplicationQuestionRegisterRequest(value, 1));
        assertThat(value.length()).isEqualTo(201);
        assertThatThrownBy(() -> applicationQuestionService.register(moim.getId(), requestQuestions))
                .isInstanceOf(ApplicationException.class)
                .hasMessage(APPLICATION_QUESTION_CONTENT_VALID.getMessage());
    }

    @Test
    @DisplayName("[지원 양식 작성] 순서 중복")
    void registerFailByDuplicateOrders() {
        List<ApplicationQuestionRegisterRequest> requestQuestions = new ArrayList<>();
        requestQuestions.add(new ApplicationQuestionRegisterRequest("질문ㅎㅎ", 1));
        requestQuestions.add(new ApplicationQuestionRegisterRequest("질문", 1));
        assertThatThrownBy(() -> applicationQuestionService.register(moim.getId(), requestQuestions))
                .isInstanceOf(ApplicationException.class)
                .hasMessage(APPLICATION_QUESTION_ORDERS_VALID.getMessage());
    }

    @Test
    @DisplayName("[지원 양식 작성] 유효하지 않은 순서")
    void registerFailByNotValidOrders() {
        List<ApplicationQuestionRegisterRequest> requestQuestions = new ArrayList<>();
        requestQuestions.add(new ApplicationQuestionRegisterRequest("질문ㅎㅎ", 3));
        requestQuestions.add(new ApplicationQuestionRegisterRequest("질문", 2));
        assertThatThrownBy(() -> applicationQuestionService.register(moim.getId(), requestQuestions))
                .isInstanceOf(ApplicationException.class)
                .hasMessage(APPLICATION_QUESTION_ORDERS_VALID.getMessage());

        assertThatThrownBy(() -> applicationQuestionService.register(moim.getId(), List.of(new ApplicationQuestionRegisterRequest("질문", 0))))
                .isInstanceOf(ApplicationException.class)
                .hasMessage(APPLICATION_QUESTION_ORDERS_VALID.getMessage());
    }

    @Test
    @DisplayName("[지원 양식 작성] 10개 초과")
    void registerFailedByOrdersGreaterThanTen() {
        List<ApplicationQuestionRegisterRequest> requestQuestions = new ArrayList<>();
        for (int i = 1; i < 12; i++) {
            requestQuestions.add(new ApplicationQuestionRegisterRequest("질문" + i, i));
        }
        assertThatThrownBy(() -> applicationQuestionService.register(moim.getId(), requestQuestions))
                .isInstanceOf(ApplicationException.class)
                .hasMessage(APPLICATION_QUESTION_MAX.getMessage());
    }

    @Test
    @DisplayName("지원 질문 찾기 성공")
    void findQuestionList() {
        applicationQuestionService.register(
                moim.getId(),
                List.of(
                        new ApplicationQuestionRegisterRequest("질문1", 1),
                        new ApplicationQuestionRegisterRequest("질문2", 2))
        );
        List<ApplicationQuestion> questionList = applicationQuestionService.findQuestionList(moim.getId());

        assertAll(
                () -> assertThat(questionList).extracting(ApplicationQuestion::getContent).containsExactlyInAnyOrder("질문1", "질문2"),
                () -> assertThat(questionList).extracting(ApplicationQuestion::getOrder).containsExactlyInAnyOrder(1, 2),
                () -> assertThat(questionList).extracting(ApplicationQuestion::getMoim).containsExactlyInAnyOrder(moim, moim)
        );
    }
}