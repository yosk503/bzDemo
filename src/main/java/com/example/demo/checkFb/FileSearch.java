package com.example.demo.checkFb;



import com.example.demo.util.Application;
import com.example.demo.util.ExcelUtils;
import com.example.demo.util.sort;
import org.codehaus.plexus.archiver.tar.TarEntry;
import org.codehaus.plexus.archiver.tar.TarInputStream;
import org.springframework.util.ObjectUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;


/**
 * @version 1.0
 * @className: FileSearch
 * @author: LongY
 * @date: 2023/3/22
 * 检查发版文件下的所有文件，检查依赖以及非法文件
 */
public class FileSearch {
    public static String url;
    public static String excelSuffix;
    public static String tarType;
    public static String[] illegalFile;
    public static String[] checkFiles;

    public static String   checkFile;

    static {
        try {
            url=Application.getProperty("fb_url");
            tarType = Application.getProperty("tarType");
            excelSuffix=Application.getProperty("excelSuffix");
            String illegal=Application.getProperty("illegalFile");
            illegalFile=illegal.split(";");
            checkFile=Application.getProperty("check_File");
            checkFiles=checkFile.split(";");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) throws Exception {
        System.out.println("=====================================发版补丁检查开始============================");
        System.out.println("当前文件路径："+url);
        //判断是否已经解压
        if(new File(url).exists()){
            System.out.println("当前压缩包已经解压，请确认是否使用已解压文件夹或者重新解压\n"+"1-使用，2-重新解压");
            Scanner scanner=new Scanner(System.in);
            String input = scanner.nextLine();
            if("1".equals(input)){
                //直接使用
               startCheck();
            }else if("2".equals(input)){
                unZip();
                startCheck();
            }else {
                //提示输入错误
                throw new Exception("请选择正确的方式！");
            }
        }else {
            unZip();
            startCheck();
        }
        System.out.println("=====================================发版补丁检查结束============================");
    }

    public static void startCheck() throws Exception {
        String[] files = new File(url).list();
        String excelUrl=getExcelName(files);
        files= Arrays.stream(files).filter((str) -> checkFile.contains(str)).collect(Collectors.toList()).toArray(new String[0]);
        Map<String,String> excelMap=getExcelMap(url+"\\"+excelUrl);
        for (int i = 0; i < files.length; i++) {
            File file=new File(url+"\\"+files[i]);
            if(!file.isFile()){
                System.out.println("=====================================开始检查文件"+files[i]+"============================");
                checkFile(excelMap,files[i]);
                for (int j = 0; j < checkFiles.length; j++) {
                    if(files[i].equals(checkFiles[j])&&!"调度".equals(files[i])){
                        sort.findpath(url+"\\"+files[i]+"\\");
                    }
                }
            }
        }
    }

    /**
     * 解压zip压缩包文件
     */
    public static void unZip() throws Exception {
        //获取要解压到的地址
        String[] array=url.split("\\\\");
        String newUrl=String.join("\\\\", Arrays.copyOf(array, array.length - 1));
        //删除文件夹，重新解压
        deleteFileList(new File(url));
        UnzipUtility.unzip(url+".zip",newUrl);
    }

    public static void deleteFileList(File dir) throws Exception {
        if (!dir.exists() || !dir.isDirectory()) {// 判断是否存在目录
            return;
        }
        String[] files = dir.list();// 读取目录下的所有目录文件信息
        for (int i = 0; i < Objects.requireNonNull(files).length; i++) {// 循环，添加文件名或回调自身
            File file = new File(dir, files[i]);
            BasicFileAttributes basicFileAttributes = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
            if (basicFileAttributes.isRegularFile()) {// 如果文件
                long size= basicFileAttributes.size() /(1024*1024);
                boolean result=file.delete();
                if (result) {
                    System.out.println(file.getName()+"文件大小："+size+"M---"+"删除成功");
                }
            } else {// 如果是目录
                deleteFileList(file);// 回调自身继续删除
            }
        }
    }
    /**
     * 按照根目录文件单个循环判断
     */
    public static void checkFile(Map<String,String> excelMap,String files) throws Exception {
        List<Map<String, String>> fileNames = new ArrayList<>();
        //每个压缩包的文件
        fileNames = FileSearch.findFileList(new File(url+"\\"+files), fileNames);
        //压缩包中的路径
        List<String> fileList = new ArrayList<>();
        //全路径
        List<String> fileListPath = new ArrayList<>();
        for (int i = 0; i < fileNames.size(); i++) {
            Map<String, String> map = fileNames.get(i);
            for (String path : map.keySet()) {
                fileListPath.add(path);
                fileList.add(map.get(path));
            }
        }


        Map<String , Long> countMap = fileList.stream().collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
        List<String > compareList = countMap.keySet().stream().filter(key -> countMap.get(key) > 1).collect(Collectors.toList());
        for (int i = 0; i < compareList.size(); i++) {
            for (int j = 0; j < fileListPath.size(); j++) {
                String path=fileListPath.get(j).split(tarType)[1];
                if(path.substring(1).equals(compareList.get(i))){
                    String name=getTarName(fileListPath.get(j));
                    name=name.substring(0,name.indexOf("_"));
                    System.out.println("冲突文件："+compareList.get(i)+"，所在文件的位置："+fileListPath.get(j)+"，涉及到的补丁编号："+name+",登记人为："+excelMap.get(name));
                }
            }
        }
        String illegal= Application.getProperty("illegalFile");
        String[] illegalFile=illegal.split(";");
        for (int i = 0; i <illegalFile.length ; i++) {
            for (int j = 0; j < fileListPath.size(); j++) {
                if(fileListPath.get(j).contains(illegalFile[i])){
                    System.out.println("非法文件的路径为："+fileListPath.get(j));
                }
            }
        }
    }

    /**
     * 遍历整个文件夹，得到返回值
     */
    public static List<Map<String, String>> findFileList(File dir, List<Map<String, String>> fileList) throws Exception {
        // 读取目录下的所有目录文件信息
        String[] files = dir.list();
        for (int i = 0; i < files.length; i++) {
            File file = new File(dir, files[i]);
            if (file.isFile()) {
                if (file.getName().endsWith(".tar")) {
                    fileList.add(getZipFilePath(dir + "\\" + file.getName()));
                }
            } else {
                // 回调自身继续查询
                findFileList(file, fileList);
            }
        }
        return fileList;
    }

    /**
     * 获取压缩包文件地址
     */
    public static Map<String, String> getZipFilePath(String path) throws IOException {
        FileInputStream inputStream = new FileInputStream(path);
        Map<String, String> map = new HashMap<>();
        TarInputStream tarInputStream = new TarInputStream(new BufferedInputStream(inputStream));
        TarEntry ze;
        while ((ze = tarInputStream.getNextEntry()) != null) {
            if(checkIsFile(path + "\\" + ze.getName().replace("/","\\"))){
                map.put(path + "\\" + ze.getName().replace("/","\\"), ze.getName().replace("/","\\"));
            }
        }
        tarInputStream.close();
        inputStream.close();
        return map;
    }

    /**
     * 判断压缩包内的目录是否为文件
     */
    public static Boolean checkIsFile(String tarPath){
        String[] path=tarPath.split("\\\\");
        if(path[path.length-1].contains(".")){
            return true;
        }
        return false;
    }
    /**
     * 遍历excel,获取map
     */
    public static Map<String,String > getExcelMap(String url) throws Exception {
        String fileName =url;
        FileInputStream inputStream = new FileInputStream(fileName);
        List<List<Object>> list = ExcelUtils.getListByExcel(inputStream, fileName);
        Map<String,String > map=new HashMap<>();
        for (int i = 1; i < list.size(); i++) {
            ArrayList arrayList = (ArrayList) list.get(i);
            map.put(arrayList.get(2).toString(),arrayList.get(9).toString());
        }
        return map;
    }

    /**
     * 获取投产文件名称
     */
    public static String getExcelName(String[] path) throws Exception {
        String excelName="";
        for(String url:path){
            if(url.contains(excelSuffix)){
                excelName=url;
            }
        }
        if(ObjectUtils.isEmpty(excelName)){
            throw new Exception("未找到投产文件excel，请放在投产文件根目录");
        }
        return excelName;
    }

    /**
     * 获取tar包的名称
     */
    public static String getTarName(String sameUrl){
        String tarName="";
        String[] arr=sameUrl.split("\\\\");
        for (String name:arr){
            if(name.contains("tar")){
                tarName=name;
            }
        }
        return tarName;
    }

}
