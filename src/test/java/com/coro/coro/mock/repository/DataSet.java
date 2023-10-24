package com.coro.coro.mock.repository;

import com.coro.coro.application.domain.Application;
import com.coro.coro.application.domain.ApplicationAnswer;
import com.coro.coro.application.domain.ApplicationQuestion;
import com.coro.coro.member.domain.Member;
import com.coro.coro.member.domain.MemberPhoto;
import com.coro.coro.moim.domain.Moim;
import com.coro.coro.moim.domain.MoimMember;
import com.coro.coro.moim.domain.MoimPhoto;
import com.coro.coro.moim.domain.MoimTag;
import com.coro.coro.schedule.domain.Schedule;

import java.util.HashMap;
import java.util.Map;

public class DataSet {

    protected final Map<Long, Member> memberData = new HashMap<>();
    protected Long memberSequence = 1L;

    protected final Map<Long, MemberPhoto> memberPhotoData = new HashMap<>();

    protected final Map<Long, Moim> moimData = new HashMap<>();
    protected Long moimSequence = 1L;

    protected final Map<Long, MoimTag> moimTagData = new HashMap<>();
    protected Long moimTagSequence = 1L;

    protected final Map<Long, MoimMember> moimMemberData = new HashMap<>();
    protected Long moimMemberSequence = 1L;

    protected final Map<Long, MoimPhoto> moimPhotoData = new HashMap<>();

    protected final Map<Long, Application> applicationData = new HashMap<>();
    protected Long applicationSequence = 1L;

    protected final Map<Long, ApplicationQuestion> applicationQuestionData = new HashMap<>();
    protected Long applicationQuestionSequence = 1L;

    protected final Map<Long, ApplicationAnswer> applicationAnswerData = new HashMap<>();
    protected Long applicationAnswerSequence = 1L;

    protected final Map<Long, Schedule> scheduleData = new HashMap<>();
    protected Long scheduleSequence = 1L;
}
