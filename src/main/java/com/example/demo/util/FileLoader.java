package com.example.demo.util;

import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;
 import java.io.IOException;
import java.nio.charset.StandardCharsets;
 @Component
public class FileLoader {
     private final ResourceLoader resourceLoader;
     public FileLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }
     public  String loadFileAsString() throws IOException {
        Resource resource = resourceLoader.getResource("pmass.txt");
        byte[] fileData = FileCopyUtils.copyToByteArray(resource.getInputStream());
        return new String(fileData, StandardCharsets.UTF_8);
    }
}