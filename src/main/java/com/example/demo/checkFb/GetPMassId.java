package com.example.demo.checkFb;



import com.example.demo.util.ExcelUtils;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * 从excel获取所有的补丁编号，方便下载附件
 * @author yanglonggui
 */
public class GetPMassId {

    /**
     * 投产整理前使用 获取要投产的excel路径
     * 然后获取补丁编号，再以分号分隔开，方便补丁下载
     */
    public static void main(String[] args) throws Exception {
        String fileName = "D:\\logs\\shiro\\20230706投产补丁.XLSX";
        FileInputStream inputStream=null;
        try{
            inputStream = new FileInputStream(fileName);
            List<List<Object>> list = ExcelUtils.getListByExcel(inputStream, fileName);
            inputStream.close();
            StringBuffer stringBuffer=new StringBuffer();
            for (int i = 1; i <list.size()-1; i++) {
                ArrayList<Object> arrayList= (ArrayList<Object>) list.get(i);
                stringBuffer.append(arrayList.get(2)).append(";");
            }
            System.out.println(stringBuffer);
        }catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }finally {
            if(inputStream != null){
                inputStream.close();
            }
        }

    }
}