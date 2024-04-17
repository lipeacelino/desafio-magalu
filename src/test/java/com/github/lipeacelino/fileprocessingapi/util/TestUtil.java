package com.github.lipeacelino.fileprocessingapi.util;

import lombok.SneakyThrows;
import org.springframework.mock.web.MockMultipartFile;

import java.nio.file.Files;
import java.nio.file.Paths;

public abstract class TestUtil {

    @SneakyThrows
    public static MockMultipartFile getMockMultipartFile() {
        var path = Paths.get("src/test/resources", "data_1.txt");
        var file = path.toFile();

        byte[] content = Files.readAllBytes(file.toPath());

        return new MockMultipartFile(
                "file",
                file.getName(),
                "text/plain",
                content
        );
    }


}
