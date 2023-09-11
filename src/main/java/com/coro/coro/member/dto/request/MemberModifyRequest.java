package com.coro.coro.member.dto.request;

import lombok.Getter;
import org.springframework.util.StringUtils;

@Getter
public class MemberModifyRequest {

    private String originalPassword;
    private String newPassword;
    private String introduction;

    public boolean isExist() {
        return StringUtils.hasText(originalPassword) && StringUtils.hasText(newPassword) && StringUtils.hasText(introduction);
    }
}
