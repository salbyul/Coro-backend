package com.coro.coro.member.controller;

import com.coro.coro.application.dto.response.ApplicationResponse;
import com.coro.coro.application.service.ApplicationService;
import com.coro.coro.common.response.APIResponse;
import com.coro.coro.member.dto.response.MemberInformationResponse;
import com.coro.coro.member.service.User;
import com.coro.coro.member.dto.request.MemberModificationRequest;
import com.coro.coro.member.dto.request.MemberRegisterRequest;
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

import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
@Builder
public class MemberController implements MemberControllerDocs {

    private final MemberService memberService;
    private final MoimService moimService;
    private final ApplicationService applicationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Override
    public APIResponse register(@RequestBody final MemberRegisterRequest requestMember) {
        Long savedId = memberService.register(requestMember);
        return APIResponse.create()
                .addObject("savedId", savedId);
    }

    @GetMapping("/me")
    @Override
    public APIResponse getInformation(@AuthenticationPrincipal final User user) {
        MemberInformationResponse response = memberService.getInformation(user.getId());
        return APIResponse.create()
                .addObject("member", response);
    }

    @PutMapping("/me")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public APIResponse updatePassword(@RequestBody final MemberModificationRequest requestMember,
                              @AuthenticationPrincipal final User user) {
        memberService.update(user.getId(), requestMember);
        return APIResponse.create();
    }

    @GetMapping("/moims")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public APIResponse getMoimJoinedList(@AuthenticationPrincipal final User user) throws IOException {
        List<Moim> moimList = moimService.getMoimListByMemberId(user.getId());
        List<MoimSearchResponse> summaryMoims = moimService.getSummaryMoim(moimList);
        return APIResponse.create()
                .addObject("list", summaryMoims);
    }

    @GetMapping("/applications")
    @Override
    public APIResponse getApplications(@ModelAttribute(name = "moim") final Long moimId,
                                       @AuthenticationPrincipal final User user,
                                       final String status) {
        List<ApplicationResponse> applicationList = applicationService.getApplication(moimId, user.getId(), status);
        return APIResponse.create()
                .addObject("applicationList", applicationList);
    }
}
