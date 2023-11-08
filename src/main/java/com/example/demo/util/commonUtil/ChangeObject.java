package com.example.demo.util.commonUtil;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import lombok.extern.slf4j.Slf4j;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
@Slf4j
public class ChangeObject {

    /**
     * 获取String类型
     *
     * @param data
     * @param obj
     * @return
     */
    public static String objectChangeString(String data, Boolean flag, String obj, String name) throws Exception {
        JSONObject jsonObject = JSONObject.parseObject(data);
        String result = jsonObject.getString(obj) == null ? "" : jsonObject.getString(obj);
        if (flag && StringUtils.isEmpty(result)) {
            throw new Exception(name + "不能为空");
        }
        return result;
    }
    public static Boolean objectChangeBoolean(String data, Boolean flag, String obj, String name) throws Exception {

        JSONObject jsonObject = JSONObject.parseObject(data);
        Boolean result = jsonObject.getBoolean(obj) != null && jsonObject.getBoolean(obj);
        if (!flag && result) {
            throw new Exception(name + "不能为空");
        }
        return result;
    }
    public static void printMapData(Map<String, Object> map) {
        StringBuilder logMessage = new StringBuilder();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if (value instanceof List) {
                List<?> list = (List<?>) value;
                logMessage.append(key).append(":").append(Arrays.toString(list.toArray())).append(";");
            } else {
                logMessage.append(key).append(":").append(value).append(";");
            }
        }
        log.info("请求参数为："+ logMessage);
    }

}