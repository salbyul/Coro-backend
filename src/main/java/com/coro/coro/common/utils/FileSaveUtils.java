package com.coro.coro.common.utils;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;

public class FileSaveUtils {

    @SuppressWarnings("ConstantConditions")
    public static String generateFileName(final MultipartFile multipartFile) {
        String uuid = UUID.randomUUID().toString();
        String now = LocalDateTime.now().toString();
        String extra = extractExtra(multipartFile.getOriginalFilename());
        return uuid + "[" + now + "]" + extra;
    }

    private static String extractExtra(final String originalName) {
        return originalName.substring(originalName.indexOf("."));
    }

    public static void transferFile(final MultipartFile multipartFile, final String path, final String name) throws IOException {
        multipartFile.transferTo(new File(path, name));
    }
}
