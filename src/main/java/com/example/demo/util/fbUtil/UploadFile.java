package com.example.demo.util.fbUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class UploadFile {
     public static void main(String[] args) {
         String filePath = "D:\\Yd\\桌面\\CFS_20230705121245.tar";
         String jdbcUrl = "";
         String username = "";
         String password = "";


         Connection connection = null;
         try {
             connection = DriverManager.getConnection(jdbcUrl, username, password);
             connection.setAutoCommit(false); // 关闭自动提交

             File file = new File(filePath);
             FileInputStream fis = new FileInputStream(file);
             PreparedStatement statement = connection.prepareStatement("update pm_attach_files pm set pm.filebody=? where 1=1");
             statement.setBlob(1, fis);
             int rowsAffected = statement.executeUpdate();

             if (rowsAffected > 0) {
                 System.out.println("File saved successfully!");
                 connection.commit(); // 提交事务
             } else {
                 System.out.println("Failed to save file.");
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