/**
 *
 */
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
    public static void main(String[] args) throws Exception {
        String fileName = "D:\\Documents\\Tencent Files\\1793933244\\FileRecv\\20230105投产补丁.XLSX";
        FileInputStream inputStream = new FileInputStream(fileName);
        List<List<Object>> list = ExcelUtils.getListByExcel(inputStream, fileName);
        StringBuffer stringBuffer=new StringBuffer();
        for (int i = 1; i <list.size()-1; i++) {
            ArrayList arrayList= (ArrayList) list.get(i);
            stringBuffer.append(arrayList.get(2)).append(";");
        }
        System.out.println(stringBuffer.toString());
    }
}