package com.gatesma.rabbitmq.workqueue;

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
 * Date:     2020/2/13 18:04
 * Description:
 */
public class Send {

    private static final String QUEUE_NAME = "test_work_queue";

    public static void main(String[] args) throws IOException, TimeoutException, InterruptedException {
        //获取连接
        Connection connection = ConnectionUtils.getConnection();
        //获取Channel
        Channel channel = connection.createChannel();
        //声明队列
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        //发送的消息
        for(int i = 0;i < 50;i++) {
            String msg = "Hello WorkQueue :" + i;
            channel.basicPublish("", QUEUE_NAME, null, msg.getBytes());
            System.out.println("[Send] msg : " + msg);
            Thread.sleep(i * 20);
        }
        channel.close();
        connection.close();
    }

}
