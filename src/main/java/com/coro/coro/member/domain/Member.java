package com.coro.coro.member.domain;

import com.coro.coro.common.domain.BaseEntity;
import com.coro.coro.member.dto.request.MemberModificationRequest;
import com.coro.coro.member.exception.MemberException;
import com.coro.coro.member.validator.MemberValidator;
import com.coro.coro.moim.domain.Moim;
import lombok.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static com.coro.coro.common.response.error.ErrorType.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "member")
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String email;
    private String password;
    private String nickname;
    private String introduction;

    @Builder.Default
    @OneToMany(mappedBy = "leader")
    private List<Moim> moimList = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private MemberState state;

    @Override
    public void prePersist() {
        super.prePersist();
        this.introduction = "나를 소개합니다.\uD83D\uDE00";
        this.state = MemberState.ACTIVE;
    }

    public void encryptPassword(final PasswordEncoder passwordEncoder) {
        this.password = passwordEncoder.encode(password);
    }

    public void verifyDuplication(final List<Member> memberList) {
        for (Member member : memberList) {
            if (member.getEmail().equals(this.email)) {
                throw new MemberException(MEMBER_DUPLICATE_EMAIL);
            }
            if (member.getNickname().equals(this.nickname)) {
                throw new MemberException(MEMBER_DUPLICATE_NICKNAME);
            }
        }
    }

    public void update(final MemberModificationRequest requestMember, final PasswordEncoder passwordEncoder) {
        if (!isRightPassword(requestMember.getOriginalPassword(), passwordEncoder)) {
            throw new MemberException(MEMBER_NOT_VALID_PASSWORD);
        }
        this.introduction = requestMember.getIntroduction();
        this.password = requestMember.getNewPassword();

        MemberValidator.validateRegistration(this);
        this.password = passwordEncoder.encode(this.password);
    }

    public boolean isRightPassword(final String password, final PasswordEncoder passwordEncoder) {
        return passwordEncoder.matches(password, this.password);
    }
}
