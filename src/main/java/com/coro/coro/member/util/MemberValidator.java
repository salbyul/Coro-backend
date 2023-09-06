package com.coro.coro.member.util;

import com.coro.coro.member.domain.Member;
import com.coro.coro.member.exception.MemberException;
import org.springframework.util.StringUtils;

import static com.coro.coro.common.response.error.ErrorType.*;

public class MemberValidator {

    public static void validateRegistration(final Member member) {
        validateEmail(member.getEmail());
        validatePassword(member.getPassword());
        validateNickname(member.getNickname());
    }

    protected static void validateEmail(final String email) {
        if (isEmpty(email)) {
            throw new MemberException(EMAIL_NULL);
        }
        if (!isEmail(email)) {
            throw new MemberException(EMAIL_NOT_VALID);
        }
    }

    private static boolean isEmpty(final String value) {
        return !StringUtils.hasText(value);
    }

    private static boolean isEmail(final String email) {
        return email.matches("^[a-zA-Z0-9+-_.]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$");
    }

    protected static void validatePassword(final String password) {
        if (isEmpty(password)) {
            throw new MemberException(PASSWORD_NULL);
        }
        if (!passwordMatches(password)) {
            throw new MemberException(PASSWORD_NOT_VALID);
        }
    }

    private static boolean passwordMatches(final String password) {
        return password.matches("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{10,29}$");
    }

    protected static void validateNickname(final String nickname) {
        if (isEmpty(nickname)) {
            throw new MemberException(NICKNAME_NULL);
        }
        if (!nicknameMatches(nickname)) {
            throw new MemberException(NICKNAME_NOT_VALID);
        }
    }

    private static boolean nicknameMatches(final String nickname) {
        return nickname.matches("[\\wㄱ-ㅎ가-힣-]{2,15}");
    }
}
