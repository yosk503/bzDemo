package com.example.demo;

import com.example.demo.pmass.dao.PMassDao;
import com.example.demo.pmass.entity.pmPatchReg;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class DemoApplicationTests {
    @Autowired
    private PMassDao pMassDao;
    @Test
    public void test1() {
        List<pmPatchReg> list=pMassDao.queryAllByPatchCode("CFSC202307240012");
        System.out.println(list);
    }



}
