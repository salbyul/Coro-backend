package com.coro.coro.moim.service;

import com.coro.coro.auth.exception.AuthException;
import com.coro.coro.member.domain.Member;
import com.coro.coro.member.repository.MemberRepository;
import com.coro.coro.moim.domain.Moim;
import com.coro.coro.moim.domain.MoimTag;
import com.coro.coro.moim.domain.MoimType;
import com.coro.coro.moim.dto.request.MoimModifyRequest;
import com.coro.coro.moim.dto.request.MoimRegisterRequest;
import com.coro.coro.moim.dto.request.MoimTagRequest;
import com.coro.coro.moim.exception.MoimException;
import com.coro.coro.moim.repository.MoimRepository;
import com.coro.coro.moim.repository.MoimTagRepository;
import com.coro.coro.moim.validator.MoimTagValidator;
import com.coro.coro.moim.validator.MoimValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.coro.coro.common.response.error.ErrorType.*;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class MoimService {

    private final MoimRepository moimRepository;
    private final MoimTagRepository moimTagRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public Long register(final MoimRegisterRequest requestMoim, final MoimTagRequest requestMoimTag, final Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new AuthException(AUTH_ERROR));

        Moim moim = saveMoim(requestMoim, member);
        saveTag(requestMoimTag, moim);
        return moim.getId();
    }

    private Moim saveMoim(final MoimRegisterRequest requestMoim, final Member member) {
        Moim moim = Moim.builder()
                .leader(member)
                .name(requestMoim.getName())
                .introduction(requestMoim.getIntroduction())
                .visible(requestMoim.getVisible())
                .type(MoimType.getType(requestMoim.getType()))
                .build();

        MoimValidator.validateMoim(moim);
        validateDuplicateName(moim);

        moimRepository.save(moim);
        return moim;
    }

    private void validateDuplicateName(final Moim moim) {
        Optional<Moim> optionalMoim = moimRepository.findByName(moim.getName());
        if (optionalMoim.isPresent()) {
            throw new MoimException(MOIM_NAME_DUPLICATE);
        }
    }

    private void saveTag(final MoimTagRequest requestMoimTag, final Moim moim) {
        if (requestMoimTag == null) {
            return;
        }
        List<MoimTag> tagList = requestMoimTag.getTagList()
                .stream()
                .map(tag -> MoimTag.generateMoimTag(tag, moim))
                .collect(Collectors.toList());

        MoimTagValidator.validateTag(tagList);
        moimTagRepository.saveAll(tagList);
    }

    @Transactional
    public void update(final MoimModifyRequest requestMoim, final MoimTagRequest requestTag) {
        Moim moim = moimRepository.findById(requestMoim.getId())
                .orElseThrow(() -> new MoimException(MOIM_NOT_FOUND));
        if (!requestMoim.getName().equals(moim.getName())) {
            validateDuplicateName(requestMoim);
        }
        moim.changeTo(requestMoim);
        MoimValidator.validateMoim(moim);

        moimTagRepository.deleteAllByMoim(moim);
        saveTag(requestTag, moim);
    }

    private void validateDuplicateName(final MoimModifyRequest requestMoim) {
        boolean isDuplicate = moimRepository.findByName(requestMoim.getName()).isPresent();
        if (isDuplicate) {
            throw new MoimException(MOIM_NAME_DUPLICATE);
        }
    }
}