package com.coro.coro.member.controller;

import com.coro.coro.application.dto.response.ApplicationResponse;
import com.coro.coro.application.service.ApplicationService;
import com.coro.coro.auth.dto.response.TokenResponse;
import com.coro.coro.common.response.APIResponse;
import com.coro.coro.member.service.User;
import com.coro.coro.member.dto.request.MemberLoginRequest;
import com.coro.coro.member.dto.request.MemberModificationRequest;
import com.coro.coro.member.dto.request.MemberRegisterRequest;
import com.coro.coro.member.service.MemberPhotoService;
import com.coro.coro.member.service.MemberService;
import com.coro.coro.moim.domain.Moim;
import com.coro.coro.moim.dto.response.MoimSearchResponse;
import com.coro.coro.moim.service.MoimService;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
@Builder
public class MemberController implements MemberControllerDocs {

    private final MemberService memberService;
    private final MemberPhotoService memberPhotoService;
    private final MoimService moimService;
    private final ApplicationService applicationService;

    /**
     * 회원가입
     * @param requestMember 가입될 회원의 데이터가 담긴 객체
     * @return 가입된 유저의 Id 값
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Override
    public APIResponse register(@RequestBody final MemberRegisterRequest requestMember) {
        log.info("member: {}", requestMember);
        Long savedId = memberService.register(requestMember);
        return APIResponse.create()
                .addObject("savedId", savedId);
    }

    /**
     * 로그인
     * @param requestMember 로그인할 회원의 데이터가 담긴 객체
     * @return 토큰
     */
    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public APIResponse login(@RequestBody final MemberLoginRequest requestMember) {
        TokenResponse token = memberService.login(requestMember);
        return APIResponse.create()
                .addObject("token", token);
    }

    /**
     * 회원 정보 수정
     * @param memberId 수정할 회원의 Id 값
     * @param multipartFile 수정될 이미지 파일
     * @param requestMember 수정될 회원의 데이터가 담긴 객체
     * @return 반환값 없음
     * @throws IOException 이미지 파일로 인한 예외
     */
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public APIResponse update(@PathVariable("id") Long memberId,
                              @RequestPart(name = "profileImage", required = false) final MultipartFile multipartFile,
                              @RequestPart(name = "member", required = false) final MemberModificationRequest requestMember) throws IOException {
        if (requestMember != null) {
            memberService.update(memberId, requestMember);
        }
        if (multipartFile != null) {
            memberPhotoService.changeProfileImage(memberId, multipartFile);
        }
        return APIResponse.create();
    }

    /**
     * 회원이 가입한 모든 모임 획득
     * @param user 로그인한 유저
     * @return 회원이 가입한 모든 모임 리스트
     * @throws IOException 이미지 파일로 인한 예외
     */
    @GetMapping("/moims")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public APIResponse getMoim(@AuthenticationPrincipal final User user) throws IOException{
        List<Moim> moimList = moimService.getMoimListByMemberId(user.getId());
        List<MoimSearchResponse> summaryMoims = moimService.getSummaryMoim(moimList);

        return APIResponse.create()
                .addObject("list", summaryMoims);
    }

    /**
     * 회원이 특정 모임에 지원한 특정 상태의 지원서의 상태 획득
     * @param moimId 해당 모임의 Id 값
     * @param user 로그인한 유저
     * @param status 획득할 지원서의 상태 값
     * @return 지원서
     */
    @GetMapping("/applications")
    @Override
    public APIResponse getApplication(@ModelAttribute(name = "moim") final Long moimId,
                                      @AuthenticationPrincipal final User user,
                                      final String status) {
        List<ApplicationResponse> applicationList = applicationService.getApplication(moimId, user.getId(), status);
        return APIResponse.create()
                .addObject("applicationList", applicationList);
    }
}
