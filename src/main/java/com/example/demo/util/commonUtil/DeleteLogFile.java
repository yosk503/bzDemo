package com.example.demo.util.commonUtil;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;

public class DeleteLogFile {


    /**
     * 删除本地日志文件，防止日志文件过大，消耗空间
     * 只需在配置文件里面配置文件的根目录（delete_file_dir），以分号分开
     * 即可实现删除多个项目的日志文件
     * 遍历根目录下的所有文件夹，只删除所有的文件
     */
    public static void main(String[] args) throws Exception {
        String[] dir=Application.getProperty("delete_file_dir").split(";");
        for (String file: dir) {
            List<String> fileNames = new ArrayList<String>();
            Map<String,Long> map=new HashMap<>();
            map.put("size",0L);
            System.out.println("开始检索文件："+file);
            DeleteLogFile.findFileList(new File(file),fileNames,map);
            System.out.println("本次检索的文件一共有："+fileNames.size()+"，总大小为"+map.get("size")+"KB");
        }
    }


    public static void findFileList(File dir, List<String> fileNames,Map<String,Long> map) throws Exception {
        if (!dir.exists() || !dir.isDirectory()) {// 判断是否存在目录
            return;
        }
        String[] files = dir.list();// 读取目录下的所有目录文件信息
        for (int i = 0; i < Objects.requireNonNull(files).length; i++) {// 循环，添加文件名或回调自身
            File file = new File(dir, files[i]);
            BasicFileAttributes basicFileAttributes = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
            if (basicFileAttributes.isRegularFile()) {// 如果文件
                fileNames.add(file.getPath());
                boolean result=file.delete();
                if (result) {
                    map.put("size",map.get("size")+ basicFileAttributes.size());
                }
            } else {// 如果是目录
                findFileList(file, fileNames,map);// 回调自身
            }
        }
    }
}
