package com.example.demo.checkFb;

import com.example.demo.pmass.entity.PmPatchReg;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.ObjectUtils;

import java.io.File;
import java.util.*;

import static java.util.stream.Collectors.toList;

public class FbUtil {

    public static Map<String,String> map=new HashMap<>();

    static {
        map.put("登记", "01");
        map.put("已发测试环境", "02");
        map.put("待发版", "05");
        map.put("验证完毕", "03");
        map.put("已投产", "04");
        map.put("作废", "06");
    }

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
                if (getFileExtension(url).equals(s)) {
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

    /**
     * 校验数据库当前的状态能否发版
     */
    public static void checkState(List<PmPatchReg> entityList, String environment) throws Exception {
        String state=getMap(environment);
        LinkedList<String > linkedList = new LinkedList<>(map.values());
        for (PmPatchReg pmPatchReg : entityList) {
            String pMassState = pmPatchReg.getStat();
            String nextState = getNextValue(linkedList, pMassState);
            if (StringUtils.isNotEmpty(nextState) && !nextState.equals(state)) {
                throw new Exception("补丁编号为"+pmPatchReg.getPatchCode()+"发版环境错误，补丁登记状态和所处环境不符！当前登记状态为："+getKeyByValue(pMassState)
                        +"--要更新的状态为："+getKeyByValue(state)+"---应该更新的状态为："+getKeyByValue(nextState));
            }
        }
    }

    /**
     * 根据value获取key
     */
    private static String  getKeyByValue(String targetValue) {
        for (Map.Entry<String, String> entry : map.entrySet()) {
            if (entry.getValue().equals(targetValue)) {
                return entry.getKey();
            }
        }
        return null; // 如果目标值不存在，则返回null或者其他合适的值
    }

    /**
     * 获取下一个值
     */
    private static String getNextValue(LinkedList<String> linkedList, String targetValue) {
        ListIterator<String> iterator = linkedList.listIterator();
        while (iterator.hasNext()) {
            String currentValue = iterator.next();
            if (currentValue.equals(targetValue) && iterator.hasNext()) {
                return iterator.next();
            }
        }
        return null; // 如果目标值不存在或者目标值是链表中的最后一个元素，则返回null或者其他合适的值
    }

    /**
     * 获取文件的后缀名
     */
    public static String getFileExtension(String filename) {
        if (filename.lastIndexOf(".") != -1 && filename.lastIndexOf(".") != 0) {
            return filename.substring(filename.lastIndexOf(".") + 1);
        } else {
            return "";
        }
    }



    public static String getMap(String name) throws Exception {
        if ("uat".equals(name)) {
            return map.get("验证完毕");
        } else if ("sit".equals(name)) {
            return map.get("已发测试环境");
        } else if ("product".equals(name)) {
            return map.get("已投产");
        } else {
            throw new Exception("所传参数异常！");
        }
    }
}
