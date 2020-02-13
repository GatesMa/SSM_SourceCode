package com.gatesma.rabbitmq.workfair;

import com.gatesma.rabbitmq.util.ConnectionUtils;
import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Copyright (C), 2020
 * FileName: Recv2
 * Author:   Marlon
 * Email: gatesma@foxmail.com
 * Date:     2020/2/13 18:17
 * Description:
 */
public class Recv2 {

    private static final String QUEUE_NAME = "test_work_queue";

    public static void main(String[] args) throws IOException, TimeoutException {
        //获取连接
        Connection connection = ConnectionUtils.getConnection();
        //获取Channel
        Channel channel = connection.createChannel();
        //声明队列
        channel.queueDeclare(QUEUE_NAME, true, false, false, null);
        channel.basicQos(1);
        //声明消费者
        DefaultConsumer defaultConsumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String s = new String(body, "utf-8");
                System.out.println("[Recv] [2] msg : " + s);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    System.out.println("[Recv] [2] deal done!");
                    //收到消息，应答
                    channel.basicAck(envelope.getDeliveryTag(), false);
                }
            }
        };
        Boolean autoAck = false;
        channel.basicConsume(QUEUE_NAME, autoAck, defaultConsumer);
    }

}
