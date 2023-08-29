package com.example.demo.util.fbUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
 public class UploadFile {
    public static void main(String[] args) {
        String filePath = "D:\\Yd\\桌面\\CFS_20230705121245.tar";
        String jdbcUrl = "";
        String username = "";
        String password = "";
         try (Connection connection = DriverManager.getConnection(jdbcUrl, username, password)) {
            File file = new File(filePath);
            FileInputStream fis = new FileInputStream(file);
             PreparedStatement statement = connection.prepareStatement("update pm_attach_files pm set pm.filebody=? where 1=1");
            statement.setBlob(1, fis);
             int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("File saved successfully!");
            } else {
                System.out.println("Failed to save file.");
            }
             fis.close();
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }
}