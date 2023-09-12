package com.example.demo.quartzJobDemo;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class Main {
    public static void main(String[] args) {

        try (Connection connection = new ConnectionUtil().getConnection();
             Statement statement = connection.createStatement()) {
            // 插入示例数据
            String insertData = "INSERT INTO users (name, email) VALUES " +
                    "('John Doe', 'john@example.com')," +
                    "('Jane Smith', 'jane@example.com')";
            statement.executeUpdate(insertData);
            
//            insertData = "INSERT INTO orders (order_number, user_id, status) VALUES " +
//                    "('ORD123', 1, 'Pending')," +
//                    "('ORD456', 2, 'Shipped')";
//            statement.executeUpdate(insertData);
//
//            insertData = "INSERT INTO logistics (order_id, tracking_number, status) VALUES " +
//                    "(1, 'TRK123', 'In Transit')," +
//                    "(2, 'TRK456', 'Delivered')";
//            statement.executeUpdate(insertData);
            
            System.out.println("Tables created and data inserted successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}