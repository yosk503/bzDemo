package com.example.demo;

import com.example.demo.checkFb.FileSearch;
import com.example.demo.checkFb.GetPMassId;
import com.example.demo.pmass.dao.PMassDao;
import com.example.demo.pmass.dao.PmAttachFilesDao;
import com.example.demo.pmass.entity.PmPatchReg;
import com.example.demo.util.commonUtil.Application;
import com.example.demo.util.commonUtil.ConvertMapToObject;
import com.example.demo.util.fbUtil.DownloadFile;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.*;
import java.util.stream.Collectors;


@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
class DemoApplicationTests {
    @Autowired
    private PMassDao pMassDao;
    @Autowired
    private PmAttachFilesDao pmAttachFilesDao;


    @Test
    public void downLoadFile() throws Exception {
        try {
            DownloadFile downloadFile = new DownloadFile();
            String url=Application.getProperty("fb_url");
            String environment=Application.getProperty("environment");
            List<String> patchCodeList=new ArrayList<>();
            //获取需要下载的补丁编号
            if("test".equals(environment)){
                patchCodeList = Arrays.asList(downloadFile.loadFileAsString().split(";"));
            }else if("product".equals(environment)){
                String excelUrl= FileSearch.getPrefix(url)+"\\\\"+FileSearch.getExcelName(url);
                patchCodeList= GetPMassId.getPMassId(excelUrl);
            }
            //trim()
            patchCodeList = patchCodeList.stream().map(String::trim).collect(Collectors.toList());
            //查询数据
            List<PmPatchReg> entityList = ConvertMapToObject.convertMapToObject(pMassDao.queryAllByPatchCode(patchCodeList), PmPatchReg.class);
            //生成附件
            downloadFile.downLoanPMassFile(entityList, "uat");
            if("product".equals(environment)){
                //检查依赖
                FileSearch.startCheckFile();
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(e.getMessage());
        }
    }

    /**
     * 更新状态
     */
    public void updateStat(String flag,List<String> patchCode) {
        Map<String, String> map = new HashMap<>();
        map.put("登记","01");
        map.put("已发测试环境","02");
        map.put("验证完毕","03");
        map.put("已投产","04");
        map.put("待发版","05");
        map.put("作废","06");
        String stat=map.get(flag);
        int num= pMassDao.updateStat(stat,patchCode);
        System.out.println("更新的数量一共为："+num);
    }


}
