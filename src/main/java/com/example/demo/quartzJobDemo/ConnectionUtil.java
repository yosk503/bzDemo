package com.example.demo.quartzJobDemo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionUtil {

    public Connection getConnection() {
        String jdbcUrl = "jdbc:mysql://yanglonggui.mysql.rds.aliyuncs.com:3306/bzDemo?serverTimezone=UTC&&characterEncoding=UTF-8&useSSL=false";
        String username = "yang";
        String password = "aA13689276331!";
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(jdbcUrl, username, password);
            connection.setAutoCommit(false); // 关闭自动提交
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return connection;
    }

}
