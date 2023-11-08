package com.coro.coro.mock;

import com.coro.coro.application.controller.ApplicationController;
import com.coro.coro.application.service.port.ApplicationAnswerRepository;
import com.coro.coro.application.service.port.ApplicationQuestionRepository;
import com.coro.coro.application.service.port.ApplicationRepository;
import com.coro.coro.application.service.ApplicationQuestionService;
import com.coro.coro.application.service.ApplicationService;
import com.coro.coro.auth.controller.AuthController;
import com.coro.coro.auth.jwt.JwtProvider;
import com.coro.coro.auth.service.AuthService;
import com.coro.coro.auth.service.port.RefreshTokenRepository;
import com.coro.coro.common.service.port.FileTransferor;
import com.coro.coro.member.controller.MemberController;
import com.coro.coro.member.service.port.MemberRepository;
import com.coro.coro.member.service.MemberService;
import com.coro.coro.common.service.port.DateTimeHolder;
import com.coro.coro.common.service.port.UUIDHolder;
import com.coro.coro.mock.repository.*;
import com.coro.coro.moim.controller.MoimController;
import com.coro.coro.moim.service.port.MoimMemberRepository;
import com.coro.coro.moim.service.port.MoimPhotoRepository;
import com.coro.coro.moim.service.port.MoimRepository;
import com.coro.coro.moim.service.port.MoimTagRepository;
import com.coro.coro.moim.service.MoimService;
import com.coro.coro.schedule.controller.ScheduleController;
import com.coro.coro.schedule.service.port.ScheduleRepository;
import com.coro.coro.schedule.service.ScheduleService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class FakeContainer {

    @SuppressWarnings("FieldCanBeLocal")
    private final DataSet dataSet;

    public final MemberRepository memberRepository;
    public final MoimRepository moimRepository;
    public final MoimTagRepository moimTagRepository;
    public final MoimPhotoRepository moimPhotoRepository;
    public final MoimMemberRepository moimMemberRepository;
    public final ApplicationRepository applicationRepository;
    public final ApplicationAnswerRepository applicationAnswerRepository;
    public final ApplicationQuestionRepository applicationQuestionRepository;

    public final MemberService memberService;
    public final MoimService moimService;
    public final ApplicationQuestionService applicationQuestionService;
    public final ApplicationService applicationService;

    public final MemberController memberController;
    public final MoimController moimController;
    public final ApplicationController applicationController;

    public final ScheduleController scheduleController;
    public final ScheduleService scheduleService;
    public final ScheduleRepository scheduleRepository;

    public final AuthService authService;
    public final AuthController authController;
    public final RefreshTokenRepository refreshTokenRepository;

    public final PasswordEncoder passwordEncoder;
    public final UUIDHolder uuidHolder;
    public final DateTimeHolder dateTimeHolder;
    public final FileTransferor fileTransferor;
    public final JwtProvider jwtProvider;

    public FakeContainer() {
        dataSet = new DataSet();
        this.memberRepository = new FakeMemberRepository(dataSet);
        this.moimTagRepository = new FakeMoimTagRepository(dataSet);
        this.moimRepository = new FakeMoimRepository(dataSet);
        this.moimPhotoRepository = new FakeMoimPhotoRepository(dataSet);
        this.moimMemberRepository = new FakeMoimMemberRepository(dataSet);
        this.applicationRepository = new FakeApplicationRepository(dataSet);
        this.applicationAnswerRepository = new FakeApplicationAnswerRepository(dataSet);
        this.applicationQuestionRepository = new FakeApplicationQuestionRepository(dataSet);
        this.scheduleRepository = new FakeScheduleRepository(dataSet);
        this.refreshTokenRepository = new FakeRefreshTokenRepository(dataSet);

        this.passwordEncoder = new BCryptPasswordEncoder();
        this.uuidHolder = new FakeUUIDGenerator();
        this.dateTimeHolder = new FakeDateTime();
        this.fileTransferor = new FakeFileTransferor();
        this.jwtProvider = new FakeJwtProvider();

        this.memberService = MemberService.builder()
                .memberRepository(this.memberRepository)
                .passwordEncoder(passwordEncoder)
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

        this.scheduleService = ScheduleService.builder()
                .scheduleRepository(this.scheduleRepository)
                .moimMemberRepository(this.moimMemberRepository)
                .moimRepository(this.moimRepository)
                .build();
        this.scheduleController = ScheduleController.builder()
                .scheduleService(this.scheduleService)
                .build();

        this.authService = AuthService.builder()
                .refreshTokenRepository(this.refreshTokenRepository)
                .jwtProvider(this.jwtProvider)
                .uuidHolder(this.uuidHolder)
                .memberRepository(this.memberRepository)
                .passwordEncoder(this.passwordEncoder)
                .build();
        this.authController = AuthController.builder()
                .authService(this.authService)
                .build();

    }
}
