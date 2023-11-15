package com.example.demo.config.responConfig;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.demo.config.exceptionConfig.ExceptionMsg;
import org.apache.commons.lang3.StringUtils;

public class ResponseData extends Response{
    private Object data;

    public ResponseData(ExceptionMsg msg, Object data){
        super(msg);
        try{
            if(data instanceof String&&StringUtils.isEmpty((String) data)){
                this.data= JSONObject.parseObject(data.toString());
            }else {
                this.data=data;
            }
        }catch (Exception e){
            try {
                this.data= JSON.parse(data.toString());
            }catch (Exception exception){
                this.data=data;
            }

        }
    }

    public ResponseData() {
        super();
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    @Override
    public String toString(){
        return "ResponseData{" +
                "data=" + data +
                "} " + super.toString();
    }

}
