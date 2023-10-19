package com.coro.coro.common.utils;

import com.coro.coro.common.service.port.DateTimeHolder;
import com.coro.coro.common.service.port.UUIDHolder;
import org.springframework.web.multipart.MultipartFile;

public class FileSaveUtils {

    @SuppressWarnings("ConstantConditions")
    public static String generateFileName(final MultipartFile multipartFile, final DateTimeHolder dateTimeHolder, final UUIDHolder uuidHolder) {
        String uuid = uuidHolder.generateUUID();
        String now = dateTimeHolder.now();
        String extra = extractExtra(multipartFile.getOriginalFilename());
        return uuid + "[" + now + "]" + extra;
    }

    private static String extractExtra(final String originalName) {
        return originalName.substring(originalName.indexOf("."));
    }
}
