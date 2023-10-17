package com.coro.coro.mock;

import com.coro.coro.common.service.port.FileTransferor;
import org.springframework.web.multipart.MultipartFile;

public class FakeFileTransferor implements FileTransferor {

    @Override
    public byte[] getFile(final String name, final String path) {
        return new byte[0];
    }

    @Override
    public void saveFile(final MultipartFile multipartFile, final String path, final String name) {

    }
}
