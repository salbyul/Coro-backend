package com.coro.coro.mock;

import com.coro.coro.application.controller.ApplicationController;
import com.coro.coro.application.repository.port.ApplicationAnswerRepository;
import com.coro.coro.application.repository.port.ApplicationQuestionRepository;
import com.coro.coro.application.repository.port.ApplicationRepository;
import com.coro.coro.application.service.ApplicationQuestionService;
import com.coro.coro.application.service.ApplicationService;
import com.coro.coro.common.service.port.FileTransferor;
import com.coro.coro.member.controller.MemberController;
import com.coro.coro.member.repository.port.MemberPhotoRepository;
import com.coro.coro.member.repository.port.MemberRepository;
import com.coro.coro.member.service.MemberPhotoService;
import com.coro.coro.member.service.MemberService;
import com.coro.coro.common.service.port.DateTimeHolder;
import com.coro.coro.common.service.port.UUIDHolder;
import com.coro.coro.mock.repository.*;
import com.coro.coro.moim.controller.MoimController;
import com.coro.coro.moim.repository.port.MoimMemberRepository;
import com.coro.coro.moim.repository.port.MoimPhotoRepository;
import com.coro.coro.moim.repository.port.MoimRepository;
import com.coro.coro.moim.repository.port.MoimTagRepository;
import com.coro.coro.moim.service.MoimService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class FakeContainer {

    @SuppressWarnings("FieldCanBeLocal")
    private final DataSet dataSet;

    public final MemberPhotoRepository memberPhotoRepository;
    public final MemberRepository memberRepository;
    public final MoimRepository moimRepository;
    public final MoimTagRepository moimTagRepository;
    public final MoimPhotoRepository moimPhotoRepository;
    public final MoimMemberRepository moimMemberRepository;
    public final ApplicationRepository applicationRepository;
    public final ApplicationAnswerRepository applicationAnswerRepository;
    public final ApplicationQuestionRepository applicationQuestionRepository;

    public final MemberService memberService;
    public final MemberPhotoService memberPhotoService;
    public final MoimService moimService;
    public final ApplicationQuestionService applicationQuestionService;
    public final ApplicationService applicationService;

    public final MemberController memberController;
    public final MoimController moimController;
    public final ApplicationController applicationController;

    public final PasswordEncoder passwordEncoder;
    public final UUIDHolder uuidHolder;
    public final DateTimeHolder dateTimeHolder;
    public final FileTransferor fileTransferor;

    public FakeContainer() {
        dataSet = new DataSet();
        this.memberPhotoRepository = new FakeMemberPhotoRepository(dataSet);
        this.memberRepository = new FakeMemberRepository(dataSet);
        this.moimTagRepository = new FakeMoimTagRepository(dataSet);
        this.moimRepository = new FakeMoimRepository(dataSet);
        this.moimPhotoRepository = new FakeMoimPhotoRepository(dataSet);
        this.moimMemberRepository = new FakeMoimMemberRepository(dataSet);
        this.applicationRepository = new FakeApplicationRepository(dataSet);
        this.applicationAnswerRepository = new FakeApplicationAnswerRepository(dataSet);
        this.applicationQuestionRepository = new FakeApplicationQuestionRepository(dataSet);

        this.passwordEncoder = new BCryptPasswordEncoder();
        this.uuidHolder = new FakeUUIDGenerator();
        this.dateTimeHolder = new FakeDateTime();
        this.fileTransferor = new FakeFileTransferor();

        this.memberService = MemberService.builder()
                .memberRepository(this.memberRepository)
                .passwordEncoder(passwordEncoder)
                .tokenProvider(new FakeJwtProvider())
                .build();
        this.memberPhotoService = MemberPhotoService.builder()
                .memberRepository(this.memberRepository)
                .memberPhotoRepository(this.memberPhotoRepository)
                .uuidHolder(this.uuidHolder)
                .dateTimeHolder(this.dateTimeHolder)
                .fileTransferor(this.fileTransferor)
                .build();
        this.moimService = MoimService.builder()
                .moimRepository(this.moimRepository)
                .moimTagRepository(this.moimTagRepository)
                .memberRepository(this.memberRepository)
                .moimPhotoRepository(this.moimPhotoRepository)
                .applicationQuestionRepository(this.applicationQuestionRepository)
                .moimMemberRepository(this.moimMemberRepository)
                .dateTimeHolder(this.dateTimeHolder)
                .uuidHolder(this.uuidHolder)
                .fileTransferor(this.fileTransferor)
                .build();
        this.applicationQuestionService = ApplicationQuestionService.builder()
                .moimRepository(this.moimRepository)
                .applicationQuestionRepository(this.applicationQuestionRepository)
                .build();
        this.applicationService = ApplicationService.builder()
                .applicationQuestionRepository(this.applicationQuestionRepository)
                .memberRepository(this.memberRepository)
                .moimRepository(this.moimRepository)
                .applicationRepository(this.applicationRepository)
                .applicationAnswerRepository(this.applicationAnswerRepository)
                .moimMemberRepository(this.moimMemberRepository)
                .build();

        this.memberController = MemberController.builder()
                .memberService(this.memberService)
                .memberPhotoService(this.memberPhotoService)
                .moimService(this.moimService)
                .applicationService(this.applicationService)
                .build();
        this.moimController = MoimController.builder()
                .moimService(this.moimService)
                .build();
        this.applicationController = ApplicationController.builder()
                .applicationQuestionService(this.applicationQuestionService)
                .applicationService(this.applicationService)
                .build();
    }
}
