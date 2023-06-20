package com.example.demo.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

public class CheckCodeDiff {


    public static void main(String[] args) throws Exception {
        String localUrl = "D:\\logs\\checkTest\\local";
        String checkUrl = "D:\\logs\\checkTest\\check";
        getFile(localUrl, checkUrl);
    }

    /**
     * 获取两个文件夹
     */
    public static void getFile(String urlOne, String urlTwo) throws Exception {
        ArrayList<String> fileListOne = new ArrayList<String>();
        ArrayList<String> fileListTwo = new ArrayList<String>();
        //获取本地的所有文件路径
        fileListOne = findFileList(new File(urlOne), fileListOne);
        //获取环境上的所有文件路径
        fileListTwo = findFileList(new File(urlTwo), fileListTwo);

        List<String> one=new ArrayList<String>();
        List<String> two=new ArrayList<String>();
        Map<String, String> mapOne = new HashMap<>();
        //先以本地为主，找出环境与本地不同的
        for (int i = 0; i < fileListOne.size(); i++) {
            boolean flag=false;
            String fileOne=fileListOne.get(i).split("local\\\\")[1];
            for (int j = 0; j < fileListTwo.size(); j++) {
                String fileTwo=fileListTwo.get(j).split("check\\\\")[1];
                //文件名相同 则比较文件
                if (fileOne.equals(fileTwo)) {
                    if(!compareFiles(fileListOne.get(i),fileListTwo.get(j))){
                        one.add(fileOne);
                        mapOne.put(fileOne,"");
                    }
                    flag=true;
                }
            }
            if(!flag){
                one.add(fileOne);
                mapOne.put(fileOne,"");
            }
        }
        //再以环境为主，找出本地与环境不同的
        Map<String, String> mapTwo = new HashMap<>();
        for (int i = 0; i < fileListTwo.size(); i++) {
            boolean flag=false;
            String fileTwo=fileListTwo.get(i).split("check\\\\")[1];
            for (int j = 0; j < fileListOne.size(); j++) {
                //截取让两个文件夹从相同的根目录开始
                String fileOne=fileListOne.get(j).split("local\\\\")[1];
                //文件名相同 则比较文件
                if (fileTwo.equals(fileOne)) {
                    if(!compareFiles(fileListTwo.get(i),fileListOne.get(j))){
                        two.add(fileTwo);
                        mapTwo.put(fileTwo,"");
                    }
                    flag=true;
                }
            }
            if(!flag){
                two.add(fileTwo);
                mapTwo.put(fileTwo,"");
            }
        }
        //获取两个list的交集(本地和柜面不相同的文件)
        List<String> intersection = one.stream().filter(two::contains).collect(toList());
        System.out.println("本地和柜面不相同的文件");
        intersection.parallelStream().forEach(System.out :: println);
        //差集(list1 - list2)（本地为主 测试环境没有的文件）
        List<String> reduce1 = one.stream().filter(item -> !two.contains(item)).collect(toList());
        System.out.println("测试环境没有的文件");
        reduce1.parallelStream().forEach(System.out :: println);
        // 差集 (list2 - list1)（测试为主 本地没有的文件）
        List<String> reduce2 = two.stream().filter(item -> !one.contains(item)).collect(toList());
        System.out.println("本地环境没有的文件");
        reduce2.parallelStream().forEach(System.out :: println);
    }

    public static ArrayList<String> findFileList(File dir, ArrayList<String> fileList) throws Exception {
        if (!dir.exists() || !dir.isDirectory()) {// 判断是否存在目录
            return fileList;
        }
        // 读取目录下的所有目录文件信息
        String[] files = dir.list();
        for (int i = 0; i < files.length; i++) {
            File file = new File(dir, files[i]);
            if (file.isFile()) {
                fileList.add(file.getAbsolutePath());
            } else {
                // 回调自身继续查询
                findFileList(file, fileList);
            }
        }
        return fileList;
    }

    public static boolean compareFiles(String fileOne, String fileTwo) throws Exception {
        BufferedReader br1 = null;
        BufferedReader br2 = null;
        boolean areEqual = true;
        try {
            br1 = new BufferedReader(new FileReader(fileOne));
            br2 = new BufferedReader(new FileReader(fileTwo));
            String line1 = br1.readLine();
            String line2 = br2.readLine();
            while (line1 != null || line2 != null) {
                assert line1 != null;
                if (!line1.equals(line2)) {
                    areEqual = false;
                    break;
                }
                line1 = br1.readLine();
                line2 = br2.readLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if (br1 != null){
                br1.close();
            }
            if (br2 !=null){
                br2.close();
            }
        }
        return areEqual;
    }

}
