package com.example.demo.controller;

import com.example.demo.config.ResponseData;
import com.example.demo.service.PostDemoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.transaction.Transactional;

@RestController
@RequestMapping("MostDemo")
public class PostDemoControl {
    @Autowired
    private PostDemoService postDemoService;


    @Transactional
    @RequestMapping(value = "/desTest",method= RequestMethod.POST)//测试接口不用权限
    public ResponseData desTest (@RequestBody String  data) throws Exception {
        return postDemoService.desTest(data);
    }
    @Transactional
    @RequestMapping(value = "/desTest2",method= RequestMethod.POST)//测试接口不用权限
    public String  desTest2 (@RequestBody String  data) throws Exception {
        return "你好！";
    }
}
