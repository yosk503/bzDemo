package com.example.demo.util;


import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * @version 1.0
 * @className:Application
 * @author:yanglonggui
 * @date: 2022/8/19
 */
public class Application {
    public Application(){
    }
    public static String getProperty(String name) throws Exception {
        try {
            return new String(ResourceBundle.getBundle("application").getString(name).getBytes("ISO-8859-1"),"GBK");
        } catch (MissingResourceException exception) {
           throw new Exception("系统异常，获取全局参数失败");

        }
    }
}
