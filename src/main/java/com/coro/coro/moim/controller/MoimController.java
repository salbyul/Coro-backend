package com.coro.coro.moim.controller;

import com.coro.coro.application.dto.request.ApplicationQuestionRegisterRequest;
import com.coro.coro.common.response.APIResponse;
import com.coro.coro.member.domain.MemberRole;
import com.coro.coro.member.service.User;
import com.coro.coro.moim.annotation.Search;
import com.coro.coro.moim.domain.Moim;
import com.coro.coro.moim.dto.request.*;
import com.coro.coro.moim.dto.response.MoimDetailResponse;
import com.coro.coro.moim.dto.response.MoimMemberResponse;
import com.coro.coro.moim.dto.response.MoimModificationResponse;
import com.coro.coro.moim.dto.response.MoimSearchResponse;
import com.coro.coro.moim.service.MoimService;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@Builder
@RequestMapping("/api/moims")
public class MoimController implements MoimControllerDocs {

    private final MoimService moimService;

    /**
     * 모임의 디테일 정보 획득
     * @param moimId 디테일 정보를 획득할 모임의 Id 값
     * @param user 로그인한 유저
     * @return 모임의 디테일 정보
     * @throws IOException 이미지 파일로 인한 예외
     */
    @GetMapping("/{id}")
    @Override
    public APIResponse detail(@PathVariable("id") final Long moimId, @AuthenticationPrincipal final User user) throws IOException {
        MoimDetailResponse detail = moimService.getDetail(moimId, user.getId());
        return APIResponse.create()
                .addObject("moim", detail);
    }

    /**
     * 모임 검색
     * 모임의 이름 혹은 태그로 검색 가능
     * @param moimSearchRequest 검색 조건 DTO
     * @param pageable 페이징
     * @return 검색 결과
     * @throws IOException 이미지 파일로 인한 예외
     */
    @GetMapping("/search")
    @Override
    public APIResponse search(@Search final MoimSearchRequest moimSearchRequest, final Pageable pageable) throws IOException {
        Page<Moim> result = moimService.search(moimSearchRequest, pageable);
        List<MoimSearchResponse> moimList = moimService.getSummaryMoim(result.getContent());

        return APIResponse.create()
                .addObject("list", moimList)
                .addObject("totalPages", result.getTotalPages())
                .addObject("hasNext", result.hasNext())
                .addObject("hasPrevious", result.hasPrevious())
                .addObject("isFirst", result.isFirst())
                .addObject("isLast", result.isLast());
    }

    /**
     * 모임 생성
     * @param requestMoim 생성할 모임의 데이터가 담긴 객체
     * @param requestTag 생성할 모임의 태그리스트가 담긴 객체
     * @param requestQuestions 생성할 모임의 지원 양식이 담긴 객체
     * @param multipartFile 생성할 모임의 이미지 파일
     * @param user 로그인한 유저
     * @return 생성된 모임의 Id 값
     * @throws IOException 이미지 파일로 인한 예외
     */
    @PostMapping
    @Override
    public APIResponse register(@RequestPart(name = "moim") final MoimRegisterRequest requestMoim,
                                @RequestPart(required = false, name = "tagList") final MoimTagRequest requestTag,
                                @RequestPart(required = false, name = "applicationQuestionList") final List<ApplicationQuestionRegisterRequest> requestQuestions,
                                @RequestPart(name = "photo", required = false) final MultipartFile multipartFile,
                                @AuthenticationPrincipal final User user) throws IOException {
        Long savedId = moimService.register(requestMoim, requestTag, requestQuestions, multipartFile, user.getId());
        return APIResponse.create()
                .addObject("moimId", savedId);
    }

    /**
     * 모임 수정
     * @param moimId 수정할 모임의 Id 값
     * @param requestMoim 수정할 모임 데이터가 담긴 객체
     * @param requestTag 수정된 태그 리스트가 담긴 객체
     * @param requestQuestions 수정된 지원 양식이 담긴 객체
     * @param multipartFile 수정된 이미지 파일
     * @param user 로그인한 유저
     * @return 반환값 없음
     * @throws IOException 이미지 파일로 인한 예외
     */
    @PutMapping("/{id}")
    @Override
    public APIResponse update(@PathVariable("id") Long moimId,
                              @RequestPart(name = "moim") final MoimModificationRequest requestMoim,
                              @RequestPart(name = "tagList") final MoimTagRequest requestTag,
                              @RequestPart(name = "applicationQuestionList") final List<ApplicationQuestionRegisterRequest> requestQuestions,
                              @RequestPart(name = "photo", required = false) final MultipartFile multipartFile,
                              @AuthenticationPrincipal final User user) throws IOException {
        moimService.update(moimId, requestMoim, requestTag, multipartFile, requestQuestions, user.getId());
        return APIResponse.create();
    }

    /**
     * 모임 수정을 위한 모임의 데이터 획득
     * @param moimId 해당 모임의 Id 값
     * @param user 로그인한 유저
     * @return 모임의 데이터가 담긴 객체
     * @throws IOException 이미지 파일로 인한 예외
     */
    @GetMapping("/modification/{id}")
    @Override
    public APIResponse getMoimForModification(@PathVariable("id") final Long moimId, @AuthenticationPrincipal User user) throws IOException {
        MoimModificationResponse detail = moimService.getDetailForModification(moimId, user.getId());
        return APIResponse.create()
                .addObject("detail", detail);
    }

    /**
     * 해당 모임에 가입된 회원 리스트 획득
     * @param moimId 해당 모임의 Id 값
     * @param user 로그인한 유저
     * @return 모임에 가입된 회원 리스트
     */
    @GetMapping("/{moimId}/members")
    @Override
    public APIResponse getMoimMember(@PathVariable("moimId") final Long moimId, @AuthenticationPrincipal final User user) {
        List<MoimMemberResponse> moimMemberResponseList = moimService.getMoimMemberList(moimId);
        MemberRole loggedInMemberRole = moimService.getMemberRole(user.getId(), moimId);
        return APIResponse.create()
                .addObject("moimMemberList", moimMemberResponseList)
                .addObject("role", loggedInMemberRole);
    }

    /**
     * 모임에 가입된 회원들의 역할 변경
     * @param moimId 해당 모임의 Id 값
     * @param requestMoimMember 수정된 회원들의 정보가 담긴 객체
     * @param user 로그인한 유저
     * @return 반환값 없음
     */
    @PutMapping("/{moimId}/members")
    @Override
    public APIResponse changeMoimMember(@PathVariable("moimId") final Long moimId,
                                        @RequestBody final List<MoimMemberModificationRequest> requestMoimMember,
                                        @AuthenticationPrincipal final User user) {
        moimService.modifyMoimMember(moimId, requestMoimMember, user.getId());
        return APIResponse.create();
    }

    /**
     * 회원 추방
     * @param moimId 해당 모임의 Id 값
     * @param moimMemberId 추방될 회원의 MoimMember Id 값
     * @param user 로그인한 유저
     * @return 반환값 없음
     */
    @DeleteMapping("/{moimId}/members")
    @Override
    public APIResponse deportMember(@PathVariable(name = "moimId") final Long moimId, @ModelAttribute(name = "moimMember") final Long moimMemberId, @AuthenticationPrincipal final User user) {
        moimService.deport(moimId, moimMemberId, user.getId());
        return APIResponse.create();
    }
}