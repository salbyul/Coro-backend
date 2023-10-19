package com.coro.coro.common.service.port;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileTransferor {

    byte[] getFile(final String name, final String path) throws IOException;
    void saveFile(final MultipartFile multipartFile, final String path, final String name) throws IOException;
}
