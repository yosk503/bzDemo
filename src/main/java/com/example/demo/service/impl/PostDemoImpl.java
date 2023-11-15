package com.example.demo.service.impl;

import com.alibaba.fastjson.JSON;
import com.example.demo.config.responConfig.ResponseData;
import com.example.demo.service.PostDemoService;
import com.example.demo.util.commonUtil.ChangeObject;
import com.example.demo.util.commonUtil.WyAndGmPost;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class PostDemoImpl implements PostDemoService {

    @Override
    public ResponseData desTest(String data) throws Exception {
        Map map = JSON.parseObject(JSON.parse(data).toString(), Map.class);
        String resetAddress= ChangeObject.objectChangeString(data, true, "resetAddress", "请求路径");
        String serviceAddress= ChangeObject.objectChangeString(data,true,"serviceAddress","请求url");
        String restType= ChangeObject.objectChangeString(data,true,"restType","请求的方法");//1-json,2-form-data
        Boolean isAuth=ChangeObject.objectChangeBoolean(data, true, "isAuth", "是否加密");
        if (isAuth) {
            ChangeObject.objectChangeString(data, true, "sys", "请求系统");
            ChangeObject.objectChangeString(data, true, "sysUserId", "系统用户Id");
            ChangeObject.objectChangeString(data, true, "sysPassWord", "系统用户密码");
            ChangeObject.objectChangeString(data, true, "EncryptKey", "密钥");
        }
        WyAndGmPost wyAndGmPost = new WyAndGmPost(serviceAddress,resetAddress,
                (String) map.get("sys"), (String) map.get("sysUserId"), (String) map.get("sysPassWord"),
                isAuth, (String) map.get("EncryptKey"),restType);
        return wyAndGmPost.WyOrGmPost((Map) map.get("data"));
    }
}
