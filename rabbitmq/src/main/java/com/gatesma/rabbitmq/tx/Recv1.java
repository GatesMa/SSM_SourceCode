package com.gatesma.rabbitmq.tx;

import com.gatesma.rabbitmq.util.ConnectionUtils;
import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Copyright (C), 2020
 * FileName: Recv1
 * Author:   Marlon
 * Email: gatesma@foxmail.com
 * Date:     2020/2/13 19:22
 * Description:
 */
public class Recv1 {

    private static final String QUEUE_NAME = "test_queue_tx";


    public static void main(String[] args) throws IOException, TimeoutException {
        //获取连接
        Connection connection = ConnectionUtils.getConnection();

        //创建一个Channel
        Channel channel = connection.createChannel();
        //创建队列
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);

        //声明消费者
        DefaultConsumer defaultConsumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String s = new String(body, "utf-8");
                System.out.println("[Recv] [1] msg : " + s);
            }
        };

        channel.basicConsume(QUEUE_NAME, true, defaultConsumer);

    }
}
