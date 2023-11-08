package com.example.demo.checkFb;


import com.example.demo.util.excelUtil.ExcelUtils;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * 从excel获取所有的补丁编号，方便下载附件
 * @author yanglonggui
 */
public class GetPMassId {

    public static void main(String[] args) throws Exception {
      GetPMassId.getPMassId("D:\\fb\\bzDemo\\20230824投产补丁.XLSX");
    }
    /**
     * 投产整理前使用 获取要投产的excel路径
     * 然后获取补丁编号，再以分号分隔开，方便补丁下载
     */
    public static List<String> getPMassId(String fileName) throws Exception {
        FileInputStream inputStream=null;
        List<String> listReturn=new ArrayList<>();
        try{
            inputStream = new FileInputStream(fileName);
            List<List<Object>> list = ExcelUtils.getListByExcel(inputStream, fileName);
            inputStream.close();
            StringBuffer stringBuffer=new StringBuffer();
            for (int i = 1; i <list.size(); i++) {
                ArrayList<Object> arrayList= (ArrayList<Object>) list.get(i);
                stringBuffer.append(arrayList.get(2)).append(";");
                if(arrayList.get(2)!=null){
                    listReturn.add((String) arrayList.get(2));
                }
            }
            return listReturn;
        }catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }finally {
            if(inputStream != null){
                inputStream.close();
            }
        }

    }
}