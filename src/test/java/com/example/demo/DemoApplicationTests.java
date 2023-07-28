package com.example.demo;

import com.example.demo.pmass.dao.PMassDao;
import com.example.demo.pmass.dao.PmAttachFilesDao;
import com.example.demo.pmass.entity.PmPatchReg;
import com.example.demo.util.commonUtil.ConvertMapToObject;
import com.example.demo.util.fbUtil.DownloadFile;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;
import java.util.List;


@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
class DemoApplicationTests {
    @Autowired
    private PMassDao pMassDao;
    @Autowired
    private PmAttachFilesDao pmAttachFilesDao;


    @Test
    public void test1() throws Exception {
        try{
            DownloadFile downloadFile=new DownloadFile();
            //获取需要下载的补丁编号
            List<String> patchCodeList = Arrays.asList(downloadFile.loadFileAsString().split(";"));
            //查询数据
            List<PmPatchReg> entityList = ConvertMapToObject.convertMapToObject(pMassDao.queryAllByPatchCode(patchCodeList), PmPatchReg.class);
            downloadFile.downLoanPMassFile(entityList);
        }catch (Exception e){
            e.printStackTrace();
            throw new Exception(e.getMessage());
        }


    }



}
