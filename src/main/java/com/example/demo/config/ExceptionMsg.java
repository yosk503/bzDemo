package com.example.demo.config;


public enum ExceptionMsg {
    Success_200("200","操作成功"),
    Success_200_1("200","登陆成功"),
    FAILED_400("400","操作失败"),
    FAILED_401("401","未授权非法请求"),
    FAILED_403("403","服务器拒绝请求"),
    FAILED_404("404","请求路径错误"),
    FAILED_405("405","参数传递错误"),
    FAILED_406("405","请先登录"),
    SECRET_API_ERROR("400","请求解密参数错误，timestamp、salt、sign等参数传递是否正确传递"),
    SECRET_API_CHECK_ERROR("400","验签失败，请确认加密方式是否正确"),
    SECRET_API_ENCRYPT_ERROR("400","加解密失败，请联系管理员检查"),
    SYSTEM_EXCEPTION_ERROR("500","系统异常，请联系管理员检查");
    private String code;
    private String msg;

    ExceptionMsg(String code, String msg){
        this.code=code;
        this.msg=msg;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
