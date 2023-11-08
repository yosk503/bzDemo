package com.example.demo.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;
@ComponentScan
@RestControllerAdvice
public class ExceptionConfig {
    /**
     * 空指针异常
     */
    @ExceptionHandler(value = NullPointerException.class)
    public Response nullErrorHandel(NullPointerException e){
        Response responseData=new Response();
        responseData.setCode("400");
        responseData.setMsg(e.getMessage());
        e.printStackTrace();
        return responseData;
    }

    /**
     * 未定义的所有异常
     */
    @ResponseBody
    @ExceptionHandler(value=Exception.class)
    public Response defaultErrorHandler(Exception e){
        Response responseData=new Response();
        responseData.setCode("400");
        responseData.setMsg(e.getMessage());
        e.printStackTrace();
        return responseData;
    }


}
