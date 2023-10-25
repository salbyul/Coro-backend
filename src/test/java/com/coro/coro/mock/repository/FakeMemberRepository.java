package com.coro.coro.mock.repository;

import com.coro.coro.member.domain.Member;
import com.coro.coro.member.service.port.MemberRepository;

import java.util.*;
import java.util.stream.Collectors;

public class FakeMemberRepository implements MemberRepository {

    private final DataSet dataSet;

    public FakeMemberRepository(final DataSet dataSet) {
        this.dataSet = dataSet;
    }


    @Override
    public Long save(final Member member) {
        if (Objects.isNull(member.getId())) {
            Member toBeSaved = Member.builder()
                    .id(dataSet.memberSequence++)
                    .email(member.getEmail())
                    .password(member.getPassword())
                    .nickname(member.getNickname())
                    .build();
            toBeSaved.prePersist();
            dataSet.memberData.put(toBeSaved.getId(), toBeSaved);
            return toBeSaved.getId();
        }
        member.preUpdate();
        dataSet.memberData.put(member.getId(), member);
        return member.getId();
    }

    @Override
    public Optional<Member> findById(final Long id) {
        return Optional.ofNullable(dataSet.memberData.get(id));
    }

    @Override
    public List<Member> findByEmailOrNickname(final String email, final String nickname) {
        return dataSet.memberData.values().stream()
                .filter(member -> member.getEmail().equals(email) || member.getNickname().equals(nickname))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Member> findByNickname(final String nickname) {
        return dataSet.memberData.values().stream()
                .filter(member -> member.getNickname().equals(nickname))
                .findFirst();
    }

    @Override
    public Optional<Member> findByEmail(final String email) {
        return dataSet.memberData.values().stream()
                .filter(member -> member.getEmail().equals(email))
                .findFirst();
    }
}
