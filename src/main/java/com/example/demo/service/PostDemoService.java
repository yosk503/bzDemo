package com.example.demo.service;

import com.example.demo.config.ResponseData;
import org.springframework.web.bind.annotation.RequestBody;

public interface PostDemoService {
    ResponseData desTest(@RequestBody String  data)throws Exception;
}
