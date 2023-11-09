package com.example.demo.util.study;

import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Test {
    public static void main(String[] args) throws Exception {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(Files.newInputStream(Paths.get("D:\\fb\\bzHelp\\test.txt")), StandardCharsets.UTF_8));
        String line = bufferedReader.readLine();
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(replaceLine(line)).append("\n");
        if (line.contains(".java")) {
            stringBuffer.append(replaceLine(line).replace(".class", "$*")).append("\n");
        }
        while (line != null) {
            String lineNext = bufferedReader.readLine();
            stringBuffer.append(replaceLine(lineNext)).append("\n");
            if (line.contains(".java")) {
                stringBuffer.append(replaceLine(lineNext).replace(".class", "$*")).append("\n");
            }

            line = lineNext;
        }
        System.out.println(stringBuffer);
    }

    public static String replaceLine(String line) {
        if (StringUtils.isNotEmpty(line)) {
            return line.replaceAll(".java", ".class").replaceAll(":", "").replaceAll("\\d+", "").trim();
        }
        return "";
    }
}
