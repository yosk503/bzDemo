package com.example.demo.checkFb;



import com.example.demo.util.ExcelUtils;
import org.codehaus.plexus.archiver.tar.TarEntry;
import org.codehaus.plexus.archiver.tar.TarInputStream;
import org.springframework.util.ObjectUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;


/**
 * @version 1.0
 * @className: FileSearch
 * @author: LongY
 * @date: 2023/3/22
 */
public class FileSearch {
    public static String url = "D:\\logs\\shiro";
    public static String excelSuffix="XLSX";
    public static String[] illegalFile=new String[]{"serialConsole.log.2023-03-15.3"};

    public static void main(String[] args) throws Exception {
        System.out.println("=====================================发版补丁检查开始============================");
        String[] files = new File(url).list();
        String excelUrl=getExcelName(files);
        Map<String,String> excelMap=getExcelMap(url+"\\"+excelUrl);
        for (int i = 0; i < files.length; i++) {
            File file=new File(url+"\\"+files[i]);
            if(!file.isFile()){
                System.out.println("=====================================开始检查文件"+files[i]+"============================");
                checkFile(excelMap,files[i]);
            }
        }
        System.out.println("=====================================发版补丁检查结束============================");
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
        List<String > compareList = countMap.keySet().stream().filter(key -> countMap.get(key) > 1).distinct().collect(Collectors.toList());
        for (int i = 0; i < compareList.size(); i++) {
            for (int j = 0; j < fileListPath.size(); j++) {
                String path=fileListPath.get(j).split("tar")[1];
                if(path.substring(1).equals(compareList.get(i))){
                    String name=getTarName(fileListPath.get(j));
                    System.out.println("冲突文件："+compareList.get(i)+"，所在文件的位置："+fileListPath.get(j)+"，涉及到的补丁编号："+name+",登记人为："+excelMap.get(name));
                }
            }
        }
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
        String[] files = dir.list();// 读取目录下的所有目录文件信息
        for (int i = 0; i < files.length; i++) {
            File file = new File(dir, files[i]);
            if (file.isFile()) {
                if (file.getName().endsWith(".tar")) {
                    fileList.add(getZipFilePath(dir + "\\" + file.getName()));
                }
            } else {
                findFileList(file, fileList);// 回调自身继续查询
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
            map.put(path + "\\" + ze.getName(), ze.getName());
        }
        tarInputStream.close();
        inputStream.close();
        return map;
    }

    /**
     * 遍历excel,获取map
     */
    public static Map<String,String > getExcelMap(String url) throws Exception {
        String fileName =url;
        FileInputStream inputStream = new FileInputStream(fileName);
        List<List<Object>> list = ExcelUtils.getListByExcel(inputStream, fileName);
        Map<String,String > map=new HashMap<>();
        for (int i = 1; i < list.size()-1; i++) {
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
