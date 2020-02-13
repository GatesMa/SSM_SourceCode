package com.gatesma.rabbitmq.simple;

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
 * Date:     2020/2/13 12:17
 * Description:
 */
public class Send {

    private static final String QUEUE_NAME = "test_simple_queue";

    public static void main(String[] args) throws IOException, TimeoutException {

        //获取一个连接
        Connection connection = ConnectionUtils.getConnection();

        //从连接中获取一个通道
        Channel channel = connection.createChannel();

        //声明队列
        channel.queueDeclare(QUEUE_NAME, false, false,false, null);

        //要发送的消息
        String msg = "Hello RebbitMQ!";

        channel.basicPublish("", QUEUE_NAME, null, msg.getBytes());

        System.out.println("[Send] msg : " + msg);

        channel.close();

        connection.close();

    }

}
