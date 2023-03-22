package com.example.demo.util;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.InputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static java.sql.Types.BOOLEAN;
import static java.sql.Types.NUMERIC;
import static org.apache.poi.ss.usermodel.DataValidationConstraint.ValidationType.FORMULA;
import static org.apache.xmlbeans.impl.piccolo.xml.Piccolo.STRING;

public class ExcelUtils {
    private final static String excel2003L =".xls";    //2003- 版本的excel
    private final static String excel2007U =".xlsx";  //2007版本

    /**
     * @Description：获取IO流中的数据，组装成List<List<Object>>对象
     */
    public static List<List<Object>> getListByExcel(InputStream in, String fileName) throws Exception{
        List<List<Object>> list = null;

        //创建Excel工作薄
        Workbook work = getWorkbook(in,fileName);
        if(null == work){
            throw new Exception("创建Excel工作薄为空！");
        }
        Sheet sheet = null;  //页数
        Row row = null;  //行数
        Cell cell = null;  //列数

        list = new ArrayList<List<Object>>();
        //遍历Excel中所有的sheet
        for (int i = 0; i < work.getNumberOfSheets(); i++) {
            sheet = work.getSheetAt(i);
            if(sheet==null){continue;}

            //遍历当前sheet中的所有行
            for (int j = sheet.getFirstRowNum(); j <= sheet.getLastRowNum(); j++) {
                row = sheet.getRow(j);
                if(row==null){continue;}

                //遍历所有的列
                List<Object> li = new ArrayList<Object>();
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
     * @Description： 根据文件后缀，自适应上传文件的版本
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
     * @Description： 对表格中数值进行格式化
     */
    public static String getValue(Cell cell) {
        String value = "";
        if(null==cell){
            return value;
        }
        switch (cell.getCellType()) {
            //数值型
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    //如果是date类型则 ，获取该cell的date值
                    Date date = DateUtil.getJavaDate(cell.getNumericCellValue());
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                    value = format.format(date);;
                }else {// 纯数字
                    BigDecimal big=new BigDecimal(cell.getNumericCellValue());
                    value = big.toString();
                    //解决1234.0  去掉后面的.0
                    if(null!=value&&!"".equals(value.trim())){
                        String[] item = value.split("[.]");
                        if(1<item.length&&"0".equals(item[1])){
                            value=item[0];
                        }
                    }
                }
                break;
            //字符串类型
            case STRING:
                value = cell.getStringCellValue();
                break;
            // 公式类型
            case FORMULA:
                //读公式计算值
                value = String.valueOf(cell.getNumericCellValue());
                if (value.equals("NaN")) {// 如果获取的数据值为非法值,则转换为获取字符串
                    value = cell.getStringCellValue();
                }
                break;
            // 布尔类型
            case BOOLEAN:
                value = " "+ cell.getBooleanCellValue();
                break;
            default:
                cell.setCellType(Cell.CELL_TYPE_STRING);
                cell.getStringCellValue();
                value = cell.getStringCellValue();

        }
        if("null".endsWith(value.trim())){
            value="";
        }
        return value;
    }
}
