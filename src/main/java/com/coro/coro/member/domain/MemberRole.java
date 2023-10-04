package com.coro.coro.member.domain;

public enum MemberRole {
    USER, MANAGER, LEADER;

    public boolean isNotLeader() {
        return this != LEADER;
    }

    public boolean isLeader() {
        return this == LEADER;
    }
}
