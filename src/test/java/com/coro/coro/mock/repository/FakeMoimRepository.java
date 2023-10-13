package com.coro.coro.mock.repository;

import com.coro.coro.moim.domain.Moim;
import com.coro.coro.moim.domain.MoimMember;
import com.coro.coro.moim.repository.port.MoimRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.*;
import java.util.stream.Collectors;

public class FakeMoimRepository implements MoimRepository {

    private final DataSet dataSet;

    public FakeMoimRepository(final DataSet dataSet) {
        this.dataSet = dataSet;
    }

    @Override
    public Long save(final Moim moim) {
        if (Objects.isNull(moim.getId())) {
            Moim toBeSaved = Moim.builder()
                    .id(dataSet.moimSequence++)
                    .leader(moim.getLeader())
                    .name(moim.getName())
                    .introduction(moim.getIntroduction())
                    .visible(moim.getVisible())
                    .type(moim.getType())
                    .state(moim.getState())
                    .tagList(moim.getTagList())
                    .questionList(moim.getQuestionList())
                    .build();
            toBeSaved.prePersist();
            dataSet.moimData.put(toBeSaved.getId(), toBeSaved);
            return toBeSaved.getId();
        }
        moim.preUpdate();
        dataSet.moimData.put(moim.getId(), moim);
        return moim.getId();
    }

    @Override
    public Optional<Moim> findById(final Long id) {
        return Optional.ofNullable(dataSet.moimData.get(id));
    }

    @Override
    public Page<Moim> findPageByName(final String name, final Pageable pageable) {
        int size = pageable.getPageSize();
        long offset = pageable.getOffset();
        List<Moim> totalContent = dataSet.moimData.values().stream()
                .filter(moim -> moim.getName().contains(name))
                .collect(Collectors.toList());
        int total = totalContent.size();
        List<Moim> content = new ArrayList<>();
        for (long i = offset; i < size * offset; i++) {
            content.add(totalContent.get((int) offset));
        }
        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public Page<Moim> findPageByTag(final String tag, final Pageable pageable) {
        int size = pageable.getPageSize();
        long offset = pageable.getOffset();
        List<Moim> totalContent = new ArrayList<>();
        for (Moim moim : dataSet.moimData.values()) {
            boolean contains = moim.getTagList().stream()
                    .anyMatch(moimTag -> moimTag.getName().contains(tag));
            if (contains) {
                totalContent.add(moim);
            }
        }
        int total = totalContent.size();
        List<Moim> content = new ArrayList<>();
        for (long i = offset; i < size * offset; i++) {
            content.add(totalContent.get((int) offset));
        }
        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public Optional<Moim> findByName(final String name) {
        return dataSet.moimData.values().stream()
                .filter(moim -> moim.getName().equals(name))
                .findFirst();
    }

    @Override
    public List<Moim> findAllByMemberId(final Long memberId) {
        return dataSet.moimMemberData.values().stream()
                .filter(moimMember -> moimMember.getMember().getId().equals(memberId))
                .distinct()
                .map(MoimMember::getMoim)
                .collect(Collectors.toList());
    }
}
