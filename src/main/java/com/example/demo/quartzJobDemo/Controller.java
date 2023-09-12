package com.example.demo.quartzJobDemo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/test")
public class Controller {
    @RequestMapping(value = "/test2",method= RequestMethod.POST)
    public void test2() throws Exception {
        List<String> list=new ArrayList<>(Arrays.asList("FJ202308183269","FJ202308183270","FJ202308223296","FJ202308223297","FJ202308043214"));
        for (int i = 0; i < 1000; i++) {
            for (int j = 0; j < list.size(); j++) {
                long startTime=System.currentTimeMillis();
                uploadFile(list.get(j));
                long endTime=System.currentTimeMillis();
                log.info("test2执行时间："+(endTime-startTime));
                if(i==10){
                    throw new Exception("异常信息");
                }
            }
        }
    }
    @RequestMapping(value = "/test3",method= RequestMethod.POST)
    public void test3(){
        List<String> list=new ArrayList<>(Arrays.asList("FJ202308173251","FJ202308243330","FJ202308243329","FJ202308243333","FJ202308183280"));
        for (int i = 0; i < 1000; i++) {
            for (int j = 0; j < list.size(); j++) {
                long startTime=System.currentTimeMillis();
                uploadFile(list.get(j));
                long endTime=System.currentTimeMillis();
                log.info("test3执行时间："+(endTime-startTime));
            }
        }
    }
    @RequestMapping(value = "/test4",method= RequestMethod.POST)
    public void test4(){
        List<String> list=new ArrayList<>(Arrays.asList("FJ202308213290","FJ202308243321","FJ202308243322","FJ202308243325","FJ202308223294"));
        for (int i = 0; i < 1000; i++) {
            for (int j = 0; j < list.size(); j++) {
                long startTime=System.currentTimeMillis();
                uploadFile(list.get(j));
                long endTime=System.currentTimeMillis();
                log.info("test4执行时间："+(endTime-startTime));
            }
        }
    }
    @RequestMapping(value = "/test5",method= RequestMethod.POST)
    public void test5(){
        List<String> list=new ArrayList<>(Arrays.asList("FJ202308173256","FJ202308243334","FJ202308223296","FJ202308073220","FJ202307213104"));
        for (int i = 0; i < 1000; i++) {
            for (int j = 0; j < list.size(); j++) {
                long startTime=System.currentTimeMillis();
                uploadFile(list.get(j));
                long endTime=System.currentTimeMillis();
                log.info("test5执行时间："+(endTime-startTime));
            }
        }
    }


    public void  uploadFile(String fileId){
        String filePath = "D:\\fb\\test.tar";
        Connection connection = null;
        try {
            connection = new ConnectionUtil().getConnection();
            connection.setAutoCommit(false); // 关闭自动提交

            File file = new File(filePath);
            FileInputStream fis = new FileInputStream(file);
            PreparedStatement statement = connection.prepareStatement("update pm_attach_files pm set pm.filebody=? where 1=1 AND ATTACH_ID= ? ");
            statement.setBlob(1, fis);
            statement.setString(2, fileId);
            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                connection.commit(); // 提交事务
            }
            fis.close();
        } catch (SQLException | IOException e) {
            if (connection != null) {
                try {
                    connection.rollback(); // 回滚事务
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
        } finally {
            if (connection != null) {
                try {
                    connection.setAutoCommit(true); // 恢复自动提交
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
