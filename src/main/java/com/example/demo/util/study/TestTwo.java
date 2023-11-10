package com.example.demo.util.study;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
@Slf4j
public class TestTwo {
    public static void main(String[] args) throws Exception {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(Files.newInputStream(Paths.get("D:\\fb\\bzHelp\\test.txt")), StandardCharsets.UTF_8));
        String line = bufferedReader.readLine();
        StringBuffer stringBuffer = new StringBuffer();
        if(!line.contains("$*")){
            stringBuffer.append(line).append("\n");
        }
        while (line != null) {
            String lineNext = bufferedReader.readLine();
            if(lineNext!=null&&!lineNext.contains("$*")){
                stringBuffer.append(lineNext).append("\n");
            }
            line = lineNext;
        }
       log.info(String.valueOf(stringBuffer));
    }
}
