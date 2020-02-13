package com.gatesma.rabbitmq.tx;

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
    
    private static final String QUEUE_NAME = "test_queue_tx";

    public static void main(String[] args) throws IOException, TimeoutException {
        //获取连接
        Connection connection = ConnectionUtils.getConnection();
        //获取Channel
        Channel channel = connection.createChannel();
       
        //声明交换机
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);//分发
        
        //发送的消息
        String msg = "Hello TX.";

        try {
            channel.txSelect();

            channel.basicPublish("", QUEUE_NAME,null, msg.getBytes());
            int i = 1 / 0;
            channel.txCommit();
            System.out.println("[Send] msg : " + msg);
        } catch (Exception e) {
            channel.txRollback();
            System.out.println("[Send] msg RollBack");
        }

        channel.close();
        connection.close();
    }
    
}
