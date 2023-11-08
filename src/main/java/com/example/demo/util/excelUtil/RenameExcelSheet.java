package com.example.demo.util.excelUtil;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

public class RenameExcelSheet {

    /**
     * 实现文件夹下面的所有excel文件第一个sheet标签重命名
     */
    public static void main(String[] args) throws Exception {
        List<String> fileNames = new ArrayList<>();
        findFileList(new File("D:\\software\\IntelliJ IDEA 2022.1.4\\projects"),fileNames,"sheet1");
        System.out.println(fileNames.size());
        for (int i = 0; i < fileNames.size(); i++) {

        }
    }

    /**
     * sheet文件重命名
     */
    public static boolean renameExcelSheet(String url,String newName) throws Exception {
        FileInputStream file=null;
        FileOutputStream fileOut=null;
        Workbook workbook=null;
        boolean flag=false;
        try {
            file = new FileInputStream(url);
            workbook = WorkbookFactory.create(file);
            workbook.setSheetName(0, newName);
            fileOut = new FileOutputStream(url);
            workbook.write(fileOut);
            workbook.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if(file != null){
                file.close();
            }
            if (workbook != null) {
                workbook.close();
            }
            if(fileOut!=null){
                fileOut.close();
            }
        }
        return true;
    }

    /**
     * 遍历所有的文件
     */
    public static void findFileList(File file, List<String> fileNames,String newName) throws Exception {
        if (!file.exists() || !file.isDirectory()) {// 判断是否存在目录
            return;
        }
        String[] files = file.list();// 读取目录下的所有目录文件信息
        assert files != null;
        for (String s : files) {// 循环，添加文件名或回调自身
            File interFile = new File(file, s);
            String fileName=interFile.getAbsolutePath();
            BasicFileAttributes basicFileAttributes = Files.readAttributes(interFile.toPath(), BasicFileAttributes.class);
            if (basicFileAttributes.isRegularFile()) {// 如果文件
                fileNames.add(interFile.getAbsolutePath());
                System.out.println(interFile.getAbsolutePath());
                //boolean flag=renameExcelSheet(interFile.getAbsolutePath(),newName);
            } else {// 如果是目录
                findFileList(interFile, fileNames,newName);// 回调自身继续查询
            }
        }
    }
}
