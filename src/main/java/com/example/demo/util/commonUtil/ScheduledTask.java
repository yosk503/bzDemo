package com.example.demo.util.commonUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class ScheduledTask {

    private Logger logger= LoggerFactory.getLogger(ScheduledTask.class);

    @Scheduled(cron = "0 0/990 1-18 * * * ")
    public void testOne(){
        logger.info("每分钟执行一次");
    }

    @Scheduled(fixedDelay = 30000)
    public void testTwo(){
        logger.info("每30秒执行一次");
    }

    @Scheduled(cron = "0 0 1 * * ?")//每天凌晨1点执行
    public void initTask(){
        //执行任务
        logger.info("执行任务"+new Date());
    }

}