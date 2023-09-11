package com.coro.coro.member.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MemberModifyRequest {

    private String originalPassword;
    private String newPassword;
    private String introduction;

    public boolean isExist() {
        return StringUtils.hasText(originalPassword) && StringUtils.hasText(newPassword) && StringUtils.hasText(introduction);
    }
}
