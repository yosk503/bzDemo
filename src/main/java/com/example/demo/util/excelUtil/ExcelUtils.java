package com.example.demo.util.excelUtil;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class ExcelUtils {
    private final static String excel2003L =".xls";    //2003- 版本的excel
    private final static String excel2007U =".xlsx";  //2007版本

    /**
     * 获取IO流中的数据，组装成List<List<Object>>对象
     */
    public static List<List<Object>> getListByExcel(InputStream in, String fileName) throws Exception{
        List<List<Object>> list;

        //创建Excel工作薄
        Workbook work = getWorkbook(in,fileName);
        if(null == work){
            throw new Exception("创建Excel工作薄为空！");
        }
        Sheet sheet;  //页数
        Row row;  //行数
        Cell cell;  //列数

        list = new ArrayList<>();
        //遍历Excel中所有的sheet
        for (int i = 0; i < work.getNumberOfSheets(); i++) {
            sheet = work.getSheetAt(i);
            if(sheet==null){continue;}

            //遍历当前sheet中的所有行
            for (int j = sheet.getFirstRowNum(); j <= sheet.getLastRowNum(); j++) {
                row = sheet.getRow(j);
                if(row==null){continue;}

                //遍历所有的列
                List<Object> li = new ArrayList<>();
                for (int y = row.getFirstCellNum(); y < row.getLastCellNum(); y++) {
                    cell = row.getCell(y);
                    li.add(getValue(cell));
                }
                list.add(li);
            }
        }

        return list;

    }

    /**
     * 根据文件后缀，自适应上传文件的版本
     */
    public static  Workbook getWorkbook(InputStream inStr,String fileName) throws Exception{
        Workbook wb = null;
        String fileType = fileName.substring(fileName.lastIndexOf("."));
        if(excel2003L.equals(fileType.toLowerCase(Locale.ROOT))){
            wb = new HSSFWorkbook(inStr);  //2003-
        }else if(excel2007U.equals(fileType.toLowerCase(Locale.ROOT))){
            wb = new XSSFWorkbook(inStr);  //2007+
        }else{
            throw new Exception("解析的文件格式有误！");
        }
        return wb;
    }

    /**
     * 对表格中数值进行格式化
     */
    public static String getValue(Cell cell) {
        DataFormatter formatter = new DataFormatter();
        String value = formatter.formatCellValue(cell);
        if("null".endsWith(value.trim())){
            value="";
        }
        return value;
    }
}
