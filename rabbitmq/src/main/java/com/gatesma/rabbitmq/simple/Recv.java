package com.gatesma.rabbitmq.simple;

/**
 * Copyright (C), 2020
 * FileName: Recv
 * Author:   Marlon
 * Email: gatesma@foxmail.com
 * Date:     2020/2/13 14:16
 * Description:
 */

import com.gatesma.rabbitmq.util.ConnectionUtils;
import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * 获取消息
 */
public class Recv {

    private static final String QUEUE_NAME = "test_simple_queue";

    public static void main(String[] args) throws IOException, TimeoutException {
        //获取连接
        Connection connection = ConnectionUtils.getConnection();

        //创建一个Channel
        Channel channel = connection.createChannel();

        //队列声明
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);

        DefaultConsumer defaultConsumer = new DefaultConsumer(channel) {
            //获取到达的消息
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String s = new String(body, "utf-8");
                System.out.println("[Recv] " + s);
            }
        };
        //监听队列
        channel.basicConsume(QUEUE_NAME, true, defaultConsumer);

    }

}
