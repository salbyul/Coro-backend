package com.coro.coro.member.domain;

public enum MemberState {
    ACTIVE, SUSPENDED;

    public boolean isActive() {
        return this == ACTIVE;
    }

    public boolean isSuspended() {
        return this == SUSPENDED;
    }
}
