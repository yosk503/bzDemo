package com.example.demo;

import com.example.demo.checkFb.FbUtil;
import com.example.demo.checkFb.FileSearch;
import com.example.demo.checkFb.GetPMassId;
import com.example.demo.pmass.dao.PMassDao;
import com.example.demo.pmass.entity.PmPatchReg;
import com.example.demo.util.commonUtil.Application;
import com.example.demo.util.commonUtil.ConvertMapToObject;
import com.example.demo.util.fbUtil.DownloadFile;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
class DemoApplicationTests {
    @Autowired
    private PMassDao pMassDao;

    @Test
    public void downLoadFile() throws Exception {
        try {
            log.info("测试开始");
            DownloadFile downloadFile = new DownloadFile();
            String url = Application.getProperty("pmass_download_dir");
            String environment = Application.getProperty("environment");
            String version = Application.getProperty("version");
            String excelSuffix = Application.getProperty("excelSuffix");
            List<String> patchCodeList = new ArrayList<>();
            //获取需要下载的补丁编号
            //测试环境需要将补丁编号放置在pmass上，生产环境则为excel放置在配置的pmass_download_dir
            if ("uat".equals(environment) || "sit".equals(environment)) {
                patchCodeList = Arrays.asList(downloadFile.loadFileAsString().split(";"));
            } else if ("product".equals(environment)) {
                patchCodeList = GetPMassId.getPMassId(FbUtil.getExcelPath(url, version, excelSuffix));
            }
            log.info("获取到的补丁编号为：" + String.join(";", patchCodeList));
            //trim()
            patchCodeList = patchCodeList.stream().map(String::trim).filter(StringUtils::isNotEmpty).collect(Collectors.toList());
            //查询数据
            List<PmPatchReg> entityList = ConvertMapToObject.convertMapToObject(pMassDao.queryAllByPatchCode(patchCodeList), PmPatchReg.class);
            //校验查询到的数据是否正确
            FbUtil.checkPMassPatch(pMassDao.queryList(patchCodeList), patchCodeList);
            //生成附件
            downloadFile.downLoanPMassFile(entityList, environment, version, excelSuffix);
            if ("product".equals(environment)) {
                //检查依赖
                FileSearch.startCheckFile();
                //发版准备结束以后把根目录下的excel删除，防止下次发版操作出错
                File excelFile = new File(FbUtil.getExcelPath(url, version, excelSuffix));
                boolean flag = excelFile.delete();
            }
            //默认暂时不更新，若要启用，请谨慎使用
            //updateStat(environment,patchCodeList);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new Exception(e.getMessage());
        }
    }

    /**
     * 更新状态
     * sit-已发测试环境
     * uat-验证完毕
     * product-已投产
     */
    public void updateStat(String flag, List<String> patchCode) throws Exception {
        String stat = getMap(flag);
        //此处最好直接替换成自己pmass的id
        String username = System.getProperty("user.name");
        String date = new SimpleDateFormat("yyyyMMdd HHmmss").format(new Date());
        int num = pMassDao.updateStat(stat, username, date, patchCode);
        log.info("更新的数量一共为：" + num);
    }

    public String getMap(String name) throws Exception {
        Map<String, String> map = new HashMap<>();
        map.put("登记", "01");
        map.put("已发测试环境", "02");
        map.put("验证完毕", "03");
        map.put("已投产", "04");
        map.put("待发版", "05");
        map.put("作废", "06");
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
