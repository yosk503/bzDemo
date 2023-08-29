package com.example.demo.checkFb;

import org.springframework.util.ObjectUtils;

import java.io.File;
import java.util.Collections;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class FbUtil {
    /**
     * 截取路径
     */
    public static String getPrefix(String path) {
        int index = path.lastIndexOf("\\");
        if (index != -1) {
            return path.substring(0, index);
        }
        return path;
    }


    /**
     * 获取当前excel所在路径
     */
    public static String getExcelPath(String rootUrl,String version,String excelSuffix ) throws Exception {
        return getPrefix(rootUrl) + "\\\\" + getExcelName(rootUrl,version,excelSuffix);
    }

    /**
     * 获取投产文件名称
     */
    public static String getExcelName(String urlRoot,String version,String excelSuffix) throws Exception {
        String excelName = "";
        String[] excelType = excelSuffix.split(";");
        if ("new".equals(version)) {
            urlRoot = getPrefix(urlRoot);
        }
        String[] files = new File(urlRoot).list();
        assert files != null;
        int excelCount=0;
        for (String url : files) {
            for (String s : excelType) {
                if (url.contains(s)) {
                    excelName = url;
                    excelCount++;
                }
            }
        }
        if(excelCount>1){
            throw new Exception("当前发版根目录含有多个excel文件，请检查！");
        }
        if (ObjectUtils.isEmpty(excelName)) {
            throw new Exception("未找到投产的excel，请检查");
        }
        return excelName;
    }

    /**
     * 校验查询出来的数量是否相同
     */
    public static void checkPMassPatch(List<String> resultList,List<String> excelList) throws Exception {
        Collections.sort(resultList);
        Collections.sort(excelList);
        boolean isEqual = resultList.equals(excelList);
        if(!isEqual){
            List<String> reduce = excelList.stream().filter(item -> !resultList.contains(item)).collect(toList());
            System.out.println("数据库中不存在的补丁为：");
            reduce.forEach(System.out::println);
            throw new Exception("数据库中登记的补丁和excel中的不一致");
        }
    }

}
