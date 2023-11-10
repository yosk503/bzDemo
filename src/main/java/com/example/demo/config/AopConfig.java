package com.example.demo.config;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * 使用@Before在切入点开始处切入内容
 * 使用@After在切入点结尾处切入内容
 * 使用@AfterReturning在切入点return内容之后切入内容（可以用来对处理返回值做一些加工处理）
 * 使用@Around在切入点前后切入内容，并自己控制何时执行切入点自身的内容
 * 使用@AfterThrowing用来处理当切入内容部分抛出异常之后的处理逻辑
 * Description:  使之成为切面类
 * 把切面类加入到IOC容器中
 */
@Slf4j
@Aspect
@Component
public class AopConfig {
    //线程局部的变量,解决多线程中相同变量的访问冲突问题。
    ThreadLocal<Long> startTime = new ThreadLocal<>();

    @Pointcut("execution(public * com.example.demo.controller..*(..))")
    public void aopWebLog() {
    }

    @Before("aopWebLog()")
    public void doBefore(JoinPoint joinPoint) throws Throwable {
        startTime.set(System.currentTimeMillis());
        // 接收到请求，记录请求内容
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            // 记录下请求内容
            log.info("URL : " + request.getRequestURL().toString() + ",HTTP方法 : " + request.getMethod() + ",IP地址 : "
                    + request.getRemoteAddr());
            log.info("类的方法 : " + joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName() +
                    ",参数 : " + Arrays.toString(joinPoint.getArgs()).replace("'", "").replace("\r\n"," "));

        }
    }

    @AfterReturning(pointcut = "aopWebLog()", returning = "retObject")
    public void doAfterReturning(JoinPoint joinPoint, Object retObject) throws Throwable {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        Object object = retObject;
        if (attributes != null && !(retObject instanceof Response)) {
            HttpServletResponse response = attributes.getResponse();
            object = new ResponseData(ExceptionMsg.Success_200, JSON.toJSON(retObject));
            assert response != null;
            OutputStream out = response.getOutputStream();
            byte[] json = JSONObject.toJSONString(object).getBytes(StandardCharsets.UTF_8);
            out.write(json);
            out.close();
        }
        assert attributes != null;
        if(object instanceof ResponseData&&!"200".equals(((ResponseData) object).getCode())){
            log.error("应答值 : " + object);
        }else {
            log.info("应答值 : " + object);
        }

    }

    //抛出异常后通知（After throwing advice） ： 在方法抛出异常退出时执行的通知。
    @AfterThrowing(pointcut = "aopWebLog()", throwing = "ex")
    public void addAfterThrowingLogger(JoinPoint joinPoint, Exception ex) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
    }

}
