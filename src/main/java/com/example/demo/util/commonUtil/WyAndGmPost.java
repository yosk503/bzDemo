package com.example.demo.util.commonUtil;

import com.alibaba.fastjson.JSON;
import com.example.demo.config.exceptionConfig.ExceptionMsg;
import com.example.demo.config.responConfig.ResponseData;
import net.sf.json.JSONSerializer;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * @version 1.0
 * @className:WyAndGmPost
 * @author:yanglonggui
 * @date: 2022/10/19
 */
public class WyAndGmPost {
    private int TIMEOUT_TIME = 1000 * 60 * 5;
    private String serviceAddress;
    private String resetAddress;
    private String sys;
    private String userId;
    private String password;
    private Boolean isAuth;
    private String EncryptKey;
    private String restType;

    public WyAndGmPost(String serviceAddress, String resetAddress, String sys, String userId, String password, Boolean isAuth, String EncryptKey, String restType) {
        this.serviceAddress = serviceAddress;
        this.resetAddress = resetAddress;
        this.sys = sys;
        this.userId = userId;
        this.password = password;
        this.isAuth = isAuth;
        this.EncryptKey = EncryptKey;
        this.restType = restType;
    }

    public ResponseData WyOrGmPost(Map<String, String> map) throws Exception {
        return postEncryptJson(serviceAddress, resetAddress, map);
    }

    /**
     * 用于请求柜面或者网银接口
     * 返回结果为封装一层后的结果
     */
    public ResponseData postEncryptJson(String gmServiceAddress, String resetAddress, Map<String, String> map) throws Exception {
        String result;
        HttpURLConnection connection = null;
        BufferedInputStream bis = null;
        try {
            URL url = new URL(gmServiceAddress + resetAddress);
            connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setUseCaches(false);
            connection.setInstanceFollowRedirects(true);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("charset", "UTF-8");
            connection.setConnectTimeout(TIMEOUT_TIME);
            connection.setReadTimeout(TIMEOUT_TIME);
            setHeader(connection);
            sendJsonStrEncrypt(map, connection);
            bis = new BufferedInputStream(connection.getInputStream());
            ByteArrayOutputStream out = new ByteArrayOutputStream(4096);
            copy(bis, out);
            byte[] b = out.toByteArray();
            result = new String(b, "UTF-8");
            //加密传输时，解密数据
            if (isAuth) {
                result = DES3Util.decryptThreeDESECB(result, EncryptKey);
            }

        } catch (Exception e) {
            throw new Exception("接口调用异常:" + e.getMessage(), e);
        } finally {
            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException e) {
                    throw new Exception("关闭Http连接异常:" + e.getMessage(), e);
                }
            }
            if (connection != null) {
                connection.disconnect();
            }
        }
        String success = JSON.parseObject(result, Map.class).get("success").toString();
        Object obj;
        if (success.equals("true")) {
            obj = JSON.parseObject(result, Map.class).get("data");
            return new ResponseData(ExceptionMsg.Success_200,obj);
        } else {
            obj = JSON.parseObject(result, Map.class).get("errorMessage");
            return new ResponseData(ExceptionMsg.FAILED_400,obj);
        }
    }

    /**
     * post请求x-www-form-urlencoded格式，测试结果未成功
     */
    public String postFormUrlEncoded(String gmServiceAddress, String resetAddress, Map<String, Object> map) {
        String requestBody = map.toString();
        String result = " ";
        HttpURLConnection connection = null;
        try {
            URL url = new URL(gmServiceAddress + resetAddress);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestProperty("Connection", "Keep-Alive");
            // 不使用缓存
            connection.setUseCaches(false);
            connection.connect();
            PrintWriter out = new PrintWriter(new OutputStreamWriter(connection.getOutputStream(), StandardCharsets.UTF_8));
            out.print(requestBody);
            out.flush();

            int resultCode = connection.getResponseCode();
            if (HttpURLConnection.HTTP_OK == resultCode) {
                StringBuffer stringBuffer = new StringBuffer();
                String readLine;
                BufferedReader responseReader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
                while ((readLine = responseReader.readLine()) != null) {
                    stringBuffer.append(readLine).append("\n");
                }
                responseReader.close();
                result = stringBuffer.toString();
            } else {
                result = "{\"code\":\"" + resultCode + "\"}";
            }
            out.close();
        } catch (Exception e) {
            return "{\"code\":500,\"result\":\"x-www-form-urlencoded请求 " + gmServiceAddress + resetAddress + " 时出现异常\"}";
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return result;
    }

    /**
     * post请求x-www-form-urlencoded格式，测试结果成功
     */
    public static String doPost(String httpUrl, Map<String,String> param) {
        HttpURLConnection connection = null;
        InputStream is = null;
        OutputStream os = null;
        BufferedReader br = null;
        String result = null;
        try {
            URL url = new URL(httpUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setConnectTimeout(15000);
            connection.setReadTimeout(60000);
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            os = connection.getOutputStream();
            StringBuilder paramString= new StringBuilder();
            for(String key:param.keySet()){
                paramString.append(key).append("=").append(param.get(key)).append("&");
            }
            os.write(paramString.substring(0,paramString.length()-1).getBytes());
            if (connection.getResponseCode() == 200) {
                is = connection.getInputStream();
                br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                StringBuffer sbf = new StringBuffer();
                String temp = null;
                while ((temp = br.readLine()) != null) {
                    sbf.append(temp);
                    sbf.append("\r\n");
                }
                result = sbf.toString();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(connection !=null){
                // 断开与远程地址url的连接
                connection.disconnect();
            }
            // 关闭资源
            if (null != br) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (null != os) {
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (null != is) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }



   /**
     * 设置请求头 用于请求柜面或者网银
     */
    private void setHeader(HttpURLConnection connection) throws Exception {
        if (connection != null) {
            if (isAuth) {
                sys = DES3Util.encrypt(sys, EncryptKey);
                userId = DES3Util.encrypt(userId, EncryptKey);
                password = DES3Util.encrypt(password, EncryptKey);
            }
            connection.setRequestProperty("system", sys);
            connection.setRequestProperty("userId", userId);
            connection.setRequestProperty("password", password);
        }
    }

    /**
     * 流的读取
     */
    public void copy(InputStream in, OutputStream out) throws Exception {

        try {
            byte[] buffer = new byte[4096];
            int nrOfBytes = -1;
            while ((nrOfBytes = in.read(buffer)) != -1) {
                out.write(buffer, 0, nrOfBytes);
            }
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
            throw new Exception("读取失败！", e);
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

    }

    /**
     * 请求柜面或者网银进行请求加密
     */
    private void sendJsonStrEncrypt(Map<String, String> map, HttpURLConnection connection) {
        if (map != null && !map.isEmpty()) {
            BufferedOutputStream bos = null;
            Object jsonObj = JSONSerializer.toJSON(map);
            try {
                bos = new BufferedOutputStream(connection.getOutputStream());
                String content = jsonObj.toString();
                //如果要加密，那么json字符串都需要加密
                if (isAuth) {
                    content = DES3Util.encrypt(content, EncryptKey);
                }
                bos.write(content.getBytes());
                bos.flush();
            } catch (Exception e) {
                throw new RuntimeException(e);
            } finally {
                if (bos != null) {
                    try {
                        bos.close();
                    } catch (IOException e) {
                        bos = null;
                    }
                }
            }
        }
    }
}
