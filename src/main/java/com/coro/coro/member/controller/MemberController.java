package com.coro.coro.member.controller;

import com.coro.coro.application.dto.response.ApplicationResponse;
import com.coro.coro.application.service.ApplicationService;
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

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Override
    public APIResponse register(@RequestBody final MemberRegisterRequest requestMember) {
        log.info("member: {}", requestMember);
        Long savedId = memberService.register(requestMember);
        return APIResponse.create()
                .addObject("savedId", savedId);
    }

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public APIResponse login(@RequestBody final MemberLoginRequest requestMember) {
        String token = memberService.login(requestMember);
        return APIResponse.create()
                .addObject("token", token);
    }

    /*
    이미지, 회원정보 따로 수정 가능
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

    @GetMapping("/moims")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public APIResponse getMoim(@AuthenticationPrincipal final User user) throws IOException{
        List<Moim> moimList = moimService.getMoimListByMemberId(user.getId());
        List<MoimSearchResponse> summaryMoims = moimService.getSummaryMoim(moimList);

        return APIResponse.create()
                .addObject("list", summaryMoims);
    }

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
