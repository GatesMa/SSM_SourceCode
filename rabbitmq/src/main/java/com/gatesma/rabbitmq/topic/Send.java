package com.gatesma.rabbitmq.topic;

import com.gatesma.rabbitmq.util.ConnectionUtils;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Copyright (C), 2020
 * FileName: Send
 * Author:   Marlon
 * Email: gatesma@foxmail.com
 * Date:     2020/2/13 19:16
 * Description:
 */
public class Send {
    
    private static final String EXCHANGE_NAME = "test_exchange_topic";

    public static void main(String[] args) throws IOException, TimeoutException {
        //获取连接
        Connection connection = ConnectionUtils.getConnection();
        //获取Channel
        Channel channel = connection.createChannel();
       
        //声明交换机
        channel.exchangeDeclare(EXCHANGE_NAME, "topic");//分发
        
        //发送的消息
        String msg = "Hello Topic.";
        String routingKey = "goods.delete";
        channel.basicPublish(EXCHANGE_NAME, routingKey, null, msg.getBytes());
        
        System.out.println("[Send] msg : " + msg);
        
        channel.close();
        connection.close();
    }
    
}
