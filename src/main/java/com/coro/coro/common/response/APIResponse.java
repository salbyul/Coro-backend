package com.coro.coro.common.response;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public class APIResponse extends CoroResponse {

    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    private final Map<String, Object> body;

    private APIResponse() {
        super();
        body = new HashMap<>();
    }

    public static APIResponse create() {
        return new APIResponse();
    }

    public APIResponse addObject(final String key, final Object object) {
        this.body.put(key, object);
        return this;
    }
}
