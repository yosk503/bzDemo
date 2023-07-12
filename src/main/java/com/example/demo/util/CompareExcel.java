package com.example.demo.util;

import java.io.FileInputStream;
import java.util.List;

/**
 * 比较两个excel文件内容是否一致
 */
public class CompareExcel {
    public static void main(String[] args) throws Exception{
        String excelUrlOne="D:\\桌面\\本地全部导出文件.xlsx";
        String excelUrlTwo="D:\\桌面\\sit全部导出文件.xlsx";
        //获取第一个excel的所有内容
        FileInputStream inputStreamOne = new FileInputStream(excelUrlOne);
        List<List<Object>> listOne = ExcelUtils.getListByExcel(inputStreamOne, excelUrlOne);
        //获取第二个excel的所有内容
        FileInputStream inputStreamTwo = new FileInputStream(excelUrlTwo);
        List<List<Object>> listTwo = ExcelUtils.getListByExcel(inputStreamTwo, excelUrlTwo);
        for (int i = 0; i < listOne.size(); i++) {
            List<Object> listOneCon=listOne.get(i);
            List<Object> listTwoCon=listTwo.get(i);
            for (int j = 0; j < listOneCon.size(); j++) {
                if(!listOneCon.get(j).equals(listTwoCon.get(j))&&j!=0){
                    System.out.println("当前不相同的行数为："+i+"行,列数为："+j+"列，第一个文件的值为："+listOneCon.get(j)+"，第二个文件的值为："+listTwoCon.get(j));
                }
            }
        }


    }
}
