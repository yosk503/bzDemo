package com.example.demo.util.excelUtil;

import java.io.FileInputStream;
import java.util.List;

public class CreateSqlByExcel {
    public static void main(String[] args) throws Exception {
        String url="D:\\fb\\产品表.xlsx";
        FileInputStream inputStream = new FileInputStream(url);
        List<List<Object>> listProduct = ExcelUtils.getListByExcel(inputStream, url);

        String url2="D:\\fb\\关系表.xlsx";
        FileInputStream inputStream2 = new FileInputStream(url2);
        List<List<Object>> listConfig = ExcelUtils.getListByExcel(inputStream, url2);

        System.out.println(listProduct);
        for (int i = 1; i < listProduct.size(); i++) {
            //厂商表
            StringBuffer stringBuffer=new StringBuffer();

        }

    }
}
