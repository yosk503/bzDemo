package com.example.demo.util.bzUtil;


import cn.hutool.core.lang.Pair;

import java.util.ArrayList;
import java.util.List;

public class LogFileReader {
//    public static void main(String[] args) {
//        String inputFile = "D:\\桌面\\日志\\20230909\\cfs.log.2023-09-09.1"; // 输入日志文件路径
//        String outputFile = "D:\\桌面\\日志\\20230909\\test.txt"; // 输出日志文件路径
//        int startLine = 1034109; // 起始行号
//        int endLine = 1051119; // 结束行号
//
//        try {
//            BufferedReader reader = new BufferedReader(new FileReader(inputFile));
//            BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));
//            String line;
//            int currentLine = 0;
//            int count=0;
//            // 读取日志文件并写入指定行范围的数据
//            while ((line = reader.readLine()) != null) {
//                currentLine++;
//                if (currentLine >= startLine && currentLine <= endLine) {
//                    writer.write(line);
//                    writer.newLine();
//                    if(line.contains("</BusiBillCode>")){
//                        count++;
//                    }
//                }
//                if (currentLine > endLine) {
//                    break;
//                }
//            }
//
//            reader.close();
//            writer.close();
//
//            System.out.println("日志数据已成功写入到输出文件。");
//            System.out.println("一共有："+count+" 条数据");
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }


    public static void main(String[] args) {
        Pair<List<String>,List<String>> pair=LogFileReader.getTest();
        System.out.println(pair);
    }

    public static Pair<List<String>,List<String>> getTest(){
        List<String> listOne=new ArrayList<>();
        List<String> listTwo=new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            listTwo.add(i+"");
            listOne.add(i+1+"");
        }
         return new Pair<>(listOne,listTwo);
    }
}