package com.coro.coro.mock;

import org.springframework.security.crypto.password.PasswordEncoder;

public class FakePasswordEncoder implements PasswordEncoder {

    @Override
    public String encode(final CharSequence rawPassword) {
        int maxLength = 30;
        return rawPassword + "a".repeat(maxLength - rawPassword.length());
    }

    @Override
    public boolean matches(final CharSequence rawPassword, final String encodedPassword) {
        return encode(rawPassword).equals(encodedPassword);
    }
}
