package com.coro.coro.member.domain;

import com.coro.coro.common.domain.BaseEntity;
import com.coro.coro.member.dto.request.MemberModifyRequest;
import com.coro.coro.member.exception.MemberException;
import com.coro.coro.member.util.MemberValidator;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.*;
import java.util.List;

import static com.coro.coro.common.response.error.ErrorType.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String email;
    private String password;
    private String nickname;
    private String introduction;
    @Enumerated(EnumType.STRING)
    private MemberState state;

    @Builder
    private Member(final String email, final String password, final String nickname) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        MemberValidator.validateRegistration(this);
    }

    public void encryptPassword(final PasswordEncoder passwordEncoder) {
        this.password = passwordEncoder.encode(password);
    }

    @Override
    public void prePersist() {
        super.prePersist();
        this.introduction = "나를 소개합니다.\uD83D\uDE00";
        this.state = MemberState.ACTIVE;
    }

    public void verifyDuplication(final List<Member> foundMembers) {
        for (Member foundMember : foundMembers) {
            if (foundMember.getEmail().equals(this.email)) {
                throw new MemberException(EMAIL_DUPLICATE);
            }
            if (foundMember.getNickname().equals(this.nickname)) {
                throw new MemberException(NICKNAME_DUPLICATE);
            }
        }
    }

    public void changeTo(final MemberModifyRequest requestMember) {
        this.introduction = requestMember.getIntroduction();
        this.password = requestMember.getNewPassword();
        MemberValidator.validateRegistration(this);
    }
}
