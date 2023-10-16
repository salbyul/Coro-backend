package com.coro.coro.application.service;

import com.coro.coro.application.domain.ApplicationQuestion;
import com.coro.coro.application.dto.request.ApplicationQuestionRegisterRequest;
import com.coro.coro.application.exception.ApplicationException;
import com.coro.coro.member.dto.request.MemberRegisterRequest;
import com.coro.coro.mock.FakeContainer;
import com.coro.coro.moim.domain.Moim;
import com.coro.coro.moim.dto.request.MoimRegisterRequest;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.coro.coro.common.response.error.ErrorType.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertAll;

class ApplicationQuestionServiceTest {

    @Test
    @DisplayName("정상적인 지원 양식 작성")
    void register() throws IOException {
        FakeContainer container = new FakeContainer();
        Long memberId = container.memberService.register(new MemberRegisterRequest("asdf@asdf.com", "asdf1234!@", "닉네임"));
        Long moimId = container.moimService.register(new MoimRegisterRequest("모임", "모임설명", "mixed", true), null, null, null, memberId);
        Moim moim = container.moimRepository.findById(moimId).orElseThrow(() -> new ApplicationException(MOIM_NOT_FOUND));


        List<ApplicationQuestionRegisterRequest> requestQuestions = generateRequestQuestions("질문", 3);
        container.applicationQuestionService.register(moim.getId(), requestQuestions);

        List<ApplicationQuestion> questionList = container.applicationQuestionRepository.findAllByMoimId(moim.getId());

        assertAll(
                () -> assertThat(questionList).extracting(ApplicationQuestion::getContent).containsExactlyInAnyOrder("질문1", "질문2", "질문3"),
                () -> assertThat(questionList).extracting(ApplicationQuestion::getOrder).containsExactlyInAnyOrder(1, 2, 3),
                () -> assertThat(questionList).extracting(ApplicationQuestion::getMoim).containsExactlyInAnyOrder(moim, moim, moim)
        );
    }

    @Test
    @DisplayName("[지원 양식 작성] 이름 초과")
    void registerFailByNameLength() throws IOException {
        FakeContainer container = new FakeContainer();
        Long memberId = container.memberService.register(new MemberRegisterRequest("asdf@asdf.com", "asdf1234!@", "닉네임"));
        Long moimId = container.moimService.register(new MoimRegisterRequest("모임", "모임설명", "mixed", true), null, null, null, memberId);
        Moim moim = container.moimRepository.findById(moimId).orElseThrow(() -> new ApplicationException(MOIM_NOT_FOUND));

        List<ApplicationQuestionRegisterRequest> requestQuestions = new ArrayList<>();
        String value = "글".repeat(201);
        requestQuestions.add(new ApplicationQuestionRegisterRequest(value, 1));

        assertThatThrownBy(() ->
                container.applicationQuestionService.register(moim.getId(), requestQuestions)
        )
                .isInstanceOf(ApplicationException.class)
                .hasMessage(APPLICATION_QUESTION_CONTENT_VALID.getMessage());
    }

    @Test
    @DisplayName("[지원 양식 작성] 순서 중복")
    void registerFailByDuplicateOrders() throws IOException {
        FakeContainer container = new FakeContainer();
        Long memberId = container.memberService.register(new MemberRegisterRequest("asdf@asdf.com", "asdf1234!@", "닉네임"));
        Long moimId = container.moimService.register(new MoimRegisterRequest("모임", "모임설명", "mixed", true), null, null, null, memberId);
        Moim moim = container.moimRepository.findById(moimId).orElseThrow(() -> new ApplicationException(MOIM_NOT_FOUND));

        List<ApplicationQuestionRegisterRequest> requestQuestions = new ArrayList<>();
        requestQuestions.add(new ApplicationQuestionRegisterRequest("질문ㅎㅎ", 1));
        requestQuestions.add(new ApplicationQuestionRegisterRequest("질문", 1));

        assertThatThrownBy(() ->
                container.applicationQuestionService.register(moim.getId(), requestQuestions)
        )
                .isInstanceOf(ApplicationException.class)
                .hasMessage(APPLICATION_QUESTION_ORDERS_VALID.getMessage());
    }

    @Test
    @DisplayName("[지원 양식 작성] 유효하지 않은 순서")
    void registerFailByNotValidOrders() throws IOException {
        FakeContainer container = new FakeContainer();
        Long memberId = container.memberService.register(new MemberRegisterRequest("asdf@asdf.com", "asdf1234!@", "닉네임"));
        Long moimId = container.moimService.register(new MoimRegisterRequest("모임", "모임설명", "mixed", true), null, null, null, memberId);
        Moim moim = container.moimRepository.findById(moimId).orElseThrow(() -> new ApplicationException(MOIM_NOT_FOUND));

        List<ApplicationQuestionRegisterRequest> requestQuestions = new ArrayList<>();
        requestQuestions.add(new ApplicationQuestionRegisterRequest("질문ㅎㅎ", 3));
        requestQuestions.add(new ApplicationQuestionRegisterRequest("질문", 2));

        assertThatThrownBy(() ->
                container.applicationQuestionService.register(moim.getId(), requestQuestions)
        )
                .isInstanceOf(ApplicationException.class)
                .hasMessage(APPLICATION_QUESTION_ORDERS_VALID.getMessage());

        assertThatThrownBy(() ->
                container.applicationQuestionService.register(moim.getId(), List.of(new ApplicationQuestionRegisterRequest("질문", 0)))
        )
                .isInstanceOf(ApplicationException.class)
                .hasMessage(APPLICATION_QUESTION_ORDERS_VALID.getMessage());
    }

    @Test
    @DisplayName("[지원 양식 작성] 10개 초과")
    void registerFailedByOrdersGreaterThanTen() throws IOException {
        FakeContainer container = new FakeContainer();
        Long memberId = container.memberService.register(new MemberRegisterRequest("asdf@asdf.com", "asdf1234!@", "닉네임"));
        Long moimId = container.moimService.register(new MoimRegisterRequest("모임", "모임설명", "mixed", true), null, null, null, memberId);
        Moim moim = container.moimRepository.findById(moimId).orElseThrow(() -> new ApplicationException(MOIM_NOT_FOUND));

        List<ApplicationQuestionRegisterRequest> requestQuestions = generateRequestQuestions("질문", 15);

        assertThatThrownBy(() ->
                container.applicationQuestionService.register(moim.getId(), requestQuestions)
        )
                .isInstanceOf(ApplicationException.class)
                .hasMessage(APPLICATION_QUESTION_GREATER_THAN_MAX.getMessage());
    }

    @SuppressWarnings("SameParameterValue")
    private List<ApplicationQuestionRegisterRequest> generateRequestQuestions(final String content, final int counts) {
        List<ApplicationQuestionRegisterRequest> requestQuestions = new ArrayList<>();
        for (int i = 1; i <= counts; i++) {
            requestQuestions.add(new ApplicationQuestionRegisterRequest(content + i, i));
        }
        return requestQuestions;
    }

    @Test
    @DisplayName("지원 질문 찾기 성공")
    void findQuestionList() throws IOException {
        FakeContainer container = new FakeContainer();
        Long memberId = container.memberService.register(new MemberRegisterRequest("asdf@asdf.com", "asdf1234!@", "닉네임"));
        Long moimId = container.moimService.register(new MoimRegisterRequest("모임", "모임설명", "mixed", true), null, null, null, memberId);
        Moim moim = container.moimRepository.findById(moimId).orElseThrow(() -> new ApplicationException(MOIM_NOT_FOUND));

        container.applicationQuestionService.register(
                moim.getId(),
                generateRequestQuestions("질문", 2)
        );
        List<com.coro.coro.application.domain.ApplicationQuestion> questionList = container.applicationQuestionService.findQuestionList(moim.getId());

        assertAll(
                () -> assertThat(questionList).extracting(com.coro.coro.application.domain.ApplicationQuestion::getContent).containsExactlyInAnyOrder("질문1", "질문2"),
                () -> assertThat(questionList).extracting(com.coro.coro.application.domain.ApplicationQuestion::getOrder).containsExactlyInAnyOrder(1, 2),
                () -> assertThat(questionList).extracting(com.coro.coro.application.domain.ApplicationQuestion::getMoim).containsExactlyInAnyOrder(moim, moim)
        );
    }
}