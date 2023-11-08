package com.example.demo.bzHelp;

import com.example.demo.bzHelp.entity.history.*;
import com.example.demo.util.excelUtil.ExcelUtils;

import java.io.FileInputStream;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.example.demo.util.commonUtil.ConvertMapToObject.*;

public class CreateSql {

    public static int custCodeStart=0;
    public static int holderCodeStart=0;
    public static int managerCodeStart=0;
    public static int loanCodeStart=0;
    public static int itemsCodeStart=0;


    public static void main(String[] args) throws Exception {
        createInsertSql("D:\\fb\\备案信息.xlsx","DEV_CUST_BASE_INFO", new DevCustBaseInfo());
    }

    /**
     * 从excel中读取并且生成SQL
     */
    public static  <T> void createInsertSql(String filePath, String tableName, T tClass) throws Exception {
        //客户信息备案表 DEV_CUST_BASE_INFO
        FileInputStream inputStreamOne = new FileInputStream(filePath);
        List<List<Object>> listOne = ExcelUtils.getListByExcel(inputStreamOne, filePath);
        List<Map<String,Object>> list=convertData(listOne,1);
        List<?> object= convertMapToObject(list,tClass.getClass(),false);
        //此处需要对返回的object进行进一步的处理，不同的对象需要去增加custCode,生成编号，初始化状态，操作人，操作时间都需要进行更新，具体情况需要具体的处理
        object=dealWithList(object,tClass);
        for (int i = 0; i < Objects.requireNonNull(object).size(); i++) {
            String sql=generateInsertSQL(object.get(i),tableName);
            System.out.println(sql);
        }
    }

    /**
     * 生成SQL的语句
     */
    public static String generateInsertSQL(Object entity,String tableName) {
        Class<?> clazz = entity.getClass();
        StringBuilder sb = new StringBuilder();
        sb.append("INSERT INTO ");
        sb.append(tableName);
        sb.append(" (");

        Field[] fields = clazz.getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            field.setAccessible(true);
            sb.append(convertFieldName(field.getName()));
            if (i < fields.length - 1) {
                sb.append(", ");
            }
        }

        sb.append(") VALUES (");

        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            field.setAccessible(true);
            try {
                Object value = field.get(entity);
                sb.append("'");
                sb.append(value);
                sb.append("'");
                if (i < fields.length - 1) {
                    sb.append(", ");
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        sb.append(")");
        return sb.toString();
    }


    public static <T> List<?> dealWithList(List<?> list, T tClass) {
        //客户信息表
        if(tClass instanceof DevCustBaseInfo){
            dealWithDevCust((List<DevCustBaseInfo>) list);
        }else if(tClass instanceof DevHolderBaseInfo){

        }else if(tClass instanceof DevManagerBaseInfo){

        }else if(tClass instanceof DevLoanApply){

        }else if(tClass instanceof DevLoanItemsDetail){

        }
        return list;
    }

    public static String getSimpleCode(String prefix,int start,int digit){
        int num=digit-prefix.length();
        return prefix+String.format("%0"     + num + "d", start);
    }

    public static <T> void dealWithDevCust(List<DevCustBaseInfo> list){
        String nowDate = new SimpleDateFormat("yyyyMMdd HH:mm:ss").format(new Date());
        //获取开始的数字，要和之前的连上，因此需要设置全局的
        for (DevCustBaseInfo devCustBaseInfo : list) {
            devCustBaseInfo.setCustCode(getSimpleCode("C", custCodeStart, 9));
            devCustBaseInfo.setLastUpdTime(nowDate);
            devCustBaseInfo.setLastUpdOper("ADMIN");
            custCodeStart++;
        }
    }
    public static <T> void dealWithDevHolder(List<DevHolderBaseInfo> list){
        String nowDate = new SimpleDateFormat("yyyyMMdd HH:mm:ss").format(new Date());
        //获取开始的数字，要和之前的连上，因此需要设置全局的
        for (DevHolderBaseInfo devHolderBaseInfo : list) {
            devHolderBaseInfo.setCustCode(getSimpleCode("C", custCodeStart, 9));
            devHolderBaseInfo.setLastUpdTime(nowDate);
            custCodeStart++;
        }
    }

}
