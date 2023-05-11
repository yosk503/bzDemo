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
        String fileName = "D:\\\\Documents\\\\Tencent Files\\\\1793933244\\\\FileRecv\\\\服务费冻结导入.xlsx";
        FileInputStream inputStream = new FileInputStream(fileName);
        List<List<Object>> list = ExcelUtils.getListByExcel(inputStream, fileName);
        StringBuffer stringBuffer=new StringBuffer();
        for (int i = 0; i <list.size(); i++) {
            ArrayList arrayList= (ArrayList) list.get(i);
            stringBuffer.append("'");
            stringBuffer.append(arrayList.get(0)).append("',");
        }
        System.out.println(stringBuffer.toString());
    }


}