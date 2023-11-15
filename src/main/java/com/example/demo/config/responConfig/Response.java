package com.example.demo.config.responConfig;

import com.example.demo.config.exceptionConfig.ExceptionMsg;

public class Response {
    private String msg="成功！";
    private String code="0000";

    public Response(ExceptionMsg msg){
        this.code=msg.getCode();
        this.msg=msg.getMsg();
    }


    public Response() {

    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
    @Override
    public String toString() {
        return "Response{" +
                "code='" + code + '\'' +
                ", msg='" + msg + '\'' +
                '}';
    }
}
