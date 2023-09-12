package com.example.demo.rabbitMQ;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ReceiveMessageListener {

    @RabbitListener(queues = "TestDirectQueue")
    public void receiveMessage(String message) {
        log.info("接收到消息1: " +System.currentTimeMillis());
        log.info("接收到消息1: " + message);
        // 在这里处理接收到的消息
    }
    @RabbitListener(queues = "TestDirectQueue")
    public void getMessage(String message) {
        log.info("接收到消息2: " +System.currentTimeMillis());
        log.info("接收到消息2: " + message);
        // 在这里处理接收到的消息
    }
    @RabbitListener(queues = "fanout_sms_queue")
    public void getSmsMessage(String message) {
        log.info("接收到短信消息3: " +System.currentTimeMillis());
        log.info("接收到短信消息3: " + message);
        // 在这里处理接收到的消息
    }

    @RabbitListener(queues = "fanout_email_queue")
    public void getEmailMessage(String message) {
        log.info("接收到邮件消息4: " +System.currentTimeMillis());
        log.info("接收到邮件消息4: " + message);
        // 在这里处理接收到的消息
    }
}