package com.coro.coro.common.service.adaptor;

import com.coro.coro.common.service.port.FileTransferor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@Service
public class FileTransferorImpl implements FileTransferor {

    @Override
    public byte[] getFile(final String name, final String path) throws IOException {
        return Files.readAllBytes(new File(path + name).toPath());
    }

    @Override
    public void saveFile(final MultipartFile multipartFile, final String path, final String name) throws IOException {
        multipartFile.transferTo(new File(path, name));
    }
}
