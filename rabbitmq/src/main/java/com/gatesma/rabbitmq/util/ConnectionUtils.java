package com.gatesma.rabbitmq.util;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Copyright (C), 2020
 * FileName: ConnectionUtils
 * Author:   Marlon
 * Email: gatesma@foxmail.com
 * Date:     2020/2/13 12:10
 * Description:
 */
public class ConnectionUtils {

    /**
     * 获取MQ的连接
     * @return
     */
    public static Connection getConnection() throws IOException, TimeoutException {
        //定义一个连接工厂
        ConnectionFactory factory = new ConnectionFactory();

        //设置服务地址
        factory.setHost("127.0.0.1");

        //设置端口号 AMQP 5672
        factory.setPort(5672);

        //设置哪个vhost
        factory.setVirtualHost("/vhost_mmr");

        //设置用户名
        factory.setUsername("root");
        //密码
        factory.setPassword("123qwe");

        return factory.newConnection();
    }


}
